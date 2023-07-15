package com.yuqian.itax.util.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName DateUtil
 * @Description 日期时间转换工具类
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
@Slf4j
public class DateUtil {

    public static final String FILE_NAME = "MMddHHmmssSSS";
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd";
    public static final String DIR_PATTERN = "yyyy/MM/dd/";
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMES_PATTERN = "HH:mm:ss";
    public static final String NOCHAR_PATTERN = "yyyyMMddHHmmss";
    public static final String PLAIN_PATTERN = "yyyyMMdd";

    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";


    /**
     * 获取当前时间（毫秒）
     * @return
     */
    public static Long getCurrentTimes() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间（秒）
     * @return
     */
    public static Long getCurrentTimesTampDate() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取指定日期前后的时间戳(毫秒)
     *
     * @param date
     * @param time
     * @return
     */
    public static long dateToStamp(Date date, long time) {
        return date.getTime() + time;
    }

    /**
     * 日期转换为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDateByFormat(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 转换为默认格式(yyyy-MM-dd)的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatDefaultDate(Date date) {
        return formatDateByFormat(date, DEFAULT_PATTERN);
    }

    /**
     * 转换为目录格式(yyyy/MM/dd/)的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatDirDate(Date date) {
        return formatDateByFormat(date, DIR_PATTERN);
    }

    /**
     * 转换为完整格式(yyyy-MM-dd HH:mm:ss)的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatTimesTampDate(Date date) {
        return formatDateByFormat(date, TIMESTAMP_PATTERN);
    }

    /**
     * 转换为时分秒格式(HH:mm:ss)的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatTimesDate(Date date) {
        return formatDateByFormat(date, TIMES_PATTERN);
    }

    /**
     * 转换为时分秒格式(HH:mm:ss)的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatNoCharDate(Date date) {
        return formatDateByFormat(date, NOCHAR_PATTERN);
    }

    /**
     * 日期格式字符串转换为日期对象
     *
     * @param strDate 日期格式字符串
     * @param pattern 日期对象
     * @return
     */
    public static Date parseDate(String strDate, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date nowDate = format.parse(strDate);
            return nowDate;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 字符串转换为默认格式(yyyy-MM-dd)日期对象
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date parseDefaultDate(String date) {
        return parseDate(date, DEFAULT_PATTERN);
    }

    /**
     * 字符串转换为完整格式(yyyy-MM-dd HH:mm:ss)日期对象
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date parseTimesTampDate(String date) {
        return parseDate(date, TIMESTAMP_PATTERN);
    }

    /**
     * 获得当前时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * sql Date 转 util Date
     *
     * @param date java.sql.Date日期
     * @return java.util.Date
     */
    public static Date parseUtilDate(java.sql.Date date) {
        return date;
    }

    /**
     * util Date 转 sql Date
     *
     * @param date java.sql.Date日期
     * @return
     */
    public static java.sql.Date parseSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 获取年份
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取星期
     *
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    /**
     * 获取日期(多少号)
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时间(小时)
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间(分)
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取当前时间(秒)
     *
     * @param date
     * @return
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 获取当前秒
     *
     * @param date
     * @return
     */
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis() / 1000;
    }

