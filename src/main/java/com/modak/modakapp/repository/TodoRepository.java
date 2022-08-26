package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    @Query("select t from Todo t" +
            " join fetch t.family" +
            " join fetch t.member" +
            " where t.id = :id" +
            " and t.deletedAt is null")
    Optional<Todo> findById(@Param("id") int id);

    @Query("select t from Todo t" +
            " where t.groupTodoId =:groupId" +
            " and t.deletedAt is null")
    List<Todo> findAllByGroupId(@Param("groupId") int groupId);

    @Query("select t from Todo t" +
            " where t.family.id =:familyId" +
            " and t.deletedAt is null")
    List<Todo> findAllByFamilyId(@Param("familyId") int familyId);

    @Query("select t from Todo t" +
            " where t.endDate >= :date" +
            " and t.groupTodoId = :groupTodoId" +
            " and t.deletedAt is null ")
    List<Todo> findAllByGroupIdAndDate(
            @Param("date") Date date,
            @Param("groupTodoId") int groupTodoId
    );
}
