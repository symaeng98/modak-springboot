package com.modak.modakapp;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // JPA Entity에서 이벤트가 발생할 때마다 특정 로직을 실행
public abstract class BaseTimeEntity {
    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP",updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    @Column(columnDefinition = "TIMESTAMP")
    private Timestamp updatedAt;

}
