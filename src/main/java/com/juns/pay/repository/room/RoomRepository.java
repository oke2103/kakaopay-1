package com.juns.pay.repository.room;


import com.juns.pay.model.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Room findById(@Param("id") long roomId);

    Room findByIdentifier(@Param("identifier") String identifier);
}
