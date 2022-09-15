package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Integer> {
    @Query("select l from Letter l" +
            " where l.fromMember = :fromMember" +
            " and l.deletedAt is null ")
    List<Letter> findLettersByFromMember(
            @Param("fromMember") Member fromMember
    );
}
