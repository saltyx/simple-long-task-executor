package longtimetask.executor.task;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PersistentTaskData {

    private @Id @GeneratedValue long taskId;
    private String taskUrl;
    private String taskStatus;

    private @CreatedDate LocalDateTime createdDate;
    private @LastModifiedDate LocalDateTime lastModifiedDate;
}
