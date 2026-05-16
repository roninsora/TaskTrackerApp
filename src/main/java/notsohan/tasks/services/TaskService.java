package notsohan.tasks.services;

import notsohan.tasks.domain.entities.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {
    List<Task> listTask(UUID tasklistId);
    Task createTask(UUID tasklistId, Task task);
    Optional<Task> getTask(UUID tasklistId, UUID id);
    Task updateTask(UUID taskListId, UUID id, Task task);
    void deleteTask(UUID taskListId, UUID id);
    boolean isExist(UUID id);
}
