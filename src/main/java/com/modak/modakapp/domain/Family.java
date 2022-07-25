package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Family extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "family_id")

    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "family")
    private List<Member> members = new ArrayList<>();

    @Builder
    public Family(Long id, String name, LocalDateTime deletedAt, List<Member> members) {
        this.id = id;
        this.name = name;
        this.deletedAt = deletedAt;
        this.members = members;
    }
}
