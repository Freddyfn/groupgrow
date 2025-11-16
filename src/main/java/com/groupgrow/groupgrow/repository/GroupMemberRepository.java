package com.groupgrow.groupgrow.repository;

import com.groupgrow.groupgrow.model.GroupMember;
import com.groupgrow.groupgrow.model.GroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    List<GroupMember> findByUserId(Long userId);
    
    List<GroupMember> findByGroupId(Long groupId);

    long countByGroupId(Long groupId);

    long countDistinctByUserId(Long userId);

    @Query("SELECT gm.groupId FROM GroupMember gm WHERE gm.userId = :userId")
    List<Long> findGroupIdsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT gm.userId FROM GroupMember gm WHERE gm.groupId = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);
}

