package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Family extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "family")
    private List<Member> members = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "family")
    private List<Todo> todos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "family")
    private List<TodoDone> todoDones = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "family")
    private List<Anniversary> anniversaries = new ArrayList<>();

    public void removeFamily(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
