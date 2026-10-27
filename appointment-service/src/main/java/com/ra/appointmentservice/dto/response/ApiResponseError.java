package com.ra.appointmentservice.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseError {
    private String message;
    private int status;
    private String error;
    private LocalDateTime timestamp;
}