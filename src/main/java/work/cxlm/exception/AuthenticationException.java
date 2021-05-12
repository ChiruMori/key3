package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * created 2020/11/15 14:19
 *
 * @author johnniang
 * @author Chiru
 */
public class AuthenticationException extends AbstractKey3Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
