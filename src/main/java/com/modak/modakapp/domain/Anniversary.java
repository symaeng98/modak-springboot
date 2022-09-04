package com.modak.modakapp.domain;

import com.modak.modakapp.domain.enums.Category;
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
public class Anniversary {
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

    @Column(
            name = "title",
            nullable = false,
            length = 30
    )
    private String title;

    @Column(name = "memo")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('VAC', 'ANN', 'CON', 'EVE', 'DIN')")
    private Category category;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "is_year", columnDefinition = "TINYINT", length = 1)
    private int isYear;

    @Column(name = "is_lunar", columnDefinition = "TINYINT", length = 1)
    private int isLunar;

    @Column(name = "is_birthday", columnDefinition = "TINYINT", length = 1)
    private int isBirthday;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;


    public void removeAnniversary(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void changeAnniversary(
            String title,
            Date startDate,
            Date endDate,
            Category category,
            String memo,
            int isYear
    ) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.memo = memo;
        this.isYear = isYear;
    }

    public void changeBirthday(
            Date birthday,
            int isLunar
    ) {
        this.startDate = birthday;
        this.endDate = birthday;
        this.isLunar = isLunar;
    }


}
