package com.taskforge.service;

import com.taskforge.dto.TaskDtos.TaskRequest;
import com.taskforge.dto.TaskDtos.TaskResponse;
import com.taskforge.entity.Task;
import com.taskforge.entity.User;
import com.taskforge.exception.ResourceNotFoundException;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User currentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    public Page<TaskResponse> getTasks(String username, Task.Status status, Pageable pageable) {
        User user = currentUser(username);
        Page<Task> tasks = (status != null)
                ? taskRepository.findByUserAndStatus(user, status, pageable)
                : taskRepository.findByUser(user, pageable);
        return tasks.map(TaskResponse::from);
    }

    public TaskResponse getTask(String username, Long id) {
        User user = currentUser(username);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return TaskResponse.from(task);
    }

    public TaskResponse createTask(String username, TaskRequest request) {
        User user = currentUser(username);

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : Task.Status.TODO)
                .priority(request.priority() != null ? request.priority() : Task.Priority.MEDIUM)
                .dueDate(request.dueDate())
                .user(user)
                .build();

        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse updateTask(String username, Long id, TaskRequest request) {
        User user = currentUser(username);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) task.setStatus(request.status());
        if (request.priority() != null) task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        return TaskResponse.from(taskRepository.save(task));
    }

    public void deleteTask(String username, Long id) {
        User user = currentUser(username);
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
}
