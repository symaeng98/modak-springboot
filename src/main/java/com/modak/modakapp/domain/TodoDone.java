package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDone extends BaseTimeEntity {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    @Column(name = "\"date\"", nullable = false)
    private Date date;

    @Column(
            name = "is_done",
            columnDefinition = "TINYINT",
            length = 1,
            nullable = false
    )
    private int isDone;

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    public void changeIsDone(int isDone) {
        this.isDone = isDone;
    }

    public void changeTodo(Todo todo) {
        this.todo = todo;
        todo.getTodoDone().add(this);
    }

    public void removeTodoDone(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
