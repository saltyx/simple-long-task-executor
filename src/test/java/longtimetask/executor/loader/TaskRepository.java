package longtimetask.executor.loader;

import longtimetask.executor.task.PersistentTaskData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<PersistentTaskData, Long> {

    Page<PersistentTaskData> findByTaskStatusOrderByCreatedDateAsc(String taskStatus,
                                                                   Pageable pageable);

    List<PersistentTaskData> findAllByTaskStatus(String taskStatus);


}
