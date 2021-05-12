package work.cxlm.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import work.cxlm.config.Key3Properties;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * created 2020/11/3 11:04
 *
 * @author cxlm
 */
@SpringBootTest
@SuppressWarnings("all")
public class CacheStoreTest {

    List<CacheStore<String, String>> stores;

    private Key3Properties key3Properties;

    @Autowired
    public void setMyFontProperties(Key3Properties key3Properties) {
        this.key3Properties = key3Properties;
    }

    @BeforeEach
    void setUp() {
        stores = new LinkedList<>();
        stores.add(new LevelCacheStore(key3Properties));
        stores.add(new RedisCacheStore(key3Properties));
        stores.add(new InMemoryCacheStore());
    }

    @Test
    void putNullValueTest() {
        String key = "test_key";

        stores.forEach(cacheStore -> {
            assertThrows(IllegalArgumentException.class, () -> cacheStore.put(key, null));
        });
    }

    @Test
    void putNullKeyTest() {
        String value = "test_value";

        stores.forEach(cacheStore -> {
            assertThrows(IllegalArgumentException.class, () -> cacheStore.put(null, value));
        });
    }

    @Test
    void getByNullKeyTest() {
        stores.forEach(cacheStore -> {
            assertThrows(IllegalArgumentException.class, () -> cacheStore.get(null));
        });
    }


    @Test
    void getNullTest() {
        String key = "test_key";

        stores.forEach(cacheStore -> {
            Optional<String> valueOptional = cacheStore.get(key);

            assertFalse(valueOptional.isPresent());
        });
    }

    @Test
    void expirationTest() throws InterruptedException {
        String key = "test_key";
        String value = "test_value";

        stores.forEach(cacheStore -> {
            cacheStore.put(key, value, 500, TimeUnit.MILLISECONDS);

            Optional<String> valueOptional = cacheStore.get(key);

            assertTrue(valueOptional.isPresent());
            assertEquals(value, valueOptional.get());
        });

        TimeUnit.SECONDS.sleep(1L);

        stores.forEach(cacheStore -> {
            Optional<String> valueOptional = cacheStore.get(key);

            assertFalse(valueOptional.isPresent());
        });
    }

    @Test
    void deleteTest() {
        String key = "test_key";
        String value = "test_value";


        stores.forEach(cacheStore -> {
            // 添加缓存
            cacheStore.put(key, value);

            // 获取
            Optional<String> valueOptional = cacheStore.get(key);

            // 断言存在
            assertTrue(valueOptional.isPresent());
            assertEquals(value, valueOptional.get());

            // 删除缓存
            cacheStore.delete(key);

            // 再次获取
            valueOptional = cacheStore.get(key);

            // 断言不存在
            assertFalse(valueOptional.isPresent());
        });
    }

}
