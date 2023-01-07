package com.stringsandtones.OrderService.external.CompanyClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/api/v1/product")
public interface ProductService {

  @PutMapping("/reduceQuantity/{productId}")
  ResponseEntity<Void> reduceQuantity(@PathVariable long productId, @RequestParam long quantity);
}
