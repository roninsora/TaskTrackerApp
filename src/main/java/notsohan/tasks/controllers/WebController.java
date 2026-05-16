package notsohan.tasks.controllers;

import notsohan.tasks.domain.dtos.TaskListDTO;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.domain.entities.TaskStatus;
import notsohan.tasks.exceptions.TaskNotFoundException;
import notsohan.tasks.mappers.Mapper;
import notsohan.tasks.services.TaskListService;
import notsohan.tasks.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class WebController {

    private final TaskListService taskListService;
    private final TaskService taskService;
    private final Mapper<TaskList, TaskListDTO> mapper;

    public WebController(TaskListService taskListService,
                         TaskService taskService,
                         Mapper<TaskList, TaskListDTO> mapper){
        this.taskListService = taskListService;
        this.taskService = taskService;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public String index(Model model){
        List<TaskListDTO> listDTOS = taskListService.listTaskList()
                .stream()
                .map(mapper::mapTo)
                .toList();

        model.addAttribute("taskLists", listDTOS);
        model.addAttribute("newTaskList", TaskListDTO.builder().build());
        return "index";
    }

    @PostMapping("/web/task-lists")
    public String createTaskList(@ModelAttribute TaskList taskList) {
        taskListService.createTaskList(taskList);
        return "redirect:/";
    }

    @GetMapping("/web/task-lists/{taskListId}")
    public String viewTaskList(@PathVariable UUID taskListId,
                               Model model) {
        TaskList taskList = taskListService.getTaskList(taskListId)
                .orElseThrow(() -> new TaskNotFoundException("Invalid task list Id:" + taskListId));

        model.addAttribute("taskList", taskList);
        model.addAttribute("tasks", taskService.listTask(taskListId));
        model.addAttribute("newTask", new Task());
        return "taskList";
    }

    @GetMapping("/web/task-lists/{id}/edit")
    public String editTaskListForm(@PathVariable UUID id,
                                   Model model) {
        TaskList taskList = taskListService.getTaskList(id)
                .orElseThrow(() -> new TaskNotFoundException("Invalid task list Id:" + id));

        model.addAttribute("taskList", taskList);
        return "editTaskList";
    }

    @PostMapping("/web/task-lists/{id}/edit")
    public String updateTaskList(@PathVariable UUID id,
                                 @ModelAttribute TaskList updatedTaskList) {
        taskListService.updateTaskList(id, updatedTaskList);
        return "redirect:/";
    }

    @PostMapping("/web/task-lists/{task_list_id}/delete")
    public String deleteTaskList(@PathVariable UUID task_list_id) {
        TaskList taskList = taskListService.getTaskList(task_list_id).orElseThrow(() ->
                new TaskNotFoundException("Task not found with id: "+task_list_id));
        taskListService.delete(taskList);
        return "redirect:/";
    }

    @PostMapping("/web/task-lists/{taskListId}/tasks")
    public String createTask(@PathVariable UUID taskListId,
                             @ModelAttribute Task newTask) {
        taskService.createTask(taskListId, newTask);
        return "redirect:/web/task-lists/" + taskListId;
    }

    @PostMapping("/web/task-lists/{taskListId}/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable UUID taskListId,
                             @PathVariable UUID taskId) {
        taskService.deleteTask(taskListId, taskId);
        return "redirect:/web/task-lists/" + taskListId;
    }

    @PostMapping("/web/task-lists/{taskListId}/tasks/{taskId}/close")
    public String closeTask(@PathVariable UUID taskListId,
                            @PathVariable UUID taskId) {
        Task closeUpdate = new Task();
        closeUpdate.setStatus(TaskStatus.CLOSED);
        taskService.updateTask(taskListId, taskId, closeUpdate);
        return "redirect:/web/task-lists/" + taskListId;
    }

    @GetMapping("/web/task-lists/{taskListId}/tasks/{taskId}/edit")
    public String editTaskForm(@PathVariable UUID taskListId,
                               @PathVariable UUID taskId, Model model) {
        Task task = taskService.getTask(taskListId, taskId)
                .orElseThrow(() -> new TaskNotFoundException("Invalid task Id:" + taskId));

        model.addAttribute("task", task);
        model.addAttribute("taskListId", taskListId);
        return "editTask";
    }

    @PostMapping("/web/task-lists/{taskListId}/tasks/{taskId}/edit")
    public String updateTask(@PathVariable UUID taskListId,
                             @PathVariable UUID taskId,
                             @ModelAttribute Task updatedTask) {
        taskService.updateTask(taskListId, taskId, updatedTask);
        return "redirect:/web/task-lists/" + taskListId;
    }
}