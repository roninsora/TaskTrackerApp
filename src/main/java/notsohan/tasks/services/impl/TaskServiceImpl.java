package notsohan.tasks.services.impl;

import jakarta.transaction.Transactional;
import notsohan.tasks.domain.entities.Task;
import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.domain.entities.TaskPriority;
import notsohan.tasks.domain.entities.TaskStatus;
import notsohan.tasks.repositories.TaskListRepo;
import notsohan.tasks.repositories.TaskRepo;
import notsohan.tasks.services.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepo taskRepo;
    private final TaskListRepo taskListRepo;

    public TaskServiceImpl(TaskRepo taskRepo, TaskListRepo taskListRepo) {
        this.taskRepo = taskRepo;
        this.taskListRepo = taskListRepo;
    }

    @Override
    public List<Task> listTask(UUID tasklistId) {
        return taskRepo.findByTaskListId(tasklistId);
    }

    @Override
    public Task createTask(UUID tasklistId, Task task) {
        if(task.getId() != null) {
            throw new IllegalArgumentException("Task ID must be null for creation. Did you mean to update?");
        }
        if(task.getTitle() == null || task.getTitle().isBlank()){
            throw new IllegalArgumentException("Task title cannot be empty.");
        }

        TaskList taskList = taskListRepo.findById(tasklistId)
                .orElseThrow(() -> new IllegalArgumentException("TaskList not found with id: " + tasklistId));

        TaskPriority taskPriority = Optional.ofNullable(task.getPriority())
                .orElse(TaskPriority.MEDIUM);

        LocalDateTime now = LocalDateTime.now();
        Task savedTask = new Task(
                null,
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                TaskStatus.OPEN,
                taskPriority,
                taskList,
                now, now
        );

        return taskRepo.save(savedTask);
    }

    @Override
    public Optional<Task> getTask(UUID tasklistId, UUID id) {
        return taskRepo.findByTaskListIdAndId(tasklistId, id);
    }

    @Override
    public Task updateTask(UUID taskListId, UUID id, Task task) {

        return taskRepo.findByTaskListIdAndId(taskListId, id).map(existingTask -> {

            Optional.ofNullable(task.getTitle()).ifPresent(existingTask::setTitle);
            Optional.ofNullable(task.getDescription()).ifPresent(existingTask::setDescription);
            Optional.ofNullable(task.getDueDate()).ifPresent(existingTask::setDueDate);
            Optional.ofNullable(task.getPriority()).ifPresent(existingTask::setPriority);
            Optional.ofNullable(task.getStatus()).ifPresent(existingTask::setStatus);

            existingTask.setUpdated(LocalDateTime.now());

            return taskRepo.save(existingTask);
        }).orElseThrow(() -> new IllegalArgumentException("Task not found!"));
    }

    @Override
    public boolean isExist(UUID id) {
        return taskRepo.existsById(id);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskListId, UUID id) {
        taskRepo.deleteByTaskListIdAndId(taskListId, id);
    }
}
