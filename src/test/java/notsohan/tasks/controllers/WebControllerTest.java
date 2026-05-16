package notsohan.tasks.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import notsohan.tasks.domain.dtos.TaskDTO;
import notsohan.tasks.domain.dtos.TaskListDTO;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.services.TaskListService;
import notsohan.tasks.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@WebMvcTest(controllers = WebControllerTest.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskListService taskListService;
    @MockitoBean
    private TaskService taskService;

    private Task task, savedTask;
    private TaskDTO taskDTO, savedDTO;
    private TaskList taskList, savedTaskList;
    private TaskListDTO taskListDTO, savedTaskListDTO;
    private UUID taskListId, taskId;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskListId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        task = Task.builder()
                .title("first")
                .description("first")
                .build();
        taskDTO = TaskDTO.builder()
                .title("first")
                .description("first")
                .build();

        savedTask = Task.builder()
                .id(taskId)
                .title("first")
                .description("first")
                .build();

        savedDTO = TaskDTO.builder()
                .id(taskId)
                .title("first")
                .description("first")
                .build();

        taskList = TaskList.builder()
                .title("first")
                .description("first")
                .build();

        taskListDTO = TaskListDTO.builder()
                .title("first")
                .description("first")
                .build();

        savedTaskList = TaskList.builder()
                .id(taskListId)
                .title("first")
                .description("first")
                .build();

        savedTaskListDTO = TaskListDTO.builder()
                .id(taskListId)
                .title("first")
                .description("first")
                .build();
    }

}
