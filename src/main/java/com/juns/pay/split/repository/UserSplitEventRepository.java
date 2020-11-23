package com.juns.pay.split.repository;

import com.juns.pay.split.model.UserSplitEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSplitEventRepository extends JpaRepository<UserSplitEvent, Long> {

    public Optional<UserSplitEvent> findByToUserId(@Param("idToUser") long userId);

    UserSplitEvent findByToUserIdAndSplitEventId(@Param("idToUser") long userId, @Param("idSplitEvent") long splitEvnetId);
}
