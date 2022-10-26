package com.modak.modakapp.repository;

import com.modak.modakapp.domain.TodayContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodayContentRepository extends JpaRepository<TodayContent, Integer> {
    @Query("select tc" +
            " from TodayContent tc" +
            " where tc.id>= :id1" +
            " and tc.id <= :id2")
    List<TodayContent> findAllById(
            @Param("id1") int id1,
            @Param("id2") int id2
    );
}
