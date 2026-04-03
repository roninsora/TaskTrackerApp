package notsohan.tasks.repositories;

import net.bytebuddy.asm.Advice;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import org.assertj.core.api.Assert;
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
public class TaskListRepoTests {

    @Autowired
    private TaskListRepo taskListRepo;

    private TaskList taskList1, taskList2;

    @BeforeEach
    void setUp(){
        taskList1 = TaskList.builder()
                .title("First Task")
                .description("N/A")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskList2 = TaskList.builder()
                .title("Second Task")
                .description("N/A")
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        taskListRepo.save(taskList1);
        taskListRepo.save(taskList2);
    }

    @Test
    public void TaskListRepo_saveAll_returnSavedTaskList(){
        Assertions.assertThat(taskListRepo.findAll()).hasSize(2);
    }

    @Test
    public void TaskListRepository_FindById_ReturnTaskList(){

        TaskList found = taskListRepo.findById(taskList1.getId()).get();

        Assertions.assertThat(found).isNotNull();
    }

    @Test
    public void TaskListRepo_updatedList_returnTaskListNotNull(){
        TaskList foundTaskList = taskListRepo.findById(taskList1.getId()).get();
        foundTaskList.setTitle("2nd Task");
        foundTaskList.setDescription("Changing the description from N/A to this");

        TaskList tl = taskListRepo.save(foundTaskList);

        Assertions.assertThat(tl.getTitle()).isNotNull();
        Assertions.assertThat(tl.getDescription()).isNotNull();
    }

    @Test
    public void TaskListRepo_deleteList_returnTaskListNotNull(){
        taskListRepo.deleteById(taskList1.getId());
        Optional<TaskList> found = taskListRepo.findById(taskList1.getId());

        Assertions.assertThat(found).isEmpty();
    }
}
