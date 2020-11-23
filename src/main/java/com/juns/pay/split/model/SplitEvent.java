package com.juns.pay.split.model;

import com.juns.pay.common.Policy;
import com.juns.pay.room.model.Room;
import com.juns.pay.split.domain.SplitEventDTO;
import com.juns.pay.split.domain.UserSplitEventDTO;
import com.juns.pay.split.enumeration.SplitEventStatus;
import com.juns.pay.user.model.User;
import com.juns.pay.utils.ModelMapperUtil;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "tbSplitEvent",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "ix_tbSplitEvent_1",
            columnNames = {"idRoom", "token"}
        ),
        @UniqueConstraint(
            name = "ix_tbSplitEvent_2",
            columnNames = {"idCreateUser", "token"}
        )
    },
    indexes = {
        @Index(name = "ix_tbSplitEvent_3", columnList = "idCreateUser"),
        @Index(name = "ix_tbSplitEvent_4", columnList = "timeExpire")
    })
@ToString(exclude = "userSplitEvents")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class SplitEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "idCreateUser", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private User createUser;

    @ManyToOne
    @JoinColumn(name = "idRoom", nullable = false, insertable = true, updatable = false, foreignKey = @javax.persistence.ForeignKey(name = "none"))
    private Room room;

    private double amount;
    private int maxCount;

    private long timeCreate;
    private long timeExpire;

    @Column(length = 32, columnDefinition = "varchar(32) default 'NONE'")
    @Enumerated(EnumType.STRING)
    private SplitEventStatus status = SplitEventStatus.NONE;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "splitEvent")
    @PrimaryKeyJoinColumn(foreignKey = @javax.persistence.ForeignKey(name = "none"))
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<UserSplitEvent> userSplitEvents;

    @Builder
    public SplitEvent(String token, User createUser, Room room, double amount, int maxCount, long timeCreate, long timeExpire, SplitEventStatus status, Set<UserSplitEvent> userSplitEvents) {
        this.token = token;
        this.createUser = createUser;
        this.room = room;
        this.amount = amount;
        this.maxCount = maxCount;
        this.timeCreate = timeCreate;
        this.timeExpire = timeExpire;
        this.status = status;
        this.userSplitEvents = userSplitEvents;
    }

    @Transient
    public boolean isAvailable(long currentTime) {
        if (this.status != SplitEventStatus.NONE) {
            return false;
        }
        if (this.timeExpire < currentTime) {
            this.status = SplitEventStatus.EXPIRED;
            return false;
        }
        if (this.userSplitEvents == null) {
            return true;
        } else if (this.userSplitEvents.size() >= this.maxCount) {
            this.status = SplitEventStatus.COMPLETE;
            return false;
        }
        return true;
    }

    @Transient
    public SplitEventStatus getStatus(long currentTime) {
        if (this.timeExpire < currentTime) {
            this.status = SplitEventStatus.EXPIRED;
            return this.status;
        }
        if (this.userSplitEvents != null && this.userSplitEvents.size() >= this.maxCount) {
            this.status = SplitEventStatus.COMPLETE;
            return this.status;
        }
        return SplitEventStatus.NONE;
    }

    @Transient
    public double getRemainAmount() {
        double receivedAmount = 0;
        if (this.status != SplitEventStatus.NONE) {
            return 0;
        }
        for (final UserSplitEvent userSplitEvents : this.userSplitEvents) {
            receivedAmount += userSplitEvents.getReceiveAmount();
        }
        return Double.parseDouble(String.format("%.2f", this.amount - receivedAmount));
    }

    @Transient
    public int getRemainCount() {
        return this.maxCount - this.userSplitEvents.size();
    }

    public SplitEventDTO toDTO() {
        return SplitEventDTO.builder()
            .amount(this.amount)
            .receiveAmount(this.amount - this.getRemainAmount())
            .timeCreate(this.timeCreate)
            .userSplitEvents(ModelMapperUtil.mapAll(this.userSplitEvents, UserSplitEventDTO.class))
            .build();
    }

    public boolean isSearchable(long currentTime) {
        return currentTime <= this.timeCreate + Policy.SPLITEVENT_SEARCHABLE_PERIOD;
    }
}
