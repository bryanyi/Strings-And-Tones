package com.stringsandtones.ProductService.controller;

import com.stringsandtones.ProductService.model.ProductRequest;
import com.stringsandtones.ProductService.model.ProductResponse;
import com.stringsandtones.ProductService.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

  @Autowired private ProductService productService;

  @PreAuthorize("hasAuthority('Admin')")
  @PostMapping("/add")
  public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest) {
    long productId = productService.addProduct(productRequest);
    return new ResponseEntity<>(productId, HttpStatus.CREATED);
  }

  @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
  @GetMapping("/{productId}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable long productId) {
    ProductResponse productResponse = productService.getProductById(productId);
    return new ResponseEntity<>(productResponse, HttpStatus.OK);
  }

  @PutMapping("/reduceQuantity/{productId}")
  public ResponseEntity<Void> reduceQuantity(
      @PathVariable long productId, @RequestParam long quantity) {
    productService.reduceQuantity(productId, quantity);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
