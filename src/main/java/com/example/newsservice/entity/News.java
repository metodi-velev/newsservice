package com.example.newsservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by jt on 2019-01-26.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class News extends BaseEntity {

    @Builder
    public News(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String title,
                String text, String linkToPhoto, OffsetDateTime validFrom,
                OffsetDateTime validTo, String allowedRole, String unAllowedRole, ReadStatus readStatus) {
        super(id, version, createdDate, lastModifiedDate);
        this.title = title;
        this.text = text;
        this.linkToPhoto = linkToPhoto;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.allowedRole = allowedRole;
        this.unAllowedRole = unAllowedRole;
        this.readStatus = readStatus;
    }

    private String title;
    private String text;

    @Column(unique = true)
    private String linkToPhoto;

    private String allowedRole;

    private String unAllowedRole;

    @OneToOne(cascade = CascadeType.ALL)
    private ReadStatus readStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validFrom")
    private OffsetDateTime validFrom = null;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validTo")
    private OffsetDateTime validTo = null;
}
