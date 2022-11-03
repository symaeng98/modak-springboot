package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
public class Todo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @OneToMany(mappedBy = "todo", fetch = FetchType.LAZY)
    @Builder.Default
    private List<TodoDone> todoDoneList = new ArrayList<>();

    @Column(name = "group_todo_id")
    private int groupTodoId;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "memo")
    private String memo;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "time_tag", length = 20)
    private String timeTag;

    @Column(name = "repeat_tag", length = 20)
    private String repeatTag;

    @Column(name = "is_sunday", columnDefinition = "TINYINT", length = 1)
    private int isSunday;

    @Column(name = "is_monday", columnDefinition = "TINYINT", length = 1)
    private int isMonday;

    @Column(name = "is_tuesday", columnDefinition = "TINYINT", length = 1)
    private int isTuesday;

    @Column(name = "is_wednesday", columnDefinition = "TINYINT", length = 1)
    private int isWednesday;

    @Column(name = "is_thursday", columnDefinition = "TINYINT", length = 1)
    private int isThursday;

    @Column(name = "is_friday", columnDefinition = "TINYINT", length = 1)
    private int isFriday;

    @Column(name = "is_saturday", columnDefinition = "TINYINT", length = 1)
    private int isSaturday;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    public void changeSingleTodo(
            String title,
            String memo,
            Member member,
            Date date,
            String timeTag
    ) {
        this.title = title;
        this.memo = memo;
        this.member = member;
        this.startDate = date;
        this.endDate = date;
        this.timeTag = timeTag;
    }

    public void changeRepeatTodo(
            String title,
            String memo,
            Member member,
            String timeTag
    ) {
        this.title = title;
        this.memo = memo;
        this.member = member;
        this.timeTag = timeTag;
    }

    public void changeGroupTodoId(int groupTodoId) {
        this.groupTodoId = groupTodoId;
    }

    public void changeEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void changeStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void removeTodo(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
