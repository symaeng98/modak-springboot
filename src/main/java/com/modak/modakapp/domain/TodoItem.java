package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import com.modak.modakapp.TodoItemId;
import com.modak.modakapp.converter.MDFamilyAttributeConverter;
import com.modak.modakapp.domain.metadata.MDRepeatTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@Table(name = "todo_item")
@IdClass(TodoItemId.class)
public class TodoItem extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="describe", nullable = false)
    private String describe;

    @Column(name = "from_date",nullable = false)
    private Date startDate;

    @Column(name = "to_date",nullable = false)
    private Date endDate;

    @Column(name = "time_tag")
    private String timeTag;

    @Column(name = "repeat_tag", columnDefinition = "json")
    @Convert(converter = MDFamilyAttributeConverter.class)
    private MDRepeatTag repeatTag;

    @Column(name="is_done", columnDefinition = "TINYINT", length=1,nullable = false)
    @ColumnDefault("0")
    private int isDone;

    @Column(name = "image_url",length = 100)
    private String imageUrl;

    @Column(name = "order")
    @ColumnDefault("0")
    private int order;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;


    @Builder

    public TodoItem(int id, Todo todo,Member member, Family family, String title, String describe, Date startDate, Date endDate, String timeTag, MDRepeatTag repeatTag, int isDone, String imageUrl, int order, Timestamp deletedAt) {
        this.id = id;
        this.todo = todo;
        this.member = member;
        this.family = family;
        this.title = title;
        this.describe = describe;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeTag = timeTag;
        this.repeatTag = repeatTag;
        this.isDone = isDone;
        this.imageUrl = imageUrl;
        this.order = order;
        this.deletedAt = deletedAt;
    }
}
