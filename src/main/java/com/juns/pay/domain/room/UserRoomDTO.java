package com.juns.pay.domain.room;

import com.juns.pay.domain.user.UserDTO;
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
    private UserDTO user;
    @NotEmpty
    private String token;
    private String roomName;
    private String userName;
    private UserDTO createUser;
    private int timeCreate;
    private Integer timeDelete;
    private int timeJoin;
    private Integer timeLeft;
}
