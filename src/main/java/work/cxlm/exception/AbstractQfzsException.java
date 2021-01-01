package work.cxlm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * @author cxlm
 * created 2020/10/19 14:29
 */
public abstract class AbstractQfzsException extends RuntimeException {

    private Object errData;

    public AbstractQfzsException(String msg) {
        super(msg);
    }

    public AbstractQfzsException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * 获取 HTTP 状态，需要在子类实现
     *
     * @return Http 状态码
     */
    @NonNull
    public abstract HttpStatus getStatus();

    @Nullable
    public Object getErrorData() {
        return errData;
    }

    /**
     * 设置详细错误数据
     */
    @NonNull
    public AbstractQfzsException setErrorData(@Nullable Object errData) {
        this.errData = errData;
        return this;
    }

}
