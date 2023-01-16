package com.example.PaymentService.service;

import com.example.PaymentService.entity.TransactionDetails;
import com.example.PaymentService.model.PaymentMode;
import com.example.PaymentService.model.PaymentRequest;
import com.example.PaymentService.model.PaymentResponse;
import com.example.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImplementation implements PaymentService {

  @Autowired TransactionDetailsRepository transactionDetailsRepository;

  @Override
  public long initPayment(PaymentRequest paymentRequest) {
    log.info("Recording Payment Details: {}", paymentRequest);

    TransactionDetails transactionDetails =
        TransactionDetails.builder()
            .paymentDate(Instant.now())
            .paymentMode(paymentRequest.getPaymentMode().name())
            .paymentStatus("SUCCESS")
            .orderId(paymentRequest.getOrderId())
            .referenceNumber(paymentRequest.getReferenceNumber())
            .amount(paymentRequest.getAmount())
            .build();

    transactionDetailsRepository.save(transactionDetails);

    log.info("Transaction Completed with Id: {}", transactionDetails.getId());

    return transactionDetails.getId();
  }

  @Override
  public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
    log.info("Fetching payment details for order with id of {}", orderId);
    TransactionDetails transactionDetails = transactionDetailsRepository.findByOrderId(orderId);

    PaymentResponse paymentResponse =
        PaymentResponse.builder()
            .paymentId(transactionDetails.getId())
            .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
            .paymentDate(transactionDetails.getPaymentDate())
            .orderId(transactionDetails.getOrderId())
            .status(transactionDetails.getPaymentStatus())
            .amount(transactionDetails.getAmount())
            .build();

    return paymentResponse;
  }
}
