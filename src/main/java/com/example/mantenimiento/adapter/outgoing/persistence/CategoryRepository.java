package com.example.mantenimiento.adapter.outgoing.persistence;

import com.example.mantenimiento.adapter.outgoing.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
}
