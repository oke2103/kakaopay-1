package com.juns.pay.common;

public class Policy {

    // 뿌리기 요청건에 대한 고유 token은 3자리 문자열
    public static final int SPLITEVENT_TOKEN_LENGTH = 3;

    // 뿌린 건은 10분간만 유효
    public static final int SPLITEVENT_EXPIRED_TIME = 60 * 10 * 1000; // milliseconds

    // 뿌린 건에 대한 조회는 7일 동안
    public static final int SPLITEVENT_SEARCHABLE_PERIOD = 60 * 60 * 24 * 7 * 1000; // 7days milliseconds

}
