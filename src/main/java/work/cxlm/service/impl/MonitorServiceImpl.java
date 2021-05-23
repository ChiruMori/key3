package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.MultiStringCache;
import work.cxlm.event.JoiningOrBelongUpdatedEvent;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.ServiceException;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.LogType;
import work.cxlm.model.support.Key3Const;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.util.SecurityUtils;
import work.cxlm.service.MonitorService;
import work.cxlm.service.UserService;

import javax.validation.ValidationException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * created 2021/1/6 13:26
 *
 * @author Chiru
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {

    private final static String LOG_FILE_PREFIX = "spring.log";
    private final static String LOG_FILE_SUFFIX = ".gz";

    @Value("${logging.file.path}")
    private String logFileDir;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final MultiStringCache multiCache;
    private final ApplicationEventPublisher eventPublisher;

    public MonitorServiceImpl(MultiStringCache multiCache,
                              ApplicationEventPublisher eventPublisher) {
        this.multiCache = multiCache;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Date> datesHasLog() {
        Date today = new Date();
        File logPath = new File(logFileDir);
        File[] logFiles = logPath.listFiles((dir, filename) ->
                filename.startsWith(LOG_FILE_PREFIX) && filename.endsWith(LOG_FILE_SUFFIX));
        if (logFiles != null && logFiles.length != 0) {
            ArrayList<Date> res = new ArrayList<>(logFiles.length + 1);
            res.add(today);
            for (File logFile : logFiles) {
                try {
                    String[] filenameSplit = logFile.getName().split("\\.");
                    Date logFileDate = sdf.parse(filenameSplit[2]);
                    res.add(logFileDate);
                } catch (IndexOutOfBoundsException e) {
                    log.error("非法的日志文件：" + logFile, e);
                } catch (Exception e) {
                    log.error("读取日志文件列表出错", e);
                    throw new ServiceException("读取日志文件列表出错", e);
                }
            }
            return res;
        }
        return Collections.singletonList(today);
    }

    @Override
    public String getDateLog(Date targetDate) {
        User admin = SecurityContextHolder.ensureSystemAdmin();
        String targetDateString = sdf.format(targetDate);
        File logPath = new File(logFileDir);
        File[] logFiles;
        if (sdf.format(new Date()).equals(targetDateString)) {
            return readFile(new File(logFileDir + "/" + LOG_FILE_PREFIX), false);
        } else {
            // 指定日期的运行日志
            logFiles = logPath.listFiles((dir, filename) -> filename.contains(targetDateString));
        }
        if (logFiles == null || logFiles.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (File logFile : logFiles) {
            sb.append(readFile(logFile, true));
        }
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT,
                "读取了 [" + targetDateString + "] 的系统日志"));
        return sb.toString();
    }

    @Override
    public void killAllCacheData() {
        User admin = SecurityContextHolder.ensureSystemAdmin();
        // 缓存中间件
        multiCache.clear();
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：清除全部缓存"));
    }

    private void buildTokenPairToMap(Map<String, String> cacheMap, String prefix, Integer uid) {
        // AccessToken
        String accK1 = SecurityUtils.buildAccessTokenKey(uid, prefix);
        Optional<String> accessOptional = multiCache.getAny(accK1, String.class);
        if (accessOptional.isPresent()) {
            cacheMap.put(accK1, accessOptional.get());
            String accK2 = SecurityUtils.buildAccessTokenKey(accessOptional.get(), prefix);
            String accId = multiCache.getAny(accK2, String.class).orElse("");
            cacheMap.put(accK2, accId);
            // RefreshToken，只在 accessToken 存在时才有可能存在
            String refK1 = SecurityUtils.buildRefreshTokenKey(uid, prefix);
            Optional<String> refreshOptional = multiCache.getAny(refK1, String.class);
            if (refreshOptional.isPresent()) {
                cacheMap.put(refK1, refreshOptional.get());
                String refK2 = SecurityUtils.buildRefreshTokenKey(refreshOptional.get(), prefix);
                String refId = multiCache.getAny(refK2, String.class).orElse("");
                cacheMap.put(refK2, refId);
            }
        }
    }

    @Override
    public Map<String, String> getCachedData(@NonNull String type, @Nullable String val) {
        User admin = SecurityContextHolder.ensureSystemAdmin();
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT,
                "指令：查看缓存，" + type + ":" + val));
        Map<String, String> res = new HashMap<>(16);
        switch (type) {
            case "user-cache":
                int id;
                try {
                    if (null == val) {
                        throw new ValidationException("查询用户缓存时，必须指定用户 ID");
                    }
                    id = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    throw new ValidationException("用户 id 必须为纯数字形式");
                }
                // TOKEN
                buildTokenPairToMap(res, StringUtils.EMPTY, id);
                buildTokenPairToMap(res, Key3Const.ADMIN_AUTH_KEY_PREFIX, id);
                // passcode
                String passcodeCacheKey = Key3Const.ADMIN_PASSCODE_PREFIX + id;
                Optional<String> passcodeOptional = multiCache.getAny(passcodeCacheKey, String.class);
                passcodeOptional.ifPresent(s -> res.put(passcodeCacheKey, s));
                // 用户信息缓存，直接取回，不进行转化
                String userInfoCacheKey = Key3Const.USER_INFO_CACHE_PREFIX + id;
                Optional<String> userInfoOptional = multiCache.get(userInfoCacheKey);
                userInfoOptional.ifPresent(s -> res.put(userInfoCacheKey, s));
                break;
            case "system-options":
                Optional<String> optionsOptional = multiCache.getAny(Key3Const.OPTION_KEY, String.class);
                optionsOptional.ifPresent(s -> res.put(Key3Const.OPTION_KEY, s));
                break;
            case "locations":
                Optional<String> locationOptional = multiCache.getAny(Key3Const.LOCATION_KEY, String.class);
                locationOptional.ifPresent(s -> res.put(Key3Const.LOCATION_KEY, s));
                break;
            case "special":
                if (null == val) {
                    throw new ValidationException("必须指定要查询的键");
                }
                Optional<String> valOptional = multiCache.get(val);
                valOptional.ifPresent(s -> res.put(val, s));
                break;
            default:
                break;
        }
        return res;
    }

    @Override
    public void setCache(@NonNull String k, @NonNull String v) {
        Assert.notNull(k, "缓存键不能为 null");
        Assert.hasText(v, "缓存值不能为空");

        User admin = SecurityContextHolder.ensureSystemAdmin();
        multiCache.put(k, v);
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：设置缓存"));
    }

    @Override
    public void deleteCache(String k) {
        User admin = SecurityContextHolder.ensureSystemAdmin();
        multiCache.delete(k);
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：删除指定缓存"));
    }

    private String readFile(File logFile, boolean gzip) {
        InputStream is;
        try {
            if (gzip) {
                is = new GZIPInputStream(new FileInputStream(logFile));
            } else {
                is = new FileInputStream(logFile);
            }
        } catch (IOException e) {
            log.error("打开文件失败", e);
            throw new ServiceException("打开文件失败", e);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String nextLine = reader.readLine();
            while (nextLine != null) {
                sb.append(nextLine).append("\r\n");
                nextLine = reader.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            log.error("读取日志文件[{}]内容失败！", logFile, e);
        }
        return StringUtils.EMPTY;
    }
}
