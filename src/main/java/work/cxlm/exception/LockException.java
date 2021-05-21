package work.cxlm.exception;

/**
 * 加锁失败
 * created 2020/11/3 23:04
 *
 * @author johnniang
 * @author cxlm
 */
public class LockException extends BadRequestException {
    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
