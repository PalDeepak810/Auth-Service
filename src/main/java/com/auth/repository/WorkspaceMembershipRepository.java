package com.auth.repository;

import com.auth.entity.WorkspaceMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMembershipRepository
        extends JpaRepository<WorkspaceMembership, Long> {

    List<WorkspaceMembership> findByUserId(Long userId);
}
