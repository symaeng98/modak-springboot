package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor
public class TodoDone extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Column(name = "\"date\"",nullable = false)
    private Date date;

    @Column(name="is_done", columnDefinition = "TINYINT", length=1,nullable = false)
    private int isDone;

    @Column(name = "image_url",length = 100)
    private String imageUrl;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    @Builder
    public TodoDone(int id, Member member, Family family, Todo todo, Date date, int isDone, String imageUrl, Timestamp deletedAt) {
        this.id = id;
        this.member = member;
        this.family = family;
        this.todo = todo;
        this.date = date;
        this.isDone = isDone;
        this.imageUrl = imageUrl;
        this.deletedAt = deletedAt;
    }
}
