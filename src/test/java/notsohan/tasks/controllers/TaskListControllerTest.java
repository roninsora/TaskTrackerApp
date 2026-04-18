package notsohan.tasks.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import notsohan.tasks.domain.dtos.TaskListDTO;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.mappers.Mapper;
import notsohan.tasks.services.TaskListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskListController.class)
@AutoConfigureMockMvc
public class TaskListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskListService taskListService;

    @MockitoBean
    private Mapper<TaskList, TaskListDTO> taskListMapper;

    private TaskList tasklist, savedList;
    private TaskListDTO taskListDTO, savedListDTO;
    private UUID taskListID;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        taskListID = UUID.randomUUID();

        tasklist = TaskList.builder()
                .title("First")
                .description("title")
                .build();

        taskListDTO = TaskListDTO.builder()
                .title("First")
                .description("title")
                .build();

        savedList = TaskList.builder()
                .id(taskListID)
                .title("First")
                .description("title")
                .build();

        savedListDTO = TaskListDTO.builder()
                .id(taskListID)
                .title("First")
                .description("title")
                .build();
    }

    @Test
    public void createTaskList_shouldReturnTaskListDTO() throws Exception{
        given(taskListMapper.mapFrom(taskListDTO)).willReturn(tasklist);
        given(taskListService.createTaskList(tasklist)).willReturn(savedList);
        given(taskListMapper.mapTo(savedList)).willReturn(savedListDTO);

        mockMvc.perform(post("/api/v1/task-lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskListDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListID.toString()))
                .andExpect(jsonPath("$.title").value("First"));

        verify(taskListService, times(1)).createTaskList(tasklist);
    }

    @Test
    public void listTasklist_shouldReturnListOfTask() throws Exception {
        given(taskListService.listTaskList()).willReturn(List.of(savedList));
        given(taskListMapper.mapTo(savedList)).willReturn(savedListDTO);

        mockMvc.perform(get("/api/v1/task-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskListID.toString()))
                .andExpect(jsonPath("$[0].title").value("First"));

        verify(taskListService, times(1)).listTaskList();
    }

    @Test
    public void getTaskList_whenExist_shouldReturnTaskList() throws Exception {
        given(taskListService.getTaskList(taskListID)).willReturn(Optional.of(savedList));
        given(taskListMapper.mapTo(savedList)).willReturn(savedListDTO);

        mockMvc.perform(get("/api/v1/task-lists/{task_list_id}", taskListID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListID.toString()))
                .andExpect(jsonPath("$.title").value("First"));

        verify(taskListService, times(1)).getTaskList(taskListID);
    }

    @Test
    public void getTaskList_whenNotExist_shouldReturnNotFound() throws Exception{
        given(taskListService.getTaskList(taskListID)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/task-lists/{task_list_id}", taskListID))
                .andExpect(status().isNotFound());

        verify(taskListService, times(1)).getTaskList(taskListID);
    }

    @Test
    void updateTaskList_WhenExists_ReturnsUpdatedTaskListDTO() throws Exception {
        given(taskListService.isExist(taskListID)).willReturn(true);
        given(taskListMapper.mapFrom(taskListDTO)).willReturn(tasklist);
        given(taskListService.updateTaskList(eq(taskListID), any(TaskList.class))).willReturn(savedList);
        given(taskListMapper.mapTo(savedList)).willReturn(savedListDTO);

        mockMvc.perform(patch("/api/v1/task-lists/{task_list_id}", taskListID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskListDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskListID.toString()))
                .andExpect(jsonPath("$.title").value("First"));

        verify(taskListService, times(1)).updateTaskList(eq(taskListID), any(TaskList.class));
    }

    @Test
    void updateTaskList_WhenNotExists_ReturnsNotFound() throws Exception {
        given(taskListService.isExist(taskListID)).willReturn(false);

        mockMvc.perform(patch("/api/v1/task-lists/{id}", taskListID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskListDTO)))
                .andExpect(status().isNotFound());

        verify(taskListService, never()).updateTaskList(any(), any());
    }

    @Test
    void deleteTaskList_DeletesTaskListAndReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/task-lists/{id}", taskListID))
                .andExpect(status().isOk());

        verify(taskListService, times(1)).deleteTaskList(taskListID);
    }
}
