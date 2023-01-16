package com.stringsandtones.OrderService.service;


import com.stringsandtones.OrderService.model.OrderRequest;
import com.stringsandtones.OrderService.model.OrderResponse;

public interface OrderService {
  long placeOrder(OrderRequest orderRequest);

  OrderResponse getOrderDetails(long orderId);
}
