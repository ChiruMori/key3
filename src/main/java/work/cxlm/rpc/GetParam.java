package work.cxlm.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.lang.NonNull;
import work.cxlm.utils.JsonUtils;

import java.util.Map;

/**
 * Get 请求的参数实体
 *
 * @author cxlm
 */
public interface GetParam {

    /**
     * 将对象转化为 Get 请求直接参数的形式
     *
     * @return 转化后的参数
     * @throws JsonProcessingException 无法转化时抛出
     */
    @NonNull
    default String toParamString() throws JsonProcessingException {
        Map<?, ?> paramMap = JsonUtils.objectToMap(this);
        return RpcClient.mapToParams(paramMap);
    }
}
