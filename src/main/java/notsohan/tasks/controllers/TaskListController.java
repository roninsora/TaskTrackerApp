package notsohan.tasks.controllers;

import notsohan.tasks.domain.dtos.TaskListDTO;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.mappers.Mapper;
import notsohan.tasks.services.TaskListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TaskListController {

    private final TaskListService taskListService;
    private final Mapper<TaskList, TaskListDTO> taskListMapper;

    public TaskListController(TaskListService taskListService, Mapper<TaskList, TaskListDTO> taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping("/task-lists")
    public ResponseEntity<List<TaskListDTO>> listTaskLists() {
        List<TaskList> allTasks = taskListService.listTaskList();
        List<TaskListDTO> listDTO = allTasks.stream()
                .map(taskListMapper::mapTo)
                .toList();

        return ResponseEntity.ok(listDTO);
    }

    @GetMapping("task-lists/{task_list_id}")
    public ResponseEntity<TaskListDTO> getTaskList(@PathVariable UUID task_list_id) {
        Optional<TaskList> foundList = taskListService.getTaskList(task_list_id);
        return foundList
                .map(taskListMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/task-lists")
    public ResponseEntity<TaskListDTO> createTaskList(@RequestBody TaskListDTO taskListDTO) {
        TaskList taskList = taskListMapper.mapFrom(taskListDTO);
        TaskList savedTask = taskListService.createTaskList(taskList);
        return new ResponseEntity<>(taskListMapper.mapTo(savedTask), HttpStatus.OK);
    }

    @PatchMapping("/task-lists/{task_list_id}")
    public ResponseEntity<TaskListDTO> updateTaskList(@PathVariable UUID task_list_id,
                                                      @RequestBody TaskListDTO taskListDTO) {
        if (!taskListService.isExist(task_list_id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TaskList taskList = taskListMapper.mapFrom(taskListDTO);
        TaskList updated = taskListService.updateTaskList(task_list_id, taskList);
        return new ResponseEntity<>(taskListMapper.mapTo(updated), HttpStatus.OK);
    }

    @DeleteMapping("/task-lists/{task_list_id}")
    public void deleteTaskList(@PathVariable UUID task_list_id) {
        taskListService.deleteTaskList(task_list_id);
    }
}