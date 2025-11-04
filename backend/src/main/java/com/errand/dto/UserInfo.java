package com.errand.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String phone;
    private String role;
    private String studentId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
