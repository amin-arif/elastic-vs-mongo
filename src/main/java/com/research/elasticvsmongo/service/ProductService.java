package com.research.elasticvsmongo.service;

import com.research.elasticvsmongo.dto.BulkDTO;
import com.research.elasticvsmongo.entity.Product;

import java.util.List;

public interface ProductService {

  	Product saveProduct(Product product);

	List<Product> getProducts();

	Double saveBulkProducts(BulkDTO bulkDTO);

	double saveIndividually(BulkDTO dto);

}
