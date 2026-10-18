package com.taskforge.dto;

import com.taskforge.entity.Task;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDtos {

    public record TaskRequest(
            @NotBlank String title,
            String description,
            Task.Status status,
            Task.Priority priority,
            LocalDate dueDate
    ) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            Task.Status status,
            Task.Priority priority,
            LocalDate dueDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static TaskResponse from(Task task) {
            return new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getPriority(),
                    task.getDueDate(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            );
        }
    }
}
