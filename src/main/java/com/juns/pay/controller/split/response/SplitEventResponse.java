package com.juns.pay.controller.split.response;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SplitEventResponse extends DefaultResponse {

    private String token;

    public SplitEventResponse(ResultEnum resultEnum, String token) {
        super(resultEnum);
        this.token = token;
    }
}