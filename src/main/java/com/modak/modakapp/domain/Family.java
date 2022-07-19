package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Family extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private int id;

    @Column(length = 20, nullable = false)
    private String name;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "family")
    private List<User> users = new ArrayList<>();

}
