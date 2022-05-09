package com.db.repository;

import com.db.entity.Category;
import com.db.entity.SellingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellingItemRepository extends JpaRepository<SellingItem, Long> {
}