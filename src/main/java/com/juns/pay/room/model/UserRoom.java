package com.juns.pay.room.model;

import com.juns.pay.room.domain.UserRoomDTO;
import com.juns.pay.user.model.User;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tbUserRoom",
    indexes = {
        @Index(name = "ix_tbUserRoom_1", columnList = "idRoom"),
        @Index(name = "ix_tbUserRoom_2", columnList = "idUser")
    })
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idRoom", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Room room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idUser", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private User user;

    private int timeJoin;

    private Integer timeLeft;

    @Builder
    public UserRoom(Room room, User user, int timeJoin, Integer timeLeft) {
        this.room = room;
        this.user = user;
        this.timeJoin = timeJoin;
        this.timeLeft = timeLeft;
    }

    public UserRoomDTO toDTO() {
        return UserRoomDTO.builder()
            .roomId(this.room.getId())
            .roomName(this.room.getName())
            .token(this.room.getIdentifier())
            .createUser(this.room.getCreateUser())
            .timeCreate(this.room.getTimeCreate())
            .timeDelete(this.room.getTimeDelete())
            .user(this.user)
            .timeJoin(this.timeJoin)
            .timeLeft(this.timeLeft)
            .build();
    }
}
