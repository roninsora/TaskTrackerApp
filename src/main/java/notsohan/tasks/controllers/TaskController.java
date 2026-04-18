package notsohan.tasks.controllers;

import notsohan.tasks.domain.dtos.TaskDTO;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.mappers.Mapper;
import notsohan.tasks.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task-lists/{task_list_id}")
public class TaskController {

    private final Mapper<Task, TaskDTO> taskMapper;
    private final TaskService taskService;

    public TaskController(Mapper<Task, TaskDTO> taskMapper, TaskService taskService) {
        this.taskMapper = taskMapper;
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> listTask(@PathVariable UUID task_list_id){
        List<Task> tasks = taskService.listTask(task_list_id);
        List<TaskDTO> list = tasks.stream()
                .map(taskMapper::mapTo)
                .toList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable UUID task_list_id,
                                              @RequestBody TaskDTO taskDTO){
        Task task = taskMapper.mapFrom(taskDTO);
        Task savedTask = taskService.createTask(task_list_id, task);
        return new ResponseEntity<>(taskMapper.mapTo(savedTask), HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable UUID task_list_id,
                                           @PathVariable UUID id){
        Optional<Task> found = taskService.getTask(task_list_id, id);
        return found.map(taskMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID task_list_id,
                                              @PathVariable UUID id,
                                              @RequestBody TaskDTO taskDTO){
        Task task = taskMapper.mapFrom(taskDTO);
        Task updated = taskService.updateTask(task_list_id, id, task);
        return new ResponseEntity<>(taskMapper.mapTo(updated), HttpStatus.OK);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteTask(@PathVariable UUID task_list_id,
                           @PathVariable UUID id){
        taskService.deleteTask(task_list_id, id);
    }
}
