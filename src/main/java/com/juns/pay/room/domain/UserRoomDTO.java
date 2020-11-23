package com.juns.pay.room.domain;

import com.juns.pay.user.model.User;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoomDTO {

    @NotEmpty
    private long roomId;
    @NotEmpty
    private User user;
    @NotEmpty
    private String token;
    private String roomName;
    private String userName;
    private User createUser;
    private int timeCreate;
    private Integer timeDelete;
    private int timeJoin;
    private Integer timeLeft;
}
