package com.modak.modakapp.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 10)
    private String code;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    public void removeFamily(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
