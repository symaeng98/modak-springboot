package com.modak.modakapp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.modak.modakapp.domain.enums.Provider;
import com.modak.modakapp.domain.enums.Role;
import com.modak.modakapp.dto.metadata.MDFamily;
import com.modak.modakapp.dto.metadata.MDTag;
import com.modak.modakapp.utils.converter.MDFamilyAttributeConverter;
import com.modak.modakapp.utils.converter.MDTagAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted_at is null")
@Table(name = "user", indexes = @Index(name = "idx_connection_id", columnList = "connection_id"))
public class Member extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "today_fortune_id")
    private TodayFortune todayFortune;

    @NotNull
    @Column(name = "name", length = 20)
    private String name;

    @Column(name = "is_lunar", columnDefinition = "TINYINT", length = 1)
    private int isLunar;

    @NotNull
    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "profile_image_url", length = 50)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"role\"", columnDefinition = "ENUM('DAD', 'MOM', 'SON', 'DAU')")
    private Role role; // DAD, MOM, SON, DAU

    @Column(name = "color", length = 15)
    private String color;

    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;

    @NotNull
    @Column(name = "FCM_token")
    private String fcmToken;

    @NotNull
    @Column(name = "provider", columnDefinition = "ENUM('KAKAO','APPLE')")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @NotNull
    @Column(name = "provider_id")
    private String providerId;

    @NotNull
    @Column(name = "chat_last_joined", columnDefinition = "TIMESTAMP")
    private Timestamp chatLastJoined;

    @Column(name = "connection_id", length = 100)
    private String connectionId;

    @Column(name = "today_fortune_at")
    private Date todayFortuneAt;

    @Convert(converter = MDTagAttributeConverter.class)
    @Column(name = "tag", columnDefinition = "json")
    private MDTag mdTag;

    @Convert(converter = MDFamilyAttributeConverter.class)
    @Column(name = "family_name", columnDefinition = "json")
    private MDFamily mdFamily;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private Timestamp deletedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>(); // 회원이 가지고 있는 권한 정보들

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.providerId;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }

    public void changeMemberTag(MDTag mdTag) {
        this.mdTag = mdTag;
    }

    public void changeMemberFamilyName(MDFamily mdFamily) {
        this.mdFamily = mdFamily;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public void changeMemberInfo(
            String name,
            Role role,
            String color,
            Date birthday,
            int isLunar
    ) {
        this.name = name;
        this.role = role;
        this.color = color;
        this.birthday = birthday;
        this.isLunar = isLunar;
    }

    public void changeFamily(Family family) {
        this.family = family;
    }

    public void changeTodayFortuneAndTodayFortuneAt(TodayFortune todayFortune, Date date) {
        this.todayFortune = todayFortune;
        this.todayFortuneAt = date;
    }

    public void removeMember(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
}
