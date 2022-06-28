package com.yuanstack.sca.service.system.common.log;

import com.alibaba.fastjson.JSON;
import com.yuanstack.sca.service.system.common.constants.LogType;
import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

/**
 * @description: 日志工具类：记录什么人在什么模块做了业务操作
 * @author: hansiyuan
 * @date: 2022/6/28 2:45 PM
 */
@Slf4j
public class LogUtils {

    /**
     * Debug 日志
     *
     * @param logger       日志
     * @param modelEnum    模块
     * @param bizOperation 执行操作
     * @param bizCoreParam 核心参数
     * @param obj          扩展参数
     */
    public static void debug(Logger logger, ModelEnum modelEnum, String bizOperation, Object bizCoreParam, Object... obj) {
        log(logger, LogType.DEBUG, modelEnum.getCode(), bizOperation, bizCoreParam, obj);
    }

    public static void info(Logger logger, ModelEnum modelEnum, String bizOperation, Object bizCoreParam, Object... obj) {
        log(logger, LogType.INFO, modelEnum.getCode(), bizOperation, bizCoreParam, obj);
    }

    public static void warn(Logger logger, ModelEnum modelEnum, String bizOperation, Object bizCoreParam, Object... obj) {
        log(logger, LogType.WARN, modelEnum.getCode(), bizOperation, bizCoreParam, obj);
    }

    public static void error(Logger logger, ModelEnum modelEnum, String bizOperation, Object bizCoreParam, Object... obj) {
        log(logger, LogType.ERROR, modelEnum.getCode(), bizOperation, bizCoreParam, obj);
    }

    private static void log(Logger logger, LogType logType, String modelEnum, String bizOperation, Object bizCoreParam, Object... obj) {
        try {
            Throwable e = null;
            StringBuilder printStr = new StringBuilder();
            printStr.append("[").append(modelEnum).append("]");
            printStr.append("[").append(bizOperation).append("]");
            if (ObjectUtils.isNotEmpty(bizCoreParam)) {
                if (bizCoreParam instanceof Throwable) {
                    e = (Throwable) bizCoreParam;
                } else {
                    // TODO 打印日志脱敏
                    printStr.append("[").append(JSON.toJSONString(bizCoreParam)).append("]");
                }
            }
            if (obj != null) {
                for (Object o : obj) {
                    if (o instanceof Throwable) {
                        e = (Throwable) o;
                    } else {
                        // TODO 打印日志脱敏
                        String s = JSON.toJSONString(o);
                        printStr.append("[").append(s).append("]");
                    }
                }
            }
            String msg = printStr.toString();
            if (e != null) {
                log(logger, logType, msg, e);
            } else {
                log(logger, logType, msg);
            }
        } catch (Exception e) {
            log.error("LogUtils.log异常:[{}]", ExceptionUtils.getStackTrace(e), e);
        }
    }

    private static void log(Logger logger, LogType logType, String msg, Throwable e) {
        // TODO 动态配置日志隔离级别
        switch (logType.getPriority()) {
            case 1:
                logger.debug(msg + " exception:" + ExceptionUtils.getStackTrace(e), e);
                break;
            case 2:
                logger.info(msg + " exception:" + ExceptionUtils.getStackTrace(e), e);
                break;
            case 3:
                logger.warn(msg + " exception:" + ExceptionUtils.getStackTrace(e), e);
                break;
            case 4:
                logger.error(msg + " exception:" + ExceptionUtils.getStackTrace(e), e);
                break;
            default:
                break;
        }
    }

    private static void log(Logger logger, LogType logType, String msg) {
        // TODO 动态配置日志隔离级别
        switch (logType.getPriority()) {
            case 1:
                logger.debug(msg);
                break;
            case 2:
                logger.info(msg);
                break;
            case 3:
                logger.warn(msg);
                break;
            case 4:
                logger.error(msg);
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        LogUtils.error(log, ModelEnum.BIZ_USER, "查询用户信息", 1, new Exception("发生异常"));
    }
}