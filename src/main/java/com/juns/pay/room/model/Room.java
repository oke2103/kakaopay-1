package com.juns.pay.room.model;

import com.juns.pay.room.domain.RoomDTO;
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
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tbRoom",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "ix_tbRoom_1",
            columnNames = {"identifier"}
        )
    },
    indexes = {
        @Index(name = "ix_tbRoom_2", columnList = "idCreateUser")
    })
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Room implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 대화방 식별값
    private String identifier;

    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCreateUser", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private User createUser;

    private int timeCreate;

    private Integer timeDelete;

    @Builder
    public Room(String identifier, String name, User createUser, int timeCreate, Integer timeDelete) {
        this.identifier = identifier;
        this.name = name;
        this.createUser = createUser;
        this.timeCreate = timeCreate;
        this.timeDelete = timeDelete;
    }

    public RoomDTO toDTO() {
        return RoomDTO.builder()
            .name(this.name)
            .token(this.identifier)
            .createUser(this.createUser)
            .timeCreate(this.timeCreate)
            .timeDelete(this.timeDelete)
            .build();
    }

    public boolean isDelete() {
        return this.getTimeDelete() != null;
    }
}
