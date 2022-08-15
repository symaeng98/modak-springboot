package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Integer> {
    Member findByProviderId(String providerId);
    @Query("select m.color from Member m" +
            " where m.family.id = :familyId and m.deletedAt is null")
    List<String> findColorsByFamilyId(@Param("familyId") int familyId);

    @Query("select count (m) > 0 " +
            "from Member m " +
            "where m.providerId = :providerId and m.deletedAt is null ")
    boolean isExists(@Param(value = "providerId") String providerId);

}
