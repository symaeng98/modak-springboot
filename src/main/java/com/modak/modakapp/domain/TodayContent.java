package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodayContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @Column(name = "title", length = 30)
    private String title;

    @NotNull
    @Column(name = "\"type\"", length = 20)
    private String type;

    @NotNull
    @Column(name = "\"description\"", length = 100)
    private String description;

    @NotNull
    @Column(name = "url", length = 100)
    private String url;
}
