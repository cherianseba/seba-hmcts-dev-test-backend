package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskRequest;
import uk.gov.hmcts.reform.dev.models.TaskResponse;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTrimsTaskFieldsAndReturnsSavedTask() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 4, 30, 10, 0);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            return task;
        });

        TaskResponse response = taskService.create(new TaskRequest(
            "  Review application  ", "  Check supporting documents  ", TaskStatus.OPEN, dueDateTime
        ));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Review application");
        assertThat(response.description()).isEqualTo("Check supporting documents");
        assertThat(response.status()).isEqualTo(TaskStatus.OPEN);
        assertThat(response.dueDateTime()).isEqualTo(dueDateTime);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateStatusPersistsTheNewStatus() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Review application");
        task.setStatus(TaskStatus.OPEN);
        task.setDueDateTime(LocalDateTime.of(2026, 4, 30, 10, 0));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse response = taskService.updateStatus(1L, TaskStatus.COMPLETED);

        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository).save(task);
    }

    @Test
    void missingTaskReturnsNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
