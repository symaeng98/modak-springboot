package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import com.modak.modakapp.converter.MDFamilyAttributeConverter;
import com.modak.modakapp.converter.MDRepeatTagAttributeConverter;
import com.modak.modakapp.domain.metadata.MDFamily;
import com.modak.modakapp.domain.metadata.MDRepeatTag;
import com.modak.modakapp.domain.metadata.MDTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
public class Todo extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="\"describe\"")
    private String describe;

    @Column(name = "time_tag")
    private String timeTag;

    @Column(name = "repeat_tag", columnDefinition = "json")
    @Convert(converter = MDRepeatTagAttributeConverter.class)
    private MDRepeatTag repeatTag;

    @Column(name = "start_date",nullable = false)
    private Date startDate;

    @Column(name = "end_date",nullable = false)
    private Date endDate;

    @Column(name = "\"order\"")
    @ColumnDefault("0")
    private int order;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;


    @Builder
    public Todo(int id, Member member, Family family, String title, String describe, String timeTag, MDRepeatTag repeatTag, Date startDate, Date endDate, int order, Timestamp deletedAt) {
        this.id = id;
        this.member = member;
        this.family = family;
        this.title = title;
        this.describe = describe;
        this.timeTag = timeTag;
        this.repeatTag = repeatTag;
        this.startDate = startDate;
        this.endDate = endDate;
        this.order = order;
        this.deletedAt = deletedAt;
    }
}
