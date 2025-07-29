package com.research.elasticvsmongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@EnableElasticsearchAuditing
@EnableMongoRepositories(basePackages = "com.research.elasticvsmongo.repository.mongo")
@EnableElasticsearchRepositories(basePackages = "com.research.elasticvsmongo.repository.elastic")
@SpringBootApplication
public class ElasticVsMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticVsMongoApplication.class, args);
	}

}
