package io.gamioo.common.constant;

import java.util.Date;

public class TimeConstant {
    /**
     * 2020-01-01 00:00:00
     */
    public static final long LONG_AGO = 1577808000000l;

    /**
     * 2020-01-01 00:00:00
     */
    public static final Date DATE_LONG_AGO = new Date(LONG_AGO);
    /**
     * 一周毫秒时间
     */
    public static final int ONE_WEEK_MS = 7 * 24 * 60 * 60 * 1000;

    public static void main(String[] args) {
        //68719476735

        //System.out.println(2051193600000l-1577808000000l);

        //System.out.println(TimeUtil.parse("2035-01-01","yyyy-MM-dd").getTime());
    }
}
