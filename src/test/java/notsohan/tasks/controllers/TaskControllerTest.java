package notsohan.tasks.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import notsohan.tasks.domain.dtos.TaskDTO;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.mappers.Mapper;
import notsohan.tasks.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskServiceImpl taskService;

    @MockitoBean
    private Mapper<Task, TaskDTO> taskMapper;

    private Task task, savedTask;
    private TaskDTO taskDTO, savedDTO;
    private UUID taskListId, taskId;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
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
                .description("frist")
                .build();
    }


    @Test
    public void createTask_shouldReturnTaskDTO() throws Exception {
        given(taskMapper.mapFrom(taskDTO)).willReturn(task);
        given(taskService.createTask(taskListId, task)).willReturn(savedTask);
        given(taskMapper.mapTo(savedTask)).willReturn(savedDTO);

        mockMvc.perform(post("/api/v1/task-lists/{task_list_id}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("first"));

        verify(taskService, times(1)).createTask(eq(taskListId), any(Task.class));
    }

    @Test
    public void listTask_shouldReturnTaskDTO() throws Exception {
        given(taskService.listTask(taskListId)).willReturn(List.of(savedTask));
        given(taskMapper.mapTo(savedTask)).willReturn(savedDTO);

        mockMvc.perform(get("/api/v1/task-lists/{task_list_id}/tasks", taskListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId.toString()))
                .andExpect(jsonPath("$[0].title").value("first"));

        verify(taskService, times(1)).listTask(taskListId);
    }

    @Test
    public void getTask_shouldReturnTask() throws Exception{
        given(taskService.getTask(taskListId, taskId)).willReturn(Optional.of(savedTask));
        given(taskMapper.mapTo(savedTask)).willReturn(savedDTO);

        mockMvc.perform(get("/api/v1/task-lists/{task_list_id}/tasks/{id}",taskListId, taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((taskId.toString())))
                .andExpect(jsonPath("$.title").value("first"));

        verify(taskService, times(1)).getTask(taskListId,
                taskId);
    }

    @Test
    public void getTask_whenNotExist_shouldReturnNotFound() throws Exception {
        given(taskService.getTask(taskListId, taskId)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-lists/{task_list_id}/tasks/{id}",taskListId, taskId))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTask(taskListId, taskId);
    }

    @Test
    public void updateTask_whenExist_shouldReturnUpdatedTask() throws Exception{
        given(taskMapper.mapFrom(taskDTO)).willReturn(task);
        given(taskService.updateTask(taskListId, taskId, task)).willReturn(savedTask);
        given(taskMapper.mapTo(savedTask)).willReturn(savedDTO);

        mockMvc.perform(patch("/api/v1/task-lists/{task_list_id}/tasks/{id}", taskListId, taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("first"));

        verify(taskService, times(1)).updateTask(taskListId, taskId, task);
    }

    @Test
    public void deleteTask_shouldDelete() throws Exception{
        mockMvc.perform(delete("/api/v1/task-lists/{task_list_id}/tasks/{id}", taskListId, taskId))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteTask(taskListId, taskId);
    }
}
