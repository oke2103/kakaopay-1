package com.juns.pay.split.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSplitEventRequest {

    private double amount;
    private int maxCount;
}
