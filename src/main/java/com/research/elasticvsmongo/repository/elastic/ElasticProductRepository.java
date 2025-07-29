package com.research.elasticvsmongo.repository.elastic;

import com.research.elasticvsmongo.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticProductRepository extends ElasticsearchRepository<Product, String> {

}
