package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * created 2020/10/22 16:51
 *
 * @author cxlm
 */
public class BadRequestException extends AbstractKey3Exception {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
