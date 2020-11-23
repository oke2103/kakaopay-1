package com.juns.pay.utils;

import java.util.concurrent.TimeUnit;

public class TimeAndDateUtil {

    public static final int A_DAY_SEC = 60 * 60 * 24;

    public static long getCurrentTimeMiliSec() {
        return getCurrentTime(TimeUnit.MILLISECONDS);
    }

    public static int getCurrentTimeSec() {
        return (int) getCurrentTime(TimeUnit.SECONDS);
    }

    public static long getCurrentTime(final TimeUnit unit) {
        return unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
