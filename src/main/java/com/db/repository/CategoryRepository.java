package com.db.repository;

import com.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findFirstByCategory(String category);
    boolean existsByCategory(String category);
    @Modifying
    @Transactional
    @Query("update Category c set c.lastUpdate = :lastUpdate where c.category = :category")
    void setLastUpdateByCategory(@Param(value = "lastUpdate") Date lastUpdate, @Param(value = "category") String category);
}