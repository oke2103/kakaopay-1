package com.juns.pay.room.repository;


import com.juns.pay.room.model.UserRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {

    List<UserRoom> findByUserId(@Param("idUser") long userId);

    List<UserRoom> findByRoomId(@Param("idRoom") long roomId);

    UserRoom findByUserIdAndRoomIdAndTimeLeftNull(@Param("idUser") long userId, @Param("idRoom") long roomId);
}
