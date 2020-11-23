package com.juns.pay.common;

import com.juns.pay.common.enumeration.ResultEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResultListResponse<T> extends DefaultResponse {

    List<T> resultList;
    private long count;

    public ResultListResponse(ResultEnum resultEnum, List<T> result) {
        super(resultEnum);
        this.resultList = result;
        this.count = result.size();
    }
}
