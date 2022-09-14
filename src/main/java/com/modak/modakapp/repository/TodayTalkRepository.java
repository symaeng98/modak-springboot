package com.modak.modakapp.repository;

import com.modak.modakapp.domain.TodayTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface TodayTalkRepository extends JpaRepository<TodayTalk, Integer> {
    @Query("select tt from TodayTalk tt" +
            " where tt.date >= :startDate" +
            " and tt.date <= :endDate" +
            " and tt.family.id = :familyId" +
            " and tt.deletedAt is null")
    List<TodayTalk> findTodayTalkByDate(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("familyId") int familyId
    );

}
