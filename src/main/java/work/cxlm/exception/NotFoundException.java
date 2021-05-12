package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

/**
 * created 2020/10/21 11:35
 *
 * @author cxlm
 * @author johnniang
 */
public class NotFoundException extends AbstractKey3Exception {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    @NonNull
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
