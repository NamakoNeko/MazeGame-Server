package com.javaclass.game.dao;

import com.javaclass.game.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogDao extends JpaRepository<OperationLog, Long> {

    Page<OperationLog> findByAdminId(Long adminId, Pageable pageable);
}
