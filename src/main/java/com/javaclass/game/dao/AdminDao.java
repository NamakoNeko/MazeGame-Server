package com.javaclass.game.dao;

import com.javaclass.game.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.Optional;
 
@Repository
public interface AdminDao extends JpaRepository<Admin, Long> {
 
    Optional<Admin> findByAccount(String account);

    boolean existsByAccount(String account);

    @Query("SELECT admin FROM Admin admin WHERE " +
           "(:keyword IS NULL OR admin.account LIKE %:keyword% OR admin.role LIKE %:keyword%)")
    Page<Admin> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
