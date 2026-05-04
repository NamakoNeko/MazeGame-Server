package com.javaclass.game.dao;

import com.javaclass.game.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {
	@Query("SELECT i FROM Item i WHERE " +
	           "(:keyword IS NULL OR :keyword = '' OR i.name LIKE %:keyword%) AND " +
	           "(:type IS NULL OR :type = '' OR i.type = :type)")
	    Page<Item> findByCriteria(@Param("keyword") String keyword, 
	                               @Param("type") String type, 
	                               Pageable pageable);
}