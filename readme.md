### 长任务模块

#### 目的

对于一些处理时间较长，但并非是定时类型的任务，如果采用quartz类型的定时任务框架，显得过于庞大；
但是如果仅仅放到线程池处理，那么可能会出现服务重启时的任务丢失。

长任务模块是折中了以上两种方案，设计的处理轻量化和持久化的模块。

#### 结构

- Task 任务。使用方需要继承Task实现run(URI)方法，其中URI中可以通过parameter部分传递参数。Task中必须要包含两个状态
一个是submitted，已提交，一个是processing，处理中。

- Loader 任务加载器，用于加载任务到执行器中。

- TaskExecutor 执行器，用于执行任务。其中任务中包含两个内部任务，一个是加载任务的任务，用于循环从Loader中加载
状态为submitted的任务到执行器的线程池中；另一个是恢复任务的任务，如果任务被意外终止，被意外终止的任务会处于processing
状态，此任务会比对处于此状态的任务和本机正在运行的任务，如果发现此任务是在本机被意外终止的，则调用exceptionHandler恢复任务

    - ThreadPool 执行器线程池，默认实现SimpleThreadPool，可通过扩展自定义

- ExtensionLoader 分离出Dubbo SPI部分（不包含自适应SPI），并结合Spring Bean的扩展类加载器

- ExceptionHandler 异常处理。异常处理包括处理任务执行中的异常，以及任务需要恢复时的操作。ExceptionHandler的获取是通过
ExceptionHandlerFactory。ExceptionHandlerFactory作为一个可扩展类，通过读取Task的URI参数中的``exceptionHandlerFactory``，
获取扩展的实例（也就是Dubbo SPI），此项是必须的，否则无法处理任务异常，也无法恢复任务

#### 测试

测试部分实现了一个从中数据库（基于内存的H2）中处理任务的例子。测试类为``TaskExecutorTest``

需要注意的是，在`TaskExecutorTest`中有一个QUEUE，这是由于H2（基于内存）似乎不支持多线程共享数据库数据，所以无奈将其他线程的关于数据库的操作
全部转到主线程来执行。
