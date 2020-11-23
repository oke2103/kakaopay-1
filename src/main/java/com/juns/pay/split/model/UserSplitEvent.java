package com.juns.pay.split.model;

import com.juns.pay.split.domain.UserSplitEventDTO;
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
@Table(name = "tbUserSplitEvent",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "ix_tbUserSplitEvent_1",
            columnNames = {"idSplitEvent", "idToUser"}
        )
    },
    indexes = {
        @Index(name = "ix_tbUserSplitEvent_2", columnList = "idSplitEvent"),
        @Index(name = "ix_tbUserSplitEvent_3", columnList = "idToUser")
    })
@ToString
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class UserSplitEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSplitEvent", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private SplitEvent splitEvent;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idToUser", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private User toUser;

    private double receiveAmount;

    private Long timeReceive;

    @Builder
    public UserSplitEvent(SplitEvent splitEvent, User toUser, double receiveAmount, Long timeReceive) {
        this.splitEvent = splitEvent;
        this.toUser = toUser;
        this.receiveAmount = receiveAmount;
        this.timeReceive = timeReceive;
    }

    public UserSplitEventDTO toDTO() {
        return UserSplitEventDTO.builder()
            .toUser(this.toUser.toDTO())
            .receiveAmount(this.receiveAmount)
            .timeReceive(this.timeReceive)
            .build();
    }
}