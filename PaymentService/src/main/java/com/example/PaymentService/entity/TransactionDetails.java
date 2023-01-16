package com.example.PaymentService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "TRANSACTION_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDetails {
  @Id
  @SequenceGenerator(name = "transaction_id_sequence", sequenceName = "transaction_id_sequence")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_sequence")
  private long id;

  @Column(name = "ORDER_ID")
  private long orderId;

  @Column(name = "PAYMENT_MODE")
  private String paymentMode;

  @Column(name = "REFERENCE_NUMBER")
  private String referenceNumber;

  @Column(name = "PAYMENT_DATE")
  private Instant paymentDate;

  @Column(name = "PAYMENT_STATUS")
  private String paymentStatus;

  @Column(name = "PAYMENT_AMOUNT")
  private long amount;
}
