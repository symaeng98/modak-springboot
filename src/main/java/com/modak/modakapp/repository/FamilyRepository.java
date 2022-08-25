package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Integer> {
    @Query("select f from Family f" +
            " where f.id = :id and f.deletedAt is null ")
    Optional<Family> findById(@Param("id") int id);
}
