package com.modak.modakapp.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Family extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    @OneToMany(mappedBy = "family")
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "family")
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "family")
    private List<TodoDone> todoDones = new ArrayList<>();

    @OneToMany(mappedBy = "family")
    private List<Anniversary> anniversaries = new ArrayList<>();

    @Builder
    public Family(
            int id,
            String name,
            Timestamp deletedAt,
            List<Member> members
    ) {
        this.id = id;
        this.name = name;
        this.deletedAt = deletedAt;
        this.members = members;
    }

    public void removeFamily(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
