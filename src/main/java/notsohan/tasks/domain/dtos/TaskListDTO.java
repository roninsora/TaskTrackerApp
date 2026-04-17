package notsohan.tasks.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskListDTO {
    private UUID id;
    private String title;
    private String description;
    private Integer count;
    private Double progress;
    private List<TaskDTO> tasks;
}
