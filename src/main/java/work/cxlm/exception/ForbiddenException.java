package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * created 2020/10/22 15:51
 *
 * @author johnniang
 * @author cxlm
 */
public class ForbiddenException extends AbstractQfzsException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
