package com.groupgrow.groupgrow.repository;

import com.groupgrow.groupgrow.model.GroupDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDetailsRepository extends JpaRepository<GroupDetails, Long> {
    List<GroupDetails> findByCreatorId(Long creatorId);
}

