package com.zerobee.heat.service;

import com.zerobee.heat.dto.PaymentDTO;
import com.zerobee.heat.entity.File;
import com.zerobee.heat.entity.Payment;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.FileMapper;
import com.zerobee.heat.mapper.PaymentMapper;
import com.zerobee.heat.repo.FileRepo;
import com.zerobee.heat.repo.PaymentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final PaymentMapper paymentMapper;
    private final FileRepo fileRepo;
    private final FileMapper fileMapper;

    public List<PaymentDTO> getAllPayment() {
        return paymentRepo.findAll()
                .stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(UUID id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found by id: "+id));
        return paymentMapper.toDTO(payment);
    }

    public PaymentDTO addPayment(PaymentDTO paymentDto) {
        Payment payment = paymentMapper.toEntity(paymentDto);
        if(paymentDto.getFile().getId() == null) {
            payment.setFile(null);
        } else {
            UUID fileId = paymentDto.getFile().getId();
            File file = fileRepo.findById(fileId)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found by id: "+fileId));
            payment.setFile(file);
        }
        paymentRepo.save(payment);
        return paymentMapper.toDTO(payment);
    }

    public PaymentDTO updatePayment(PaymentDTO paymentDto, UUID id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found by id: "+id));
        if (paymentDto.getAmount() != null) payment.setAmount(paymentDto.getAmount());
        if (paymentDto.getPaymentDate() != null) payment.setPaymentDate(paymentDto.getPaymentDate());
        if (paymentDto.getDueDate() != null) payment.setDueDate(paymentDto.getDueDate());
        if (paymentDto.getStatus() != null) payment.setStatus(paymentDto.getStatus());
        if (paymentDto.getMode() != null) payment.setMode(paymentDto.getMode());
        if (paymentDto.getTransactionId() != null) payment.setTransactionId(paymentDto.getTransactionId());
        if (paymentDto.getCustomerId() != null) payment.setCustomerId(paymentDto.getCustomerId());
        if (paymentDto.getFile() != null){
            File file = fileMapper.toEntity(paymentDto.getFile());
            payment.setFile(file);
        }

        return paymentMapper.toDTO(paymentRepo.save(payment));
    }

    @Transactional
    public void deletePayment(UUID id) {
        if(!paymentRepo.existsById(id))
            throw new ResourceNotFoundException("Payment not found");

        paymentRepo.deleteById(id);
    }
}