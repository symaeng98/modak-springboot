package com.modak.modakapp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
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