    /**
     * 获取当前毫秒
     *
     * @param date
     * @return
     */
    public static long getMillisecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 日期增加
     *
     * @param date Date
     * @param day  int
     * @return Date
     */
    public static Date addDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(c.DATE, day);
        return c.getTime();
    }

    /**
     * 日期相减(返回天数)
     *
     * @param date  Date
     * @param date1 Date
     * @return int 相差的天数
     */
    public static int diffDate(Date date, Date date1) {
        return (int) ((getMillisecond(date) - getMillisecond(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 日期相减(返回秒值)
     *
     * @param date  Date
     * @param date1 Date
     * @return int
     * @author
     */
    public static Long diffDateTime(Date date, Date date1) {
        return (Long) (getMillis(date) - getMillis(date1));
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time, String format) {
        Date date = new Date(time * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long time) {
        Date date = new Date(time * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
        return simpleDateFormat.format(date);
    }

    /**
     * 将时间字符串转时间戳
     *
     * @param dateString
     * @return
     */
    public static Long dateString2Long(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_PATTERN);
        Long value = null;
        if (!StringUtils.isEmpty(dateString)) {
            Date date = null;
            try {
                date = dateFormat.parse(dateString);
                if (date != null) {
                    value = date.getTime() / 1000;
                }
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return value;
    }

    /**
     * 日期开始时间
     *
     * @param start
     * @return
     */
    public static Long getStartTime(String start) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        try {
            if (!StringUtils.isEmpty(start)) {
                Date date = sdf.parse(start);
                return date.getTime() / 1000L;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 上周时间
     *
     * @param day
     * @return
     */
    public static String getLastWeekDate(int day) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = day - dayOfWeek;
        calendar.add(Calendar.DATE, offset - 7);
        return sdf.format(calendar.getTime());
    }

    /**
     * 本周时间
     *
     * @param diffDay
     * @return
     */
    public static String getCurrentDate(int diffDay) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        String imptimeBegin = sdf.format(cal.getTime());

        if (diffDay == 6) {
            cal.add(Calendar.DATE, 6);
            return sdf.format(cal.getTime());
        }
        return imptimeBegin;
    }

    /**
     * 上月开始时间
     *
     * @param mon
     * @return
     */
    public static String getMonStartDate(int mon) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, mon);
        int lastMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay);
        //上月第一天
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-01");
        return sdf2.format(c.getTime());
    }

    /**
     * @Description 获取当月第一天
     * @Author  Kaven
     * @Date   2020/9/8 10:22
     * @Param
     * @Return
     * @Exception
    */
    public static String getMonFirstDay(){
        Calendar cale = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        // 获取本月的第一天
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        String firstday = format.format(cale.getTime());
        return firstday;
    }

    /**
     * @Description 获取当月的最后一天
     * @Author  Kaven
     * @Date   2020/9/8 10:32
     * @Param
     * @Return
     * @Exception
    */
    public static String getMonLastDay(){
        Calendar cale = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        String lastday = format.format(cale.getTime());
        return lastday;
    }

    /**
     * 上月结束时间
     *
     * @param mon
     * @return
     */
    public static String getMonEndDate(int mon) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, mon);
        int lastMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), lastMonthMaxDay);
        //上月最后一天
        return sdf.format(c.getTime());
    }

    /**
     * 日期结束时间
     *
     * @param endTime
     * @return
     */
    public static Long getEndTime(String endTime) {
        return getStartTime(endTime) + 24 * 60 * 60 - 1;
    }

    /**
     * 日期结束时间
     *
     * @param endTime 时间戳
     * @return
     */
    public static Long getEndTime(Long endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getEndTime(sdf.format(new Date(endTime * 1000L)));
    }

    /**
     * 日期开始时间
     *
     * @param startTime 时间戳
     * @return
     */
    public static Long getStartTime(Long startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getStartTime(sdf.format(new Date(startTime * 1000L)));
    }

    /**
     * 昨天开始时间
     *
     * @return
     */
    public static Long getYesterdayStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getStartTime(sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L)));
    }

    /**
     * 昨天结束时间
     *
     * @return
     */
    public static Long getYesterdayEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getEndTime(sdf.format(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L)));
    }

    /**
     * 今天开始时间
     * @return
     */
    public static Long getTodayStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getStartTime(sdf.format(new Date(System.currentTimeMillis())));
    }

    /**
     * 今天结束时间
     *
     * @return
     */
    public static Long getTodayEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        return getEndTime(sdf.format(new Date(System.currentTimeMillis())));
    }

    /**
     * 上周开始时间
     *
     * @return
     */
    public static Long getLastWeekStartTime() {
        return getStartTime(getLastWeekDate(1));
    }

    /**
     * 上周结束时间
     *
     * @return
     */
    public static Long getLastWeekEndTime() {
        return getEndTime(getLastWeekDate(7));
    }

    /**
     * 本周开始时间
     *
     * @return
     */
    public static Long getCurrentWeekStartTime() {
        return getStartTime(getCurrentDate(0));
    }

    /**
     * 本周结束时间
     *
     * @return
     */
    public static Long getCurrentWeekEndTime() {
        return getEndTime(getCurrentDate(6));
    }

    /**
     * 上月开始时间
     *
     * @return
     */
    public static Long getLastMonStartTime() {
        return getStartTime(getMonStartDate(-1));
    }


    /**
     * 上月结束时间
     *
     * @return
     */
    public static Long getLastMonEndTime() {
        String lastMonDate = getMonEndDate(-1);
        return getEndTime(lastMonDate);
    }

    /**
     * 本月开始时间
     *
     * @return
     */
    public static Long getMonStartTime() {
        return getStartTime(getMonStartDate(0));
    }


    /**
     * 本月结束时间
     *
     * @return
     */
    public static Long getMonEndTime() {
        String lastMonDate = getMonEndDate(0);
        return getEndTime(lastMonDate);
    }

    /**
     * 时间快捷
     *
     * @param timeType
     * @return
     */
    public static Map<String, Long> getTimeMap(Integer timeType) {
        Map<String, Long> timeMap = new HashMap<String, Long>();
        switch (timeType) {
            case 1:
                timeMap.put("startTime", DateUtil.getYesterdayStartTime());
                timeMap.put("endTime", DateUtil.getYesterdayEndTime());
                break;
            case 2:
                timeMap.put("startTime", DateUtil.getTodayStartTime());
                timeMap.put("endTime", DateUtil.getTodayEndTime());
                break;
            case 3:
                timeMap.put("startTime", DateUtil.getLastWeekStartTime());
                timeMap.put("endTime", DateUtil.getLastWeekEndTime());
                break;
            case 4:
                timeMap.put("startTime", DateUtil.getCurrentWeekStartTime());
                timeMap.put("endTime", DateUtil.getCurrentWeekEndTime());
                break;
            case 5:
                timeMap.put("startTime", DateUtil.getLastMonStartTime());
                timeMap.put("endTime", DateUtil.getLastMonEndTime());
                break;
            case 6:
                timeMap.put("startTime", DateUtil.getMonStartTime());
                timeMap.put("endTime", DateUtil.getMonEndTime());
                break;
            default:
                break;
        }
        return timeMap;
    }

    public static boolean isBeforeNow(Date date) {
        if (date == null){
            return false;
        }
        return date.compareTo(new Date()) < 0;
    }

    /**
     * @Author Kaven
     * @Description 时间格式化
     * @Date 14:54 2019/8/15
     * @Param [date, format]
     * @return java.lang.String
     **/
    public static String format(Date date, String format) {
        return date == null ? null : (new SimpleDateFormat(format)).format(date);
    }

    /**
     * @Author Kaven
     * @Description 获取某年某月的第一天日期
     * @Date 10:49 2019/8/19
     * @Param [date]
     * @return java.util.Date
     **/
    public static Date getStartMonthDate(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return dateTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay().toDate();
    }

    /**
     * @Author Kaven
     * @Description 获取某年某月的最后一天日期
     * @Date 14:07 2019/8/19
     * @Param [date]
     * @return java.util.Date
     **/
    public static Date getEndMonthDate(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return dateTime.dayOfMonth().withMaximumValue().millisOfDay().withMaximumValue().toDate();
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_PATTERN);
        String result = format.format(today);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期
     * @param past
     * @return
     */
    public static String getFetureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * @Author Kaven
     * @Description 获取当前时间过去一个月的时间
     * @Date 9:38 2019/9/6
     * @Param []
     * @return java.lang.String
     **/
    public static String getRecentMonth(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        //过去一月
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 获取当前季度
     */
    public static String getQuarter() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int quarter = 0;
        if (month >= 1 && month <= 3) {
            quarter = 1;
        } else if (month >= 4 && month <= 6) {
            quarter = 2;
        } else if (month >= 7 && month <= 9) {
            quarter = 3;
        } else {
            quarter = 4;
        }
        return quarter + "";
    }

    /**
     * 获取指定时间对应的季度
     */
    public static String getQuarter(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH) + 1;
        int quarter = 0;
        if (month >= 1 && month <= 3) {
            quarter = 1;
        } else if (month >= 4 && month <= 6) {
            quarter = 2;
        } else if (month >= 7 && month <= 9) {
            quarter = 3;
        } else {
            quarter = 4;
        }
        return quarter + "";
    }

    /**
     * 获取某季度的第一天和最后一天
     *	@param num 第几季度
     */
    public static String[] getCurrQuarter(int year,int num) {
        String[] s = new String[2];
        String str = "";
        // 设置本年的季
        Calendar quarterCalendar = null;
        switch (num) {
            case 1: // 本年到现在经过了一个季度，在加上前4个季度
                quarterCalendar = Calendar.getInstance();
                quarterCalendar.set(Calendar.YEAR,year);
                quarterCalendar.set(Calendar.MONTH, 3);
                quarterCalendar.set(Calendar.DATE, 1);
                quarterCalendar.add(Calendar.DATE, -1);
                str = format(quarterCalendar.getTime(), "yyyy-MM-dd");
                if(StringUtils.isNotEmpty(str)) {
                    s[0] = str.substring(0, str.length() - 5) + "01-01";
                    s[1] = str;
                }else{
                    s[0] = quarterCalendar.get(Calendar.YEAR)+ "01-01";
                    s[1] = str;
                }
                break;
            case 2: // 本年到现在经过了二个季度，在加上前三个季度
                quarterCalendar = Calendar.getInstance();
                quarterCalendar.set(Calendar.YEAR,year);
                quarterCalendar.set(Calendar.MONTH, 6);
                quarterCalendar.set(Calendar.DATE, 1);
                quarterCalendar.add(Calendar.DATE, -1);
                str = format(quarterCalendar.getTime(), "yyyy-MM-dd");
                if(StringUtils.isNotEmpty(str)) {
                    s[0] = str.substring(0, str.length() - 5) + "04-01";
                    s[1] = str;
                }else{
                    s[0] = quarterCalendar.get(Calendar.YEAR)+ "04-01";
                    s[1] = str;
                }
                break;
            case 3:// 本年到现在经过了三个季度，在加上前二个季度
                quarterCalendar = Calendar.getInstance();
                quarterCalendar.set(Calendar.YEAR,year);
                quarterCalendar.set(Calendar.MONTH, 9);
                quarterCalendar.set(Calendar.DATE, 1);
                quarterCalendar.add(Calendar.DATE, -1);
                str = format(quarterCalendar.getTime(), "yyyy-MM-dd");
                if(StringUtils.isNotEmpty(str)) {
                    s[0] = str.substring(0, str.length() - 5) + "07-01";
                    s[1] = str;
                }else{
                    s[0] = quarterCalendar.get(Calendar.YEAR)+ "07-01";
                    s[1] = str;
                }
                break;
            case 4:// 本年到现在经过了四个季度，在加上前一个季度
                quarterCalendar = Calendar.getInstance();
                quarterCalendar.set(Calendar.YEAR,year);
                str = format(quarterCalendar.getTime(), "yyyy-MM-dd");
                if(StringUtils.isNotEmpty(str)) {
                    s[0] = str.substring(0, str.length() - 5) + "10-01";
                    s[1] = str.substring(0, str.length() - 5) + "12-31";
                }else{
                    s[0] = quarterCalendar.get(Calendar.YEAR)+ "10-01";
                    s[1] = quarterCalendar.get(Calendar.YEAR) + "12-31";
                }
                break;
        }
        return s;
    }

    /**
     * @Description 计算日期相差天数（Java8 API实现）
     * @Author  Kaven
     * @Date   2020/3/13 9:42
     * @Param   date1 date2
     * @Return  int 相差天数
     * @Exception
    */
    public static int differentDays(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new RuntimeException("日期不能为空");
        }
        LocalDate localDate1 = date2LocalDate(date1);
        LocalDate localDate2 = date2LocalDate(date2);
        return (int) localDate1.until(localDate2, ChronoUnit.DAYS);
    }

    public static LocalDate date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    /**
     * 判断[d1, d2]时间的月份是有一致，注意时间格式要一致
     * @param d1
     * @param d2
     * @return
     */
    public static boolean sameMonth(Date d1, Date d2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(d1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(d2);
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int month2 = calendar2.get(Calendar.MONTH);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        nowTime = DateUtil.parseDate(DateUtil.format(nowTime,DateUtil.DEFAULT_PATTERN),DateUtil.DEFAULT_PATTERN);
        if (nowTime!=null &&(nowTime.getTime() == startTime.getTime() || nowTime.getTime() == endTime.getTime())) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取本年的第一天或最后一天
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     */
    public static String getStartOrEndDayOfYear(LocalDate today, Boolean isFirst){
        String result = "";
        LocalDate resDate = LocalDate.now();
        if (today == null) {
            today = resDate;
        }
        if (isFirst) {
            resDate = LocalDate.of(today.getYear(), Month.JANUARY, 1);
            result = resDate.toString() + " 00:00:00";
        } else {
            resDate = LocalDate.of(today.getYear(), Month.DECEMBER, Month.DECEMBER.length(today.isLeapYear()));
            result = resDate.toString() + " 23:59:59";
        }
        return result;
    }

    public static void main(String[] args){
        //String[] str = getCurrQuarter(Integer.valueOf(getQuarter()));
        //System.out.println(str[0]);
        //System.out.println(str[1]);
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //Date date1 = sdf.parse("2019-12-25");
        //Date date2 = sdf.parse("2020-1-1");
        //System.out.println(differentDays(date1,date2));

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        //Date updateTime = sdf.parse("2020-04-01 00:00:00");
        //System.out.println(!sameMonth(updateTime,new Date()));

        //Date updateTime = parseTimesTampDate("2020-03-31 00:00:00");
        //String[] currQuarter = DateUtil.getCurrQuarter(Integer.valueOf(DateUtil.getQuarter()));
        //System.out.println(currQuarter[0] + ","+ currQuarter[1]);
        //System.out.println(isEffectiveDate(updateTime, parseDefaultDate(currQuarter[0]), parseDefaultDate(currQuarter[1])));

//        System.out.println("本年开始时间>>>"+getStartOrEndDayOfYear(null,true));
//        System.out.println("本年结束时间>>>"+getStartOrEndDayOfYear(null,false));

//        System.out.println("当月第一天>>>" + getMonFirstDay());
//        System.out.println("当月最后一天>>>" + getMonLastDay());
//        System.out.println("startTime>>>" + stampToDate(DateUtil.getYesterdayStartTime(), PLAIN_PATTERN));
//        System.out.println("endTime>>>" + stampToDate(DateUtil.getYesterdayEndTime(), PLAIN_PATTERN));
        String[] aa = DateUtil.getCurrQuarter(2021,1);
         System.out.print(aa[0]+"==========="+aa[1]);
    }

}

