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
            " where t.id = :id")
    Optional<Todo> findById(@Param("id") int id);

    //    @Query("select t from Todo t" +
//        " where t.groupTodoId =:groupTodoId")
    List<Todo> findAllByGroupTodoId(int groupTodoId);

    //    @Query("select t from Todo t" +
//            " where t.family.id =:familyId")
    List<Todo> findAllByFamilyId(int familyId);

    //    @Query("select t from Todo t" +
//        " where t.endDate >= :date" +
//        " and t.groupTodoId = :groupTodoId")
    List<Todo> findAllByGroupTodoIdAndEndDateGreaterThanEqual(
            int groupTodoId,
            Date date
    );

    // gg
    @Query("select t from Todo t" +
            " join fetch t.member" +
            " where ((:fromDate <= t.startDate and t.startDate <= :toDate)" +
            " or (t.startDate < :fromDate and :fromDate <= t.endDate))" +
            " and t.family.id = :familyId")
    List<Todo> findAllByFromDateAndToDate(
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("familyId") int familyId
    );
}
