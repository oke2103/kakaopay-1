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
public class RoomDTO {

    // 대화방 식별값
    @NotEmpty
    private String token;
    private String name;
    @NotEmpty
    private User createUser;
    private int timeCreate;
    private Integer timeDelete;
}
