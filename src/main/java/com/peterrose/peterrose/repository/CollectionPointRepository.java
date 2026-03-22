package com.peterrose.peterrose.repository;

import com.peterrose.peterrose.model.CollectionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, String> {
    List<CollectionPoint> findByActiveTrue();
    List<CollectionPoint> findByProvince(String province);
}
