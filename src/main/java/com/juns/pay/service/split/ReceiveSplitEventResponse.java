package com.juns.pay.service.split;

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
public class ReceiveSplitEventResponse extends DefaultResponse {

    double receiveAmount;

    public ReceiveSplitEventResponse(ResultEnum resultEnum, double receiveAmount) {
        super(resultEnum);
        this.receiveAmount = receiveAmount;
    }

}
