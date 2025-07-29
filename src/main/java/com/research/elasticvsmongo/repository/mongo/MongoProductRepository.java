package com.research.elasticvsmongo.repository.mongo;

import com.research.elasticvsmongo.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoProductRepository extends MongoRepository<Product, String> {

}
