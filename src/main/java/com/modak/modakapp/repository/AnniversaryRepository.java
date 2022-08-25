package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Integer> {
    @Query("select a from Anniversary a" +
            " where a.id = :id and a.deletedAt is null ")
    Optional<Anniversary> findById(@Param("id") int id);

    @Query("select a from Anniversary a" +
            " where ((a.startDate <= :firstDate and :firstDate <= a.endDate)" +
            " or (:firstDate<=a.startDate and a.endDate <= :lastDate)" +
            " or (a.startDate<=:lastDate and :lastDate <= a.endDate)" +
            " or (a.isYear=1))" +
            " and a.family.id = :familyId" +
            " and a.deletedAt is null")
    List<Anniversary> findAnniversariesByDate(@Param("firstDate") Date firstDate, @Param("lastDate") Date lastDate, @Param("familyId") int familyId);

    @Query("select a from Anniversary a" +
            " where a.isBirthday=1 and :memberId = a.member.id and a.deletedAt is null")
    Anniversary findAnniversaryByIsBirthday(@Param("memberId") int memberId);
}
