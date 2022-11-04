package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.TodoDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TodoDoneRepository extends JpaRepository<TodoDone, Integer> {
    //    @Query("select td from TodoDone td" +
//        " where td.id = :id")
    Optional<TodoDone> findById(int id);

    @Query("select count(td) from TodoDone td" +
            " where td.family = :family" +
            " and td.isDone=1")
    Long findNumOfDoneByFamilyId(@Param("family") Family family);

    //    @Query("select td from TodoDone td" +
//            " where td.todo = :todo")
    List<TodoDone> findAllByTodo(Todo todo);

    @Modifying(clearAutomatically = true)
    @Query("update TodoDone td" +
            " set td.deletedAt = :deletedAt" +
            " where td.member = :member")
    int deleteAllByMember(
            @Param("member") Member member,
            @Param("deletedAt") Timestamp deletedAt
    );
}