package com.example.mantenimiento.adapter.outgoing.persistence;

import com.example.mantenimiento.adapter.outgoing.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
}
