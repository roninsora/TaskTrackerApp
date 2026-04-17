package notsohan.tasks.services.impl;

import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.domain.entities.TaskPriority;
import notsohan.tasks.domain.entities.TaskStatus;
import notsohan.tasks.repositories.TaskListRepo;
import notsohan.tasks.repositories.TaskRepo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService unit Tests")
public class TaskServiceTest {

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private TaskListRepo taskListRepo;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task1, task2;
    private TaskList taskList;

    @BeforeEach
    void setUp(){
        UUID id = UUID.randomUUID();
        taskList = TaskList.builder()
                .id(id)
                .title("Left")
                .description("Na")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        task1 = Task.builder()
                .title("1st")
                .description("1st")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(TaskStatus.OPEN)
                .priority(TaskPriority.LOW)
                .taskList(taskList)
                .build();

        task2 = Task.builder()
                .description("2nd")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .status(TaskStatus.CLOSED)
                .priority(TaskPriority.HIGH)
                .build();
    }

    @Test
    public void createTask_shouldReturnSaveTask(){
        Task savedTask = Task.builder()
                .id(UUID.randomUUID())
                .title(task1.getTitle())
                .description(task1.getDescription())
                .build();

        when(taskListRepo.findById(taskList.getId())).thenReturn(Optional.of(taskList));
        when(taskRepo.save(any(Task.class))).thenReturn(task1);

        Task saved = taskService.createTask(taskList.getId(), task1);

        assertNotNull(savedTask.getId());
        assertEquals(task1.getTitle(), saved.getTitle());
        assertEquals(task1.getDescription(), saved.getDescription());

        verify(taskListRepo).findById(taskList.getId());
        verify(taskRepo).save(any(Task.class));
    }

    @Test
    @DisplayName("This test should throw exception when task id is given")
    public void createTask_shouldThrowException_whenTaskIdIsNotNull(){
        UUID invalid = UUID.randomUUID();
        Task invalidTask = Task.builder()
                .id(UUID.randomUUID())
                .title("invalid")
                .description("invalid")
                .build();

        Assertions.assertThatThrownBy(() ->
                taskService.createTask(invalid, invalidTask))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskListRepo, never()).findById(any());
        verify(taskRepo, never()).save(any());
    }

    @Test
    @DisplayName("This test should throw exception when title is null")
    public void createTask_shouldThrowException_whenTitleIsNull(){
        Task saved = Task.builder()
                .title(null)
                .description("titile gone!")
                .build();
        Assertions.assertThatThrownBy(()->
                taskService.createTask(taskList.getId(), saved))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskListRepo, never()).findById(any());
        verify(taskRepo, never()).save(any());
    }

    @Test
    public void listTask_shouldReturnTaskList(){
        when(taskRepo.findByTaskListId(task1.getId())).thenReturn(List.of(task1));

        List<Task> found = taskService.listTask(task1.getId());

        Assertions.assertThat(found).hasSize(1);
        verify(taskRepo).findByTaskListId(task1.getId());
    }

    @Test
    public void getTask_shouldReturnTask(){
        when(taskRepo.findByTaskListIdAndId(taskList.getId(), task1.getId())).thenReturn(Optional.of(task1));

        Optional<Task> found = taskService.getTask(taskList.getId(), task1.getId());

        Assertions.assertThat(found).isPresent();

        verify(taskRepo).findByTaskListIdAndId(taskList.getId(), task1.getId());
    }

    @Test
    public void updateTask_shouldReturnUpdatedTask(){
        Task update = new Task(null, "New Title", "New Desc", LocalDateTime.now(), TaskStatus.CLOSED, TaskPriority.HIGH,
                null, null, null);

        when(taskRepo.findByTaskListIdAndId(taskList.getId(), task1.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(()->
                taskService.updateTask(taskList.getId(), task1.getId(), update))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskRepo, never()).save(any());
    }

    @Test
    public void deleteTask_shouldDelete(){
        taskService.deleteTask(taskList.getId(), task1.getId());

        verify(taskRepo).deleteByTaskListIdAndId(taskList.getId(), task1.getId());
    }
}
























