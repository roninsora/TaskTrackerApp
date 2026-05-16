package notsohan.tasks.services.impl;

import notsohan.tasks.domain.entities.TaskList;
import notsohan.tasks.exceptions.TaskNotFoundException;
import notsohan.tasks.repositories.TaskListRepo;
import notsohan.tasks.services.TaskListService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepo taskListRepo;

    public TaskListServiceImpl(TaskListRepo taskListRepo) {
        this.taskListRepo = taskListRepo;
    }

    @Override
    public boolean isExist(UUID taskListId) {
        return taskListRepo.existsById(taskListId);
    }

    @Override
    public List<TaskList> listTaskList() {
        return taskListRepo.findAll();
    }

    @Override
    public TaskList createTaskList(TaskList taskList) {
        if(taskList.getId() != null){
            throw new IllegalArgumentException("Already present in DB!");
        }

        if(taskList.getTitle() == null || taskList.getTitle().isBlank()){
            throw new IllegalArgumentException("Title must be present!");
        }

        LocalDateTime time = LocalDateTime.now();
        return taskListRepo.save(new TaskList(
                null,
                taskList.getTitle(),
                taskList.getDescription(),
                null,
                time,
                time
        ));
    }

    @Override
    public Optional<TaskList> getTaskList(UUID id) {
        return taskListRepo.findById(id);
    }

    @Override
    public TaskList updateTaskList(UUID id, TaskList taskList) {

        return taskListRepo.findById(id).map(existingTaskList -> {
            Optional.ofNullable(taskList.getTitle()).ifPresent(existingTaskList::setTitle);
            Optional.ofNullable(taskList.getDescription()).ifPresent(existingTaskList::setDescription);

            existingTaskList.setUpdated(LocalDateTime.now());
            return taskListRepo.save(existingTaskList);
        }).orElseThrow(() -> new TaskNotFoundException("Task list not found"));
    }

    @Override
    public void delete(TaskList taskList) {
        taskListRepo.delete(taskList);
    }
}
