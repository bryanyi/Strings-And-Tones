package com.stringsandtones.OrderService.service;


import com.stringsandtones.OrderService.model.OrderRequest;

public interface OrderService {
  long placeOrder(OrderRequest orderRequest);
}
