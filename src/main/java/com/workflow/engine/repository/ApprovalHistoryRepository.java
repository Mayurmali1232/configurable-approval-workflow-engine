package com.workflow.engine.repository;

import com.workflow.engine.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByRequestIdOrderByActionAtAsc(Long requestId);
}