package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.PaymentDTO;
import com.zerobee.heat.entity.Payment;
import com.zerobee.heat.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/")
    public CustomResponse<List<PaymentDTO>> getAllPayments() {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched All Payment",
                paymentService.getAllPayment()
        );
    }

    @GetMapping("/{id}")
    public CustomResponse<PaymentDTO> getPaymentById(@PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Fetched Payment By Id: " + id,
                paymentService.getPaymentById(id)
        );
    }

    @PostMapping("/")
    public CustomResponse<PaymentDTO> addPayment(@RequestBody PaymentDTO paymentDto) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Created Payment",
                paymentService.addPayment(paymentDto)
        );
    }

    @PutMapping("/{id}")
    public CustomResponse<PaymentDTO> updatePayment(@RequestBody PaymentDTO paymentDto, @PathVariable UUID id) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Updated Payment",
                paymentService.updatePayment(paymentDto,id)
        );
    }

    @DeleteMapping("/{id}")
    public CustomResponse<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Deleted Payment",
                null
        );
    }

}