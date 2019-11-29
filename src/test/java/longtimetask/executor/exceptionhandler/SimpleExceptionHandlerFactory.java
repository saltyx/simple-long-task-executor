package longtimetask.executor.exceptionhandler;

import longtimetask.executor.extensionloader.Holder;
import longtimetask.executor.loader.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class SimpleExceptionHandlerFactory implements ExceptionHandlerFactory<SimpleExceptionHandler> {

    private Holder<SimpleExceptionHandler> cachedHolder = new Holder<>();
    private TaskRepository taskRepository;

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public SimpleExceptionHandler getExceptionHandler(URI uri) {
        if (cachedHolder.get() == null) {
            cachedHolder.set(new SimpleExceptionHandler(taskRepository));
        }
        return cachedHolder.get();
    }

}
