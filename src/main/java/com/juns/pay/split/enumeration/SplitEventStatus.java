package com.juns.pay.split.enumeration;

public enum SplitEventStatus {
    NONE,
    COMPLETE, // 모든 사용자가 받기 완료
    EXPIRED, // 유효 기간 만료
    ;
}
