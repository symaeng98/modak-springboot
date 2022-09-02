package com.modak.modakapp.repository;

import com.modak.modakapp.domain.TodoDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoDoneRepository extends JpaRepository<TodoDone, Integer> {
    @Query("select td from TodoDone td" +
            " where td.id = :id and td.deletedAt is null")
    Optional<TodoDone> findById(@Param("id") int id);

    @Query("select count(td) from TodoDone td" +
            " where td.family.id = :familyId and td.isDone=1 and td.deletedAt is null ")
    Long findNumOfDoneByFamilyId(@Param("familyId") int familyId);
}
