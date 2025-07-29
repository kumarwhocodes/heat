package com.zerobee.heat.dto;

import com.zerobee.heat.entity.File;
import com.zerobee.heat.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private String id;
    private Double amount;
    private LocalDate paymentDate;
    private LocalDate dueDate;

    private PaymentStatus status; // PENDING, PAID, OVERDUE

    private String mode;          // CASH, CREDIT CARD, BANK TRANSFER
    private String transactionId;

    private String customerId;

    private FileDTO file;
}
