package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * BeanUtils 转换过程中的异常
 * created 2020/10/22 13:55
 *
 * @author johnniang
 * @author cxlm
 */
public class BeanUtilsException extends AbstractKey3Exception {

    public BeanUtilsException(String message) {
        super(message);
    }

    public BeanUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
