package com.yuanstack.sca.service.system.assembly.monitor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_MONITOR;

/**
 * @description: 监控日志
 * @author: hansiyuan
 * @date: 2022/6/29 11:08 AM
 */
@Slf4j
public class MonitorLog {

    private static Logger appStatLog;

    private static final Map<MonitorKeys, ValueObject> appDatas = new ConcurrentHashMap<>(100);

    private static final ReentrantLock lock = new ReentrantLock();
    private static final ReentrantLock timerLock = new ReentrantLock();
    private static final Condition condition = timerLock.newCondition();

    private static String hostName = "";

    private static boolean writeLog = true;

    public static int appMaxKey = 100000;

    private static Thread writeThread = null;

    private MonitorLog() {
    }

    static {
        initLogBack();

        setHostName();

        runWriteThread();
    }

    private static void initLogBack() {
        appStatLog = build(MonitorConstants.LOGGER_NAME);
    }

    private static Logger build(String name) {
        RollingFileAppender appender = getAppender(name);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(name);

        //设置不向上级打印信息
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);

        return logger;
    }

    public static RollingFileAppender getAppender(String name) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        //这里是可以用来设置appender的，在xml配置文件里面，是这种形式：
        // <appender name="error" class="ch.qos.logback.core.rolling.RollingFileAppender">
        RollingFileAppender appender = new RollingFileAppender();

        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        appender.setContext(context);
        //appender的name属性
        appender.setName(name + "-appender");

        String fileName = MonitorConstants.LOG_FILE_PATH + MonitorConstants.APP_FILE_NAME
                + MonitorConstants.FILE_SUFFIX;
        //设置文件名
        appender.setFile(OptionHelper.substVars(fileName, context));

        appender.setAppend(true);

        appender.setPrudent(false);

        //设置文件创建时间及大小的类
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        policy.setContext(context);

        //文件名格式
        String fp = OptionHelper.substVars(
                MonitorConstants.LOG_FILE_PATH + MonitorConstants.APP_FILE_NAME + MonitorConstants.YYYY_MM_DD
                        + MonitorConstants.FILE_SUFFIX, context);
        //最大日志文件大小
        policy.setMaxFileSize(FileSize.valueOf("200MB"));
        //设置文件名模式
        policy.setFileNamePattern(fp);
        //设置最大历史记录为15条
        policy.setMaxHistory(15);
        //总大小限制
        policy.setTotalSizeCap(FileSize.valueOf("20GB"));
        //设置父节点是appender
        policy.setParent(appender);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setContext(context);
        //设置格式，因为聚合打印日志了，人工控制换行
        encoder.setPattern(MonitorConstants.M);
        encoder.start();

        //加入下面两个节点
        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    /**
     * 获取日志级别对象
     *
     * @param level
     * @return
     */
    public static LevelFilter getLevelFilter(Level level) {
        LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(level);
        levelFilter.setOnMatch(FilterReply.ACCEPT);
        levelFilter.setOnMismatch(FilterReply.DENY);
        return levelFilter;
    }

    /**
     * hostName
     */
    private static void setHostName() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (UnknownHostException e) {
            LogUtils.error(log, COMMON_MONITOR, "MonitorLog getLocalHost error", e);
        }
    }

    private static void runWriteThread() {
        if (null != writeThread) {
            try {
                writeThread.interrupt();
            } catch (Exception e) {
                LogUtils.error(log, COMMON_MONITOR, "interrupt write thread error", e);
            }
        }

        writeThread = new Thread(() -> {
            while (true) {
                timerLock.lock();
                try {
                    condition.await(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    LogUtils.error(log, COMMON_MONITOR, "wait error", e);
                } finally {
                    timerLock.unlock();
                }
                MonitorLog.writeLog();
            }
        });
        writeThread.setDaemon(true);
        writeThread.setName(MonitorConstants.WRITE_THREAD_NAME);
        writeThread.start();
    }

    private static void writeLog() {
        Map<MonitorKeys, ValueObject> tmp = new HashMap<>(appDatas.size());

        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat(MonitorConstants.DATE_FORMAT);
        String appTime = dateFormat.format(Calendar.getInstance().getTime());
        Iterator<Map.Entry<MonitorKeys, ValueObject>> iterator = appDatas.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<MonitorKeys, ValueObject> ent = iterator.next();
            MonitorKeys key = ent.getKey();
            long[] values = ent.getValue().getValues();
            long value1 = values[0];
            long value2 = values[1];
            if (0 == value1 && 0 == value2) {
                iterator.remove();
                continue;
            }

            sb.append(appTime).append(MonitorConstants.SPLITTER);
            sb.append(key.getString(MonitorConstants.SPLITTER)).append(MonitorConstants.SPLITTER);
            sb.append(value1).append(MonitorConstants.SPLITTER);
            sb.append(value2).append(MonitorConstants.SPLITTER_1);

            tmp.put(key, new ValueObject(value1, value2));
        }

        if (tmp.size() > 0 && writeLog) {
            appStatLog.info(sb.toString());
        }

        for (MonitorKeys key : tmp.keySet()) {
            long[] values = tmp.get(key).getValues();
            appDatas.get(key).deductCount(values[0], values[1]);
        }
    }

    public static void addStat(MonitorKeys keys, long value1, long value2) {
        ValueObject v = getAppValueObject(keys);
        if (v != null) {
            v.addCount(value1, value2);
        }
    }

    public static boolean isWriteLog() {
        return writeLog;
    }

    public static void setWriteLog(boolean writeLog) {
        MonitorLog.writeLog = writeLog;
    }

    public int getAppMaxKey() {
        return appMaxKey;
    }

    public void setAppMaxKey(int appMaxKey) {
        MonitorLog.appMaxKey = appMaxKey;
    }

    protected static ValueObject getAppValueObject(MonitorKeys keys) {
        ValueObject v = appDatas.get(keys);
        if (null == v) {
            lock.lock();
            try {
                v = appDatas.get(keys);
                if (null == v) {
                    if (appDatas.size() <= appMaxKey) {
                        v = new ValueObject();
                        appDatas.put(keys, v);
                    } else {
                        log.warn("sorry,monitor app key is out size of " + appMaxKey);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return v;
    }
}

