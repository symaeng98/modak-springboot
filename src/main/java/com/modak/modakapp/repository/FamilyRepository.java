package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Integer> {
    //    @Query("select f from Family f" +
//            " where f.id = :id")
    Optional<Family> findById(int id);

    //    @Query("select f from Family f" +
//            " where f.code = :code")
    Optional<Family> findByCode(String code);

    @Query("select count (f) > 0 from Family f" +
            " where f.code = :code")
    boolean isExists(
            @Param("code") String code
    );
}
