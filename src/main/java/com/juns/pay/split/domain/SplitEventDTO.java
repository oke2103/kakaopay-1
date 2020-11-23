package com.juns.pay.split.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitEventDTO {

    // 뿌린 시각
    private long timeCreate;
    // 뿌린 금액
    private double amount;
    // 받기 완료된 금액
    private double receiveAmount;
    // 받기 완료된 정보 ([받은 금액,받은 사용자 아이디] 리스트)
    private List<UserSplitEventDTO> userSplitEvents;

}
