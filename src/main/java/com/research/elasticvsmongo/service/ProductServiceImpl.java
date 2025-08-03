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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
		DB dbName = bulkDTO.getDbName();
		int bulkSize = bulkDTO.getBulkSize();
		int chunkSize = bulkDTO.getChunkSize();

		log.info(">>> Starting bulk insert [DB={}, Size={}, Chunk={}]", dbName, bulkSize, chunkSize);

		Consumer<List<Product>> batchSaver = dbName == DB.MONGO
				? mongoProductRepository::saveAll
				: elasticProductRepository::saveAll;

		List<Product> batch = new ArrayList<>(chunkSize);
		StopWatch stopWatch = new StopWatch("Bulk Insert Timer");
		stopWatch.start();

		try {
			streamProducts(bulkSize).forEach(product -> {
				batch.add(product);
				if (batch.size() == chunkSize) {
//					persistBatch(batch, dbName);
					batchSaver.accept(new ArrayList<>(batch));
					batch.clear();
				}
			});

			if (!batch.isEmpty()) {
//				persistBatch(batch, dbName);
				batchSaver.accept(batch);
			}
		} catch (Exception e) {
			log.error("Error saving batch of size {} to {}: {}", batch.size(), dbName, e.getMessage(), e);
		}

		stopWatch.stop();
		double totalTime = stopWatch.getTotalTimeSeconds();
		log.info("<<< Bulk insert completed [DB={}, TotalRecords={}, TimeTaken={} seconds]", dbName, bulkSize, totalTime);

		return totalTime;
	}

	@Override
	public double saveIndividually(BulkDTO bulkDTO) {
		DB dbName = bulkDTO.getDbName();
		int bulkSize = bulkDTO.getBulkSize();

		log.info(">>> Starting individual insert [DB={}, Size={}]", bulkDTO.getDbName(), bulkDTO.getBulkSize());

		Consumer<Product> saver = dbName == DB.MONGO
				? mongoProductRepository::save
				: elasticProductRepository::save;

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

//		IntStream.rangeClosed(1, bulkSize).forEach(i -> {
//			Product product = buildProduct(i);
//			if (dbName.equals(DB.MONGO)) {
//				mongoProductRepository.save(product);
//			} else {
//				elasticProductRepository.save(product);
//			}
//		});
		try {
			streamProducts(bulkSize).forEach(saver);
		} catch (Exception e) {
			log.error("Error saving batch of size {} to {}: {}", bulkSize, dbName, e.getMessage(), e);
		}

		stopWatch.stop();
		double totalTime = stopWatch.getTotalTimeSeconds();
		log.info("<<< Individual insert completed [DB={}, TotalRecords={}, TimeTaken={} seconds]", dbName, bulkSize, totalTime);

		return totalTime;
	}

	private Stream<Product> streamProducts(int total) {
		return IntStream.rangeClosed(1, total)
				.mapToObj(this::buildProduct);
	}

	private Product buildProduct(int i) {
		return Product.builder()
				.id(String.valueOf(i))
				.name("Product " + i)
				.category("Category " + i)
				.price(Math.random() * 100)
				.quantity((int) (Math.random() * 100))
				.createdAt(Instant.now())
				.manufacturer(
						Product.Manufacturer.builder()
								.name("Brand " + (i % 10))
								.country("Country " + (i % 5))
								.contact(
										Product.Contact.builder()
												.email("contact" + i + "@brand.com")
												.phone("8801" + String.format("%09d", i))
												.build()
								)
								.build()
				)
				.tags(List.of(
						Product.Tag.builder().label("TagA").color("Red").build(),
						Product.Tag.builder().label("TagB").color("Blue").build(),
						Product.Tag.builder().label("TagC").color("Green").build()
				))
				.build();
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
