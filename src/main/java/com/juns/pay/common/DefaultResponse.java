package com.juns.pay.common;

import com.juns.pay.common.enumeration.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse {

    private int resultCode = ResultEnum.OK.getCode();
    private String resultMessage = ResultEnum.OK.getMessage();
    private String detail = "";

    public DefaultResponse(ResultEnum resultEnum) {
        this.resultCode = resultEnum.getCode();
        this.resultMessage = resultEnum.getMessage();
    }
}
