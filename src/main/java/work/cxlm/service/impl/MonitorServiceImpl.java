package work.cxlm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.event.JoiningOrBelongUpdatedEvent;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.ServiceException;
import work.cxlm.model.entity.User;
import work.cxlm.model.enums.LogType;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.service.MonitorService;
import work.cxlm.service.UserService;

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
    private final UserService userService;
    private final AbstractStringCacheStore cacheStore;
    private final ApplicationEventPublisher eventPublisher;

    public MonitorServiceImpl(UserService userService,
                              AbstractStringCacheStore cacheStore,
                              ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.cacheStore = cacheStore;
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
        User admin = SecurityContextHolder.ensureUser();
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
        User admin = SecurityContextHolder.ensureUser();
        // 缓存中间件
        cacheStore.clear();
        userService.clear();
        eventPublisher.publishEvent(new JoiningOrBelongUpdatedEvent(this));
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：清除全部缓存"));
    }

    @Override
    public Map<String, String> getAllCachedData() {
        User admin = SecurityContextHolder.ensureUser();
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：查看全部缓存"));
        return cacheStore.getAll();
    }

    @Override
    public void setCache(@NonNull String k, @NonNull String v) {
        Assert.notNull(k, "缓存键不能为 null");
        Assert.hasText(v, "缓存值不能为空");

        User admin = SecurityContextHolder.ensureUser();
        cacheStore.put(k, v);
        eventPublisher.publishEvent(new LogEvent(this, admin.getId(), LogType.EMERGENCY_KIT, "指令：设置缓存"));
    }

    @Override
    public void deleteCache(String k) {
        User admin = SecurityContextHolder.ensureUser();
        cacheStore.delete(k);
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
