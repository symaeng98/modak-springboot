package com.modak.modakapp.domain;

import com.modak.modakapp.dto.metadata.MetaData;
import com.modak.modakapp.utils.converter.MetaDataAttributeConverter;
import com.sun.istack.NotNull;
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
public class Message {
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

    @NotNull
    @Column(name = "content")
    private String content;

    @Convert(converter = MetaDataAttributeConverter.class)
    @Column(name = "metadata", columnDefinition = "json")
    private MetaData metaData;

    @NotNull
    @Column(columnDefinition = "TIMESTAMP(6)")
    private Timestamp sendAt;
}
