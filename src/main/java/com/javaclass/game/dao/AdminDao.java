package com.javaclass.game.dao;

import com.javaclass.game.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.Optional;
 
@Repository
public interface AdminDao extends JpaRepository<Admin, Long> {
 
    Optional<Admin> findByAccount(String account);
}
