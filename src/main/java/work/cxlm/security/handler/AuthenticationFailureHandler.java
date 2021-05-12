package work.cxlm.security.handler;

import work.cxlm.exception.AbstractKey3Exception;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户登录凭证验证失败后的处理接口
 * created 2020/11/19 18:44
 *
 * @author Chiru
 */
public interface AuthenticationFailureHandler {

    /**
     * 当用户登录授权信息验证失败时调用此方法
     *
     * @param response  Http 响应
     * @param request   Http 请求
     * @param exception 凭证验证失败时抛出的异常实例
     * @throws IOException      网络 IO 出错时抛出
     * @throws ServletException Servlet 层错误
     */
    void onFailure(HttpServletRequest request, HttpServletResponse response, AbstractKey3Exception exception)
            throws IOException, ServletException;
}
