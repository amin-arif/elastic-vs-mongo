package com.research.elasticvsmongo.controller;

import com.research.elasticvsmongo.dto.BulkDTO;
import com.research.elasticvsmongo.entity.Product;
import com.research.elasticvsmongo.service.ProductService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/products")
public class ProductRestController {

	private final ProductService productService;

	@PostMapping("/save")
	public ResponseEntity<?> create(@RequestBody Product product) {
		Product savedProduct = productService.saveProduct(product);
		return ResponseEntity.ok(savedProduct);
	}

	@GetMapping("/fetch")
	public List<Product> getProducts() {
		return productService.getProducts();
	}

	@PostMapping("/bulk-save")
	public ResponseEntity<?> bulkSave(@NonNull @RequestBody BulkDTO bulkDTO) {
		Double executionTime = productService.saveBulkProducts(bulkDTO);
		return ResponseEntity.ok("Bulk save success. Execution time: " + executionTime + " s");
	}

	@PostMapping("/save-one-by-one")
	public ResponseEntity<String> saveProductsIndividually(@RequestBody BulkDTO dto) {
		double timeTaken = productService.saveIndividually(dto);
		return ResponseEntity.ok("Saved " + dto.getBulkSize() + " products individually in " + timeTaken + " seconds.");
	}

}
