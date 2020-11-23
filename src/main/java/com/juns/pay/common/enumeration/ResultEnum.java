package com.juns.pay.common.enumeration;

public enum ResultEnum {

    // success
    OK("ok", 0),

    INVALID_PARAM("invalid_param", 10000),

    // user
    UNAUTHENTICATED("unauthenticated", 100), // 존재하지 않는 User

    // result enum for room
    ROOM_NOT_EXIST("not_exist_room", 400),
    ROOM_NOT_ATTENDEE("not_room_attendee", 401),
    ROOM_ALREADY_ATTENDEE("already_room_attendee", 402),

    // result enum for split event
    SPLITEVENT_UNAUTHORIZED("unauthorized_event", 500),
    SPLITEVENT_ALREADY_APPLY("already_apply_event", 501),
    SPLITEVENT_NOT_EXIST("not_exist_event", 502),
    SPLITEVENT_EXPIRED("expired_event", 503),
    SPLITEVENT_MEMBER_MAX_COUNT_EXCESS("event_member_max_count_excess", 504),
    SPLITEVENT_CREATOR("event_creator", 505),
    SPLITEVENT_NOT_SEARCHABLE_PERIOD("event_not_searchable_period", 506),
    SPLITEVENT_FAIL_TO_RECEIVE("event_fail_to_receive", 507),
    ;

    int code;
    String resourceKey;

    ResultEnum(final String resourceKey, final int code) {
        this.resourceKey = resourceKey;
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.resourceKey;
    }
}
