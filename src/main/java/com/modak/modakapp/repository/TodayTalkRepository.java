package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

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

    @Query("select tt from TodayTalk tt" +
            " where tt.member = :member" +
            " and tt.date = :date" +
            " and tt.deletedAt is null")
    Optional<TodayTalk> findTodayTalkByMemberAndDate(
            @Param("member") Member member,
            @Param("date") Date date
    );

    @Query("select count (tt) > 0 from TodayTalk tt" +
            " where tt.member = :member" +
            " and tt.family = :family" +
            " and tt.date = :date" +
            " and tt.deletedAt is null")
    boolean isExists(
            @Param("member") Member member,
            @Param("family") Family family,
            @Param("date") Date date
    );
}
