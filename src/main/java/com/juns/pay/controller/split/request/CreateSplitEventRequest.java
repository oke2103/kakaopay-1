package com.juns.pay.controller.split.request;

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
