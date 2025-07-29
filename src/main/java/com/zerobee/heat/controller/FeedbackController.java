package com.zerobee.heat.controller;

import com.zerobee.heat.dto.CustomResponse;
import com.zerobee.heat.dto.FeedbackDTO;
import com.zerobee.heat.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping("/")
    public CustomResponse<List<FeedbackDTO>> getAllFeedbacks()
    {
        return new CustomResponse<>(HttpStatus.OK,"all feedbacks fetched.", feedbackService.getAllFeedbacks());
    }

    @GetMapping("/{id}")
    public CustomResponse<FeedbackDTO> getFeedbackById(@PathVariable UUID id)
    {
        return new CustomResponse<>(HttpStatus.OK,"Feedback by Id", feedbackService.getFeedbackById(id));
    }

    @PostMapping("/")
    public CustomResponse<FeedbackDTO> addFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO)
    {
        return new CustomResponse<>(HttpStatus.CREATED,"Feedback Created", feedbackService.addFeedback(feedbackDTO));
    }

}
