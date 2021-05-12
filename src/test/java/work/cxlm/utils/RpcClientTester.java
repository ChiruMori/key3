package work.cxlm.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import work.cxlm.config.Key3Properties;
import work.cxlm.rpc.code2session.Code2SessionParam;
import work.cxlm.rpc.code2session.Code2SessionResponse;
import work.cxlm.rpc.RpcClient;

/**
 * created 2020/11/17 16:11
 *
 * @author Chiru
 */
@Slf4j
public class RpcClientTester {

    @Test
    public void getRequestTest() {
        Key3Properties properties = new Key3Properties();
        Code2SessionParam param = new Code2SessionParam(properties.getAppId(), properties.getAppSecret(), "jscode", "type");
        Code2SessionResponse res = RpcClient.getUrl(properties.getAppRequestUrl(), Code2SessionResponse.class, param);
        log.debug(res.toString());
    }
}
