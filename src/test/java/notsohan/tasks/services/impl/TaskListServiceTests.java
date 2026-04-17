package notsohan.tasks.services.impl;

import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.repositories.TaskListRepo;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskListServiceImpl Unit Tests")
public class TaskListServiceTests {

    @Mock
    private TaskListRepo taskListRepo;

    @InjectMocks
    private TaskListServiceImpl taskListService;

    private TaskList taskList1, taskList2, taskList3;

    @BeforeEach
    void setUp()    {
        taskList1 = TaskList.builder()
                .id(UUID.randomUUID())
                .title("First Task")
                .description("Nothing")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskList2 = TaskList.builder()
                .title("Second Task")
                .description("Nothing too")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskList3 = TaskList.builder()
                .title("")
                .description("")
                .created(null)
                .updated(null)
                .build();
    }

    @Test
    public void isExist_shouldReturnTrue_whenExist(){
        when(taskListRepo.existsById(taskList1.getId())).thenReturn(true);

        boolean res = taskListService.isExist(taskList1.getId());

        Assertions.assertThat(res).isTrue();
        verify(taskListRepo).existsById(taskList1.getId());
    }

    @Test
    public void createTaskList_shouldReturnSavedTaskList(){
        when(taskListRepo.save(any(TaskList.class))).thenReturn(taskList2);

        TaskList saved = taskListService.createTaskList(taskList2);

        Assertions.assertThat(saved).isNotNull();
        verify(taskListRepo).save(any(TaskList.class));
    }

    @Test
    public void createTaskList_shouldThrowException_whenIdIsGiven(){
        Assertions.assertThatThrownBy(() ->
                        taskListService.createTaskList(taskList1))
                .isInstanceOf(IllegalArgumentException.class);

        verify(taskListRepo, never()).save(any());

    }

    @Test
    public void createTaskList_shouldThrowException_whenTitleIsNull(){
        Assertions.assertThatThrownBy(() ->
                        taskListService.createTaskList(taskList3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void listTaskList_shouldReturnList(){
        when(taskListRepo.findAll()).thenReturn(List.of(taskList1, taskList2));

        List<TaskList> found = taskListService.listTaskList();

        Assertions.assertThat(found.size()).isEqualTo(2);
        verify(taskListRepo).findAll();
    }


    @Test
    public void getTaskList_shouldReturnOptional(){
        when(taskListRepo.findById(taskList1.getId())).thenReturn(Optional.of(taskList1));

        Optional<TaskList> found = taskListService.getTaskList(taskList1.getId());

        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(taskList1.getId()).isEqualTo(found.get().getId());

        verify(taskListRepo).findById(taskList1.getId());
    }

    @Test
    public void updateTaskList(){
        TaskList update = new TaskList(
                null, "New Title", "" +
                "New Desc", null, LocalDateTime.now(),
                LocalDateTime.now());

        when(taskListRepo.findById(taskList1.getId())).thenReturn(Optional.of(taskList1));
        when(taskListRepo.save(any(TaskList.class))).thenAnswer(i->i.getArguments()[0]);

        TaskList updated = taskListService.updateTaskList(taskList1.getId(), update);

        Assertions.assertThat(updated.getTitle()).isEqualTo("New Title");
        Assertions.assertThat(updated.getDescription()).isEqualTo("New Desc");

        verify(taskListRepo).findById(taskList1.getId());
        verify(taskListRepo).save(any(TaskList.class));
    }

    @Test
    public void deleteTaskList_shouldDelete(){
        taskListService.deleteTaskList(taskList1.getId());
        verify(taskListRepo).deleteById(taskList1.getId());
    }
}
