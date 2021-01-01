package work.cxlm.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import work.cxlm.exception.AbstractQfzsException;
import work.cxlm.model.support.BaseResponse;
import work.cxlm.utils.JsonUtils;
import work.cxlm.utils.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录验证失败的默认处理
 * created 2020/11/19 18:50
 *
 * @author Chiru
 * @author johnniang
 */
@Slf4j
public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /** 是否为生产环境 */
    private boolean productEnv = true;

    private ObjectMapper objectMapper = JsonUtils.DEFAULT_JSON_MAPPER;

    /**
     * 根据异常的信息给出响应
     */
    @Override
    public void onFailure(HttpServletRequest request, HttpServletResponse response, AbstractQfzsException exception) throws IOException {
        log.debug("处理非法的用户请求: [{}]", ServletUtils.getRequestIp());
        log.error("异常详情：{}, status: {}, Data: {}", exception.getMessage(), exception.getStatus(), exception.getErrorData());

        BaseResponse<Object> errResponse = new BaseResponse<>();

        errResponse.setStatus(exception.getStatus().value());
        errResponse.setMsg(exception.getMessage());
        errResponse.setData(exception.getErrorData());
        // 非生产环境，记录异常调用栈
        if (!productEnv) {
            errResponse.setDevMsg(ExceptionUtils.getStackTrace(exception));
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(errResponse));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper 不能为 null");
        this.objectMapper = objectMapper;
    }

    public void setProductEnv(boolean productEnv) {
        this.productEnv = productEnv;
    }
}
