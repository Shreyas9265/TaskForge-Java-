package com.taskforge.repository;

import com.taskforge.entity.Task;
import com.taskforge.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndStatus(User user, Task.Status status, Pageable pageable);
    Optional<Task> findByIdAndUser(Long id, User user);
}
