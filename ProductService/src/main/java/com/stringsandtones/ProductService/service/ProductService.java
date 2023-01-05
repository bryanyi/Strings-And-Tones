package com.stringsandtones.ProductService.service;

import com.stringsandtones.ProductService.model.ProductRequest;
import com.stringsandtones.ProductService.model.ProductResponse;

public interface ProductService {
  long addProduct(ProductRequest productRequest);

  ProductResponse getProductById(long productId);
}
