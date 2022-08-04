package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Todo extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name = "group_todo_id")
    private int groupTodoId;

    @Column(name="title", nullable = false,length = 30)
    private String title;

    @Column(name="memo")
    private String memo;

    @OneToMany(mappedBy = "todo",fetch = FetchType.LAZY)
    private List<TodoDone> todoDone;


    @Column(name = "start_date",nullable = false)
    private Date startDate;

    @Column(name = "end_date",nullable = false)
    private Date endDate;

    @Column(name = "time_tag",length = 20)
    private String timeTag;

    @Column(name = "repeat_tag",length = 20)
    private String repeatTag;

    @Column(name="is_sunday", columnDefinition = "TINYINT", length=1)
    private int isSunday;

    @Column(name="is_monday", columnDefinition = "TINYINT", length=1)
    private int isMonday;

    @Column(name="is_tuesday", columnDefinition = "TINYINT", length=1)
    private int isTuesday;

    @Column(name="is_wednesday", columnDefinition = "TINYINT", length=1)
    private int isWednesday;

    @Column(name="is_thursday", columnDefinition = "TINYINT", length=1)
    private int isThursday;

    @Column(name="is_friday", columnDefinition = "TINYINT", length=1)
    private int isFriday;

    @Column(name="is_saturday", columnDefinition = "TINYINT", length=1)
    private int isSaturday;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;


    @Builder

    public Todo(int id, Member member, Family family, int groupTodoId, String title, String memo, Date startDate, Date endDate, String timeTag, String repeatTag, int isSunday, int isMonday, int isTuesday, int isWednesday, int isThursday, int isFriday, int isSaturday, Timestamp deletedAt) {
        this.id = id;
        this.member = member;
        this.family = family;
        this.groupTodoId = groupTodoId;
        this.title = title;
        this.memo = memo;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeTag = timeTag;
        this.repeatTag = repeatTag;
        this.isSunday = isSunday;
        this.isMonday = isMonday;
        this.isTuesday = isTuesday;
        this.isWednesday = isWednesday;
        this.isThursday = isThursday;
        this.isFriday = isFriday;
        this.isSaturday = isSaturday;
        this.deletedAt = deletedAt;
    }
}
