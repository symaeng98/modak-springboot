package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Integer> {
    @Query("select l from Letter l" +
            " where l.id = :id" +
            " and l.deletedAt is null ")
    Optional<Letter> findLetterById(
            @Param("id") int id
    );

    @Query("select l from Letter l" +
            " where l.fromMember = :member" +
            " and l.deletedAt is null ")
    List<Letter> findSentLettersByMember(
            @Param("member") Member member
    );

    @Query("select l from Letter l" +
            " where l.toMember = :member" +
            " and l.deletedAt is null ")
    List<Letter> findReceivedLettersByMember(
            @Param("member") Member member
    );

    @Query("select l from Letter l" +
            " where l.toMember = :member" +
            " and l.isNew = 1" +
            " and l.deletedAt is null ")
    List<Letter> findReceivedNewLettersByMember(
            @Param("member") Member member
    );
}
