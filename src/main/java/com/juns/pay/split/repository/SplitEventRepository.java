package com.juns.pay.split.repository;

import com.juns.pay.split.model.SplitEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitEventRepository extends JpaRepository<SplitEvent, Long> {

    List<SplitEvent> findByCreateUserId(@Param("idCreateUser") long idUser);

    List<SplitEvent> findByRoomId(@Param("idRoom") long idRoom);

    List<SplitEvent> findByToken(@Param("token") String eventToken);

    SplitEvent findByRoomIdAndToken(long id, String eventToken);

    SplitEvent findByCreateUserIdAndToken(long userId, String token);
}
