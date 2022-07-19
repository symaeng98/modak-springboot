package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "Users")
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name="is_lunar", columnDefinition = "TINYINT", length=1)
    private int is_lunar;

    @Column(nullable = false)
    private LocalDate birthday;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role; // DAD, MOM, SON, DAU

    private String color;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String fcmToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(nullable = false)
    private LocalDateTime chatLastJoined;


    @Column(name="chatNowJoining", columnDefinition = "TINYINT", length=1, nullable = false)
    private int chatNowJoining;


    private LocalDateTime deletedAt;



}
