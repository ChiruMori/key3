package work.cxlm.exception;


import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * @author cxlm
 * created 2020/10/19 14:34
 *
 * Service 层异常：内部服务器错误（500）
 */
public class ServiceException extends AbstractKey3Exception {
    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
