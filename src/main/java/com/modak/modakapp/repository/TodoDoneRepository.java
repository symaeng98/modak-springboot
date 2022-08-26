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
}
