package com.workflow.engine.repository;

import com.workflow.engine.entity.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByRequestTypeOrderByStepOrderAsc(String requestType);
    Optional<ApprovalStep> findByRequestTypeAndStepOrder(String requestType, Integer stepOrder);
}