package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
public class TodayTalk extends BaseTimeEntity {
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

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "\"date\"")
    private Date date;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    public void changeContent(String content) {
        this.content = content;
    }

    public void removeTodayTalk(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
