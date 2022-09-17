package com.modak.modakapp.domain;

import com.sun.istack.NotNull;
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
public class Letter extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private Member toMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "\"date\"")
    private Date date;

    @NotNull
    @Column(name = "envelope", length = 30)
    private String envelope;

    @NotNull
    @Column(name = "is_new", columnDefinition = "TINYINT", length = 1)
    private int isNew;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    public void changeIsNew(int isNew) {
        this.isNew = isNew;
    }
}
