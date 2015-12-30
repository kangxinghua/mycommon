package org.mycommon.modules.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by KangXinghua on 2015/12/30.
 */
public class DatesTest {

    @Test
    public void datesTest() {
        Date date = new Date();
        Date date1 = Dates.rollDay(date, -2);
        Date date2 = Dates.rollMonth(date, -3);
        System.out.println(Dates.format(date));
        System.out.println(Dates.format(date1));
        System.out.println(Dates.format(date2));
        System.out.println(Dates.getTimeOffsetDesc(date1));
        System.out.println(Dates.getTimeOffsetDesc(date2));
        System.out.println("获取下个月第一天的起始时间：" + Dates.format(Dates.getNextMonthStartTime(), Dates.PATTERN_CH));
        System.out.println("获取下个月最后一天的结束时间:" + Dates.format(Dates.getNextMonthEndTime()));
        System.out.println("获取前一个工作日:" + Dates.format(Dates.getPrevWorkday(), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("获取后一个工作日:" + Dates.format(Dates.getNextWorkday(), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("2015年10月08日的前一个工作日:" + Dates.format(Dates.getPrevWorkday(new GregorianCalendar(2015, 9, 8).getTime()), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("2015年09月30日的后一个工作日:" + Dates.format(Dates.getNextWorkday(new GregorianCalendar(2015, 8, 30).getTime()), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("2015年09月30日的后15个工作日:" + Dates.format(Dates.getWorkday(new GregorianCalendar(2015, 8, 30).getTime(), 15), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("2015年09月30日的前15个工作日:" + Dates.format(Dates.getWorkday(new GregorianCalendar(2015, 8, 30).getTime(), -15), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("获取当前季度第一天的起始时间：" + Dates.format(Dates.getQuarterStartTime()));
        System.out.println("获取当前季度最后一天的结束时间：" + Dates.format(Dates.getQuarterEndTime()));
        System.out.println("获取当周的第一个工作日：" + Dates.format(Dates.getFirstWorkday(), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("获取当周的最后一个工作日：" + Dates.format(Dates.getLastWorkday(), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("2014年2月8日：" + Dates.getWeekdayDesc(new GregorianCalendar(2014, 1, 8).getTime()) + (Dates.isWorkday(new GregorianCalendar(2014, 1, 8).getTime()) ? "是工作日" : "不是工作日"));
        System.out.println("2015年10月10日：" + Dates.getWeekdayDesc(new GregorianCalendar(2015, 9, 10).getTime()) + (Dates.isWorkday(new GregorianCalendar(2015, 9, 10).getTime()) ? "是工作日" : "不是工作日"));
        System.out.println("今年是:" + Dates.getYear() + "年" + Dates.getMonth() + "月" + Dates.getDay() + "日");
        System.out.println("下周一是:" + Dates.format(Dates.getNextWeek(Calendar.MONDAY), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("上周一是:" + Dates.format(Dates.getPrevWeek(Calendar.MONDAY), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("下个月10是:" + Dates.format(Dates.rollDay(Dates.getNextMonthStartTime(), 9), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("下下个月10是:" + Dates.format(Dates.rollDay(Dates.getNextMonthStartTime(Dates.getNextMonthStartTime()), 9), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("上个月10是:" + Dates.format(Dates.rollDay(Dates.getLastMonthStartTime(), 9), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println("上上个月10是:" + Dates.format(Dates.rollDay(Dates.getLastMonthStartTime(Dates.getLastMonthStartTime()), 9), Dates.PATTERN_CLASSICAL_SIMPLE));
        System.out.println(Dates.getWeekdayDesc(null));
    }

}