package com.modak.modakapp.domain;

import com.modak.modakapp.BaseTimeEntity;
import com.modak.modakapp.converter.MDFamilyAttributeConverter;
import com.modak.modakapp.converter.MDTagAttributeConverter;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.metadata.MDFamily;
import com.modak.modakapp.dto.metadata.MDTag;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "user", indexes = @Index(name = "idx_connection_id", columnList = "connection_id"))
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(name="is_lunar", columnDefinition = "TINYINT", length=1)
    private int is_lunar;

    @Column(nullable = false)
    private Date birthday;

    @Column(length = 50)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('DAD', 'MOM', 'SON', 'DAU')")
    private Role role; // DAD, MOM, SON, DAU

    @Column(length = 15)
    private String color;

    @Column(nullable = false)
    private String refreshToken;

    @Column(name = "FCM_token",nullable = false)
    private String fcmToken;

    @Column(nullable = false,columnDefinition = "ENUM('KAKAO','APPLE')")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private String providerId;

    @Column(columnDefinition = "TIMESTAMP",nullable = false)
    private Timestamp chatLastJoined;


    @Column(name = "connection_id", length = 100)
    private String connectionId;


    @Column(name = "tag", columnDefinition = "json")
    @Convert(converter = MDTagAttributeConverter.class)
    private MDTag mdTag;


    @Convert(converter = MDFamilyAttributeConverter.class)
    @Column(name = "family_name",columnDefinition = "json")
    private MDFamily mdFamily;

    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;


    @Builder
    public Member(int id, Family family, String name, int is_lunar, Date birthday, String profileImageUrl, Role role, String color, String refreshToken, String fcmToken, Provider provider, String providerId, Timestamp chatLastJoined, String connectionId, MDTag mdTag, MDFamily mdFamily, Timestamp deletedAt) {
        this.id = id;
        this.family = family;
        this.name = name;
        this.is_lunar = is_lunar;
        this.birthday = birthday;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.color = color;
        this.refreshToken = refreshToken;
        this.fcmToken = fcmToken;
        this.provider = provider;
        this.providerId = providerId;
        this.chatLastJoined = chatLastJoined;
        this.connectionId = connectionId;
        this.mdTag = mdTag;
        this.mdFamily = mdFamily;
        this.deletedAt = deletedAt;
    }

}
