package com.research.elasticvsmongo.service;

import com.research.elasticvsmongo.dto.BulkDTO;
import com.research.elasticvsmongo.dto.DB;
import com.research.elasticvsmongo.entity.Product;
import com.research.elasticvsmongo.repository.elastic.ElasticProductRepository;
import com.research.elasticvsmongo.repository.mongo.MongoProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final MongoProductRepository mongoProductRepository;
	private final ElasticProductRepository elasticProductRepository;

	@Override
	public Product saveProduct(Product product) {
//		return mongoProductRepository.save(product);
		return elasticProductRepository.save(product);
	}

	@Override
	public List<Product> getProducts() {
		return Collections.emptyList();
	}

	@Override
	public Double saveBulkProducts(BulkDTO bulkDTO) {
		StopWatch stopWatch = new StopWatch("Bulk Insert Timer");
		stopWatch.start();

		DB dbType = bulkDTO.getDbName();
		int bulkSize = bulkDTO.getBulkSize();
		int chunkSize = bulkDTO.getChunkSize();

		log.info("--->> Starting bulk insert: DB={}, Size={}", dbType, bulkSize);

		List<Product> batch = new ArrayList<>(chunkSize);
		streamProducts(bulkSize).forEach(product -> {
			batch.add(product);
			if (batch.size() == chunkSize) {
				persistBatch(batch, dbType);
				batch.clear();
			}
		});

		if (!batch.isEmpty()) {
			persistBatch(batch, dbType);
		}

		stopWatch.stop();
		double totalTime = stopWatch.getTotalTimeSeconds();
		log.info("===>> Bulk insert completed: DB={}, TotalRecords={}, TimeTaken={} seconds", dbType, bulkSize, totalTime);
		return totalTime;
	}

	private Stream<Product> streamProducts(int total) {
		return IntStream.rangeClosed(1, total)
				.mapToObj(this::buildProduct);
	}

	private Product buildProduct(int i) {
		Product p = new Product();
		p.setId(String.valueOf(i));
		p.setName("Product " + i);
		p.setCategory("Category " + i);
		p.setPrice(Math.random() * 100);
		p.setQuantity((int) (Math.random() * 100));
		return p;
	}

	private void persistBatch(List<Product> products, DB dbType) {
		try {
			if (dbType.equals(DB.MONGO)) {
				mongoProductRepository.saveAll(products);
			} else {
				elasticProductRepository.saveAll(products);
			}
			log.debug("Saved batch of size: {}", products.size());
		} catch (Exception e) {
			log.error("Error saving batch of size {} to {}: {}", products.size(), dbType, e.getMessage(), e);
		}
	}
}
