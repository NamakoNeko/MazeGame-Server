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

    @Query("SELECT item FROM Item item WHERE " +
           "(:keyword IS NULL OR item.name LIKE %:keyword% OR item.rare LIKE %:keyword%)")
    Page<Item> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
