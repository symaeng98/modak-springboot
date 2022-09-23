package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("select m from Member m" +
            " where m.id = :id and m.deletedAt is null ")
    Optional<Member> findById(@Param("id") int id);

    @Query("select m from Member m join fetch m.family" +
            " where m.providerId = :providerId" +
            " and m.provider = :provider and m.deletedAt is null ")
    Optional<Member> findByProviderAndProviderId(@Param("provider") Provider provider, @Param("providerId") String providerId);

    @Query("select m" +
            " from Member m join fetch m.family" +
            " where m.id = :id and m.deletedAt is null ")
    Optional<Member> findMemberWithFamilyById(@Param("id") int id);

    @Query("select m" +
            " from Member m join fetch m.family" +
            " join fetch m.todayFortune" +
            " where m.id = :id and m.deletedAt is null ")
    Optional<Member> findMemberWithFamilyAndTodayFortuneById(@Param("id") int id);

    @Query("select m" +
            " from Member m" +
            " where m.family = :family" +
            " and m.deletedAt is null ")
    List<Member> findMembersByFamily(@Param("family") Family family);

    @Query("select m.color from Member m" +
            " where m.family = :family and m.deletedAt is null")
    List<String> findColorsByFamilyId(@Param("family") Family family);

    @Query("select count (m) > 0 " +
            "from Member m " +
            "where m.providerId = :providerId and m.deletedAt is null ")
    boolean isExists(@Param(value = "providerId") String providerId);
}
