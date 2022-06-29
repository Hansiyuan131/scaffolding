package com.yuanstack.sca.service.system.common.utils;

import com.yuanstack.sca.service.system.common.constants.ModelEnum;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: 日期工具类
 * @author: hansiyuan
 * @date: 2022/6/29 11:57 AM
 */
@Slf4j
public class DateUtils {

    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String YEAR = "yyyy";

    public static final String HMS = "HH:mm:ss";

    public static final String Y_M_DHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String M_DHMS = "MMdd";
    public static final String YMDHMS = "yyyyMMdd";

    public static final String YMD = "yyyyMMdd";
    public static final String Y_M = "yyyy-MM";
    public static final String Y_M_D_T_HMS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String Y_M_DHMSS = "yyyy-MM-dd HH:mm:ss.SSS";


    /**
     * 获取现在的毫秒数秒级别的
     */
    public static String getMilliSecondNow() {
        long now = System.currentTimeMillis() / 1000;
        return String.valueOf(now);
    }

    /**
     * 格式化当前时间
     */
    public static String formatNow(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 通过年龄获取出生日期
     */
    public static Date getDateByNum(Integer number) {
        Date date = new Date();
        String format = format(date, M_DHMS);
        Integer year = getYear(number);
        String time = year + format;
        return parse(time, YMDHMS);
    }

    public static Integer getYear(Integer number) {
        if (number < 0) {
            throw new IllegalArgumentException(
                    "The age cannot be zero");
        }
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        if (number > yearNow) {
            throw new IllegalArgumentException(
                    "The age cannot before year now");
        }
        return yearNow - number;
    }

    /**
     * 把date转化为指定pattern的字符串，默认pattern为yyyy-MM-dd
     */
    public static String format(Date date) {
        return format(date, Y_M_D);
    }

    /**
     * 把date转化为指定pattern的字符串
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }

        if (pattern == null || pattern.isEmpty()) {
            pattern = Y_M_D;
        }

        DateFormat df = new SimpleDateFormat(pattern);

        return df.format(date);
    }

    /**
     * 把date字符串转化为指定pattern的Date，默认pattern为yyyy-MM-dd
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, Y_M_D);
    }

    /**
     * 把date字符串转化为指定pattern的Date
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        if (pattern == null || pattern.isEmpty()) {
            pattern = Y_M_D;
        }

        DateFormat df = new SimpleDateFormat(pattern);

        try {
            Date date = df.parse(dateStr);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算两个时间的月份差
     */
    public static Integer calculationDateGap(Date smallDate, Date bigDate) {
        if (Objects.isNull(smallDate) || Objects.isNull(bigDate)) {
            return null;
        }
        int result;
        try {
            Calendar smallCalendar = Calendar.getInstance();
            Calendar bigCalendar = Calendar.getInstance();
            smallCalendar.setTime(smallDate);
            bigCalendar.setTime(bigDate);
            result = bigCalendar.get(Calendar.MONTH) - smallCalendar.get(Calendar.MONTH);
            int year = bigCalendar.get(Calendar.YEAR) - smallCalendar.get(Calendar.YEAR);
            result = result + (year * 12) + 1;
        } catch (Exception e) {
            LogUtils.error(log, ModelEnum.COMMON_UTILS, "月份差计算异常", e);
            return null;
        }
        return result;
    }

    /**
     * 获取前一天的开始时间和结束时间
     */
    public static Map<String, Date> getBeginAndEndTime(int day) {
        Map<String, Date> map = new HashMap<>();
        //取时间
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        //设置时间
        calendar.setTime(date);
        //天数减1
        calendar.add(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //获取开始时间，为昨天开始时间
        Date beginDate = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        //获取结束时间，为昨天最后时间，精确到毫秒级别
        Date endDate = calendar.getTime();
        map.put("beginDate", beginDate);
        map.put("endDate", endDate);
        return map;
    }

    /**
     * 获取日期的最后一秒
     */
    public static Date getEndTime(Long day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取日期的第一秒
     */
    public static Date getBeginTime(Long day) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        Date endTime = DateUtils.getBeginTime(System.currentTimeMillis());
        System.out.println(endTime);

        Integer integer = calculationDateGap(new Date(), DateUtils.parse("2022-01", "yyyy-MM"));
        System.out.println(integer);
    }
}

