package com.taskforge.service;

import com.taskforge.dto.TaskDtos.TaskRequest;
import com.taskforge.dto.TaskDtos.TaskResponse;
import com.taskforge.entity.Task;
import com.taskforge.entity.User;
import com.taskforge.exception.ResourceNotFoundException;
import com.taskforge.repository.TaskRepository;
import com.taskforge.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("alice").email("alice@example.com")
                .password("hashed").role(User.Role.USER).build();
    }

    @Test
    void createTask_savesTaskOwnedByCurrentUser() {
        TaskRequest request = new TaskRequest("Write report", "Q3 summary", Task.Status.TODO,
                Task.Priority.HIGH, LocalDate.of(2026, 8, 1));

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        TaskResponse response = taskService.createTask("alice", request);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(response.title()).isEqualTo("Write report");
        assertThat(response.priority()).isEqualTo(Task.Priority.HIGH);
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    void createTask_defaultsStatusAndPriority_whenNotProvided() {
        TaskRequest request = new TaskRequest("Untitled task", null, null, null, null);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.createTask("alice", request);

        assertThat(response.status()).isEqualTo(Task.Status.TODO);
        assertThat(response.priority()).isEqualTo(Task.Priority.MEDIUM);
    }

    @Test
    void getTask_throwsNotFound_whenTaskDoesNotBelongToUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask("alice", 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateTask_updatesFields_whenTaskExistsForUser() {
        Task existing = Task.builder().id(5L).title("Old title").status(Task.Status.TODO)
                .priority(Task.Priority.LOW).user(user).build();
        TaskRequest request = new TaskRequest("New title", "Updated desc", Task.Status.DONE,
                Task.Priority.HIGH, null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(5L, user)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.updateTask("alice", 5L, request);

        assertThat(response.title()).isEqualTo("New title");
        assertThat(response.status()).isEqualTo(Task.Status.DONE);
        assertThat(response.priority()).isEqualTo(Task.Priority.HIGH);
    }

    @Test
    void deleteTask_throwsNotFound_whenTaskMissing() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUser(7L, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask("alice", 7L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository, never()).delete(any());
    }

    @Test
    void getTasks_filtersByStatus_whenStatusProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserAndStatus(eq(user), eq(Task.Status.DONE), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        taskService.getTasks("alice", Task.Status.DONE, pageable);

        verify(taskRepository).findByUserAndStatus(user, Task.Status.DONE, pageable);
        verify(taskRepository, never()).findByUser(any(User.class), any(Pageable.class));
    }
}
