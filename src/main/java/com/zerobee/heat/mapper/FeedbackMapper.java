package com.zerobee.heat.mapper;

import com.zerobee.heat.dto.FeedbackDTO;
import com.zerobee.heat.entity.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackDTO toDTO(Feedback feedback);
    Feedback toEntity(FeedbackDTO feedbackDTO);
}
