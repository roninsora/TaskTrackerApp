package notsohan.tasks.repositories;

import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.domain.entities.TaskPriority;
import notsohan.tasks.domain.entities.TaskStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TaskRepoTests {

    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private TaskListRepo taskListRepo;

    private Task task1, task2;
    private TaskList taskList;

    @BeforeEach
    void setUp(){
        taskList = TaskList.builder()
                .title("First")
                .description("Nothing")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskListRepo.save(taskList);

        task1 = Task.builder()
                .title("1")
                .taskList(taskList)
                .priority(TaskPriority.LOW)
                .status(TaskStatus.OPEN)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        task2 = Task.builder()
                .title("2")
                .taskList(taskList)
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.CLOSED)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskRepo.save(task1);
        taskRepo.save(task2);
    }


    @Test
    public void TaskRepository_saveAll_returnSavedList(){
        Assertions.assertThat(taskRepo.findAll()).hasSize(2);
    }

    @Test
    public void TaskRepository_FindByTaskListId_ReturnedTaskNotNull(){
        List<Task> found = taskRepo.findByTaskListId(taskList.getId());

        Assertions.assertThat(found).hasSize(2);
        Assertions.assertThat(found).extracting(Task::getId).containsExactlyInAnyOrder(task1.getId(), task2.getId());
    }

    @Test
    public void TaskRepository_FindByTaskListIdAndId(){
        Optional<Task> found = taskRepo.findByTaskListIdAndId(taskList.getId(), task1.getId());

        Assertions.assertThat(found).isPresent();
        Assertions.assertThat(found.get().getId()).isEqualTo(task1.getId());
    }

    @Test
    public void TaskRepository_updatedList_returnTaskNotNull(){
        Task foundTask = taskRepo.findById(task1.getId()).get();
        foundTask.setTitle("One");
        foundTask.setDescription("1 changed to One");

        Task saved = taskRepo.save(foundTask);

        Assertions.assertThat(saved.getTitle()).isNotNull();
        Assertions.assertThat(saved.getDescription()).isNotNull();
    }

    @Test
    public void TaskRepository_deletedList_returnTaskListEmpty(){
        taskRepo.deleteById(task1.getId());
        Optional<Task> found = taskRepo.findById(task1.getId());

        Assertions.assertThat(found).isEmpty();
    }

    @Test
    public void TaskRepository_deleteByTaskListIdAndId_returnedEmpty(){
        taskRepo.deleteByTaskListIdAndId(taskList.getId(), task1.getId());
        Optional<Task> found = taskRepo.findByTaskListIdAndId(taskList.getId(), task1.getId());

        Assertions.assertThat(found).isEmpty();
    }
}
