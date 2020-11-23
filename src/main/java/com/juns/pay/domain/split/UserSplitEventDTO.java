package com.juns.pay.domain.split;

import com.juns.pay.domain.user.UserDTO;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSplitEventDTO implements Serializable {

    private UserDTO toUser;
    private double receiveAmount;
    private long timeReceive;
}
