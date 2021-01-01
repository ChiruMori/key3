package work.cxlm.rpc.code2session;

import lombok.AllArgsConstructor;
import lombok.Data;
import work.cxlm.rpc.GetParam;

/**
 * created 2020/11/17 16:22
 * 不遵循驼峰命名法则，后续如果重新设计，使用 Map 作为发送参数
 *
 * @author Chiru
 */
@Data
@AllArgsConstructor
@SuppressWarnings("all")
public class Code2SessionParam implements GetParam {

    private String appid;

    private String secret;

    private String js_code;

    private String grant_type;
}
