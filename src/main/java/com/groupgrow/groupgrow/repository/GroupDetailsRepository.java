package com.groupgrow.groupgrow.repository;

import com.groupgrow.groupgrow.dto.MemberStatusDto;
import com.groupgrow.groupgrow.model.GroupDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDetailsRepository extends JpaRepository<GroupDetails, Long> {
    List<GroupDetails> findByCreatorId(Long creatorId);

    List<GroupDetails> findByPrivacy(GroupDetails.Privacy privacy);

    @Query(nativeQuery = true)
    List<MemberStatusDto> getMemberStatusForGroup(@Param("groupId") Long groupId);
}

