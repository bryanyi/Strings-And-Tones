package com.stringsandtones.ProductService.service;

import com.stringsandtones.ProductService.entity.Product;
import com.stringsandtones.ProductService.exception.ProductServiceException;
import com.stringsandtones.ProductService.model.ProductRequest;
import com.stringsandtones.ProductService.model.ProductResponse;
import com.stringsandtones.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImplementation implements ProductService {

  @Autowired private ProductRepository productRepository;

  @Override
  public long addProduct(ProductRequest productRequest) {
    log.info("Adding Product...");
    Product product =
        Product.builder()
            .productName(productRequest.getName())
            .quantity(productRequest.getQuantity())
            .price(productRequest.getPrice())
            .build();

    productRepository.save(product);

    log.info("Product created!");
    return product.getProductId();
  }

  @Override
  public ProductResponse getProductById(long productId) {
    log.info("Get product by productId of {}", productId);
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () ->
                    new ProductServiceException(
                        "Product with id of " + productId + " was not found!",
                        "PRODUCT_NOT_FOUND"));

    // We need convert the Product type to be of ProductResponse type
    ProductResponse productResponse = new ProductResponse();
    BeanUtils.copyProperties(product, productResponse);

    return productResponse;
  }
}
