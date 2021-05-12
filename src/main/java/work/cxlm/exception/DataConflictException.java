package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * 因数据冲突导致的异常
 * created 2020/12/1 22:03
 *
 * @author Chiru
 */
public class DataConflictException extends AbstractKey3Exception {
    public DataConflictException(String msg) {
        super(msg);
    }

    public DataConflictException(String msg, Throwable cause) {
        super(msg, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
