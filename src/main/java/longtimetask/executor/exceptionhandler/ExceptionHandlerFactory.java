package longtimetask.executor.exceptionhandler;

import longtimetask.executor.extensionloader.SPI;

import java.net.URI;

/**
 * 异常处理器工厂
 */
@SPI
public interface ExceptionHandlerFactory<T extends ExceptionHandler> {
    T getExceptionHandler(URI uri);
}
