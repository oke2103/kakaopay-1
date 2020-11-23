package com.juns.pay.repository.split;

import com.juns.pay.model.split.UserSplitEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSplitEventRepository extends JpaRepository<UserSplitEvent, Long> {

    UserSplitEvent findByToUserIdAndSplitEventId(@Param("idToUser") long userId, @Param("idSplitEvent") long splitEvnetId);
}
