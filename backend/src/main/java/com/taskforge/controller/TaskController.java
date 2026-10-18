package com.taskforge.controller;

import com.taskforge.dto.TaskDtos.TaskRequest;
import com.taskforge.dto.TaskDtos.TaskResponse;
import com.taskforge.entity.Task;
import com.taskforge.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            Authentication authentication,
            @RequestParam(required = false) Task.Status status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(authentication.getName(), status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(authentication.getName(), id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            Authentication authentication, @Valid @RequestBody TaskRequest request) {
        return new ResponseEntity<>(
                taskService.createTask(authentication.getName(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication, @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        taskService.deleteTask(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
