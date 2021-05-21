package work.cxlm.exception;

/**
 * JSON 转换异常
 * created: 2021/5/21 14:13
 *
 * @author Chiru
 */
public class JsonConvertException extends ServiceException {
    public JsonConvertException(String msg) {
        super(msg);
    }

    public JsonConvertException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
