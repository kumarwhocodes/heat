package com.zerobee.heat.service;

import com.zerobee.heat.dto.FeedbackDTO;
import com.zerobee.heat.entity.Feedback;
import com.zerobee.heat.exception.ResourceNotFoundException;
import com.zerobee.heat.mapper.FeedbackMapper;
import com.zerobee.heat.repo.CustomerRepo;
import com.zerobee.heat.repo.FeedbackRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepo feedbackRepo;
    private final FeedbackMapper feedbackMapper;
    private final CustomerRepo customerRepo;

    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepo.findAll()
                .stream()
                .map(feedbackMapper::toDTO)
                .collect(Collectors.toList());
    }

    public FeedbackDTO getFeedbackById(UUID id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
        return feedbackMapper.toDTO(feedback);
    }

    @Transactional
    public FeedbackDTO addFeedback(FeedbackDTO feedbackDTO) {
        if(!customerRepo.existsById(feedbackDTO.getCustomerId()))
        {
            throw new ResourceNotFoundException("Customer with Id "+ feedbackDTO.getCustomerId()+" do not exist.");
        }
        Feedback feedback = feedbackMapper.toEntity(feedbackDTO);

        // You can add additional validation here if needed (e.g., validate customer/file presence)

        Feedback savedFeedback = feedbackRepo.save(feedback);
        return feedbackMapper.toDTO(savedFeedback);
    }
}
