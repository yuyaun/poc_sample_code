package com.spdemo.db.dao;

import com.spdemo.db.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String>  {

    @Query("SELECT s FROM UserInfo s WHERE s.userName = :userName")
    UserInfo getActiveUser(@Param("userName") String userName);



}
