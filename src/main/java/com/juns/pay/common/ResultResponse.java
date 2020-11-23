package com.juns.pay.common;

import com.juns.pay.common.enumeration.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResultResponse<T> extends DefaultResponse {

    T result;

    public ResultResponse(ResultEnum resultEnum, T result) {
        super(resultEnum);
        this.result = result;
    }
}
