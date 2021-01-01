package work.cxlm.rpc.code2session;

import lombok.Data;

/**
 * auth.code2Session 接口响应实体
 * created 2020/11/17 16:07
 * <p>
 * 不遵循驼峰命名法则，后续如果重新设计，使用 Map 作为响应
 *
 * @author Chiru
 */
@Data
public class Code2SessionResponse {

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 会话密钥
     */
    private String session_key;

    /**
     * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
     */
    private String unionid;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
