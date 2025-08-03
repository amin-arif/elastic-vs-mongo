package com.research.elasticvsmongo.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "products")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

	@Id
	private String id;

	private String name;

	private String category;

	private Double price;

	private Integer quantity = 0;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	private Manufacturer manufacturer;

	private List<Tag> tags;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Manufacturer {
		private String name;
		private String country;
		private Contact contact;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Contact {
		private String email;
		private String phone;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Tag {
		private String label;
		private String color;
	}

}
