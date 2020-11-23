package com.juns.pay.asset.repository;

import com.juns.pay.asset.model.UserAsset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserAssetRepository extends JpaRepository<UserAsset, Long> {

    List<UserAsset> findByUserId(@Param("idUser") long idUser);
}
