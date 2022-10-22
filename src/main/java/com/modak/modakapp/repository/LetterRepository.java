package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Integer> {
    //    @Query("select l from Letter l" +
//            " where l.id = :id")
    Optional<Letter> findById(
            int id
    );

    @Query("select l from Letter l" +
            " where (l.fromMember = :member" +
            " or l.toMember = :member)")
    List<Letter> findLettersByMember(
            @Param("member") Member member
    );
}
