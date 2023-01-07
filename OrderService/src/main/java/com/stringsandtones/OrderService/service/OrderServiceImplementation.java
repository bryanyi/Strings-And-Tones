package com.stringsandtones.OrderService.service;

import com.stringsandtones.OrderService.entity.Order;
import com.stringsandtones.OrderService.external.CompanyClients.ProductService;
import com.stringsandtones.OrderService.model.OrderRequest;
import com.stringsandtones.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImplementation implements OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private ProductService productService;

  @Override
  public long placeOrder(OrderRequest orderRequest) {
    // orderRequest needs to be mapped to the Order object
    // Call product service to check quantity before proceeding
    // Call payment service to determine if the order was successful.
    // - Should provide status like COMPLETE or CANCELLED, etc
    log.info("Placing order for {}", orderRequest);

    productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

    log.info("Product with id {} quantity successfully updated in PRODUCT-SERVICE", orderRequest.getProductId());

    Order order =
        Order.builder()
            .productId(orderRequest.getProductId())
            .amount(orderRequest.getTotalAmount())
            .orderStatus("created")
            .orderDate(Instant.now())
            .quantity(orderRequest.getQuantity())
            .build();

    order = orderRepository.save(order);
    log.info("Order with id of {} was successfully placed", order.getId());
    return order.getId();
  }
}
