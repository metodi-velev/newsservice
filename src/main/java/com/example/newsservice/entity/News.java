package com.example.newsservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class News extends BaseEntity {

    @Builder
    public News(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String title,
                String text, String linkToPhoto, OffsetDateTime validFrom,
                OffsetDateTime validTo, String allowedRole, String unAllowedRole, ReadStatus readStatus, Photo photo) {
        super(id, version, createdDate, lastModifiedDate);
        this.title = title;
        this.text = text;
        this.linkToPhoto = linkToPhoto;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.allowedRole = allowedRole;
        this.unAllowedRole = unAllowedRole;
        this.readStatus = readStatus;
        this.photo = photo;
    }

    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String title;

    @NotNull
    @NotBlank
    @Size(max = 200)
    @Column(length = 200)
    private String text;

    @Column(unique = true)
    private String linkToPhoto;

    private String allowedRole;

    private String unAllowedRole;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.REMOVE})
    @JoinColumn(name = "read_status_id", referencedColumnName = "id")
    @JsonManagedReference
    private ReadStatus readStatus;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.REMOVE})
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    @JsonManagedReference
    private Photo photo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validFrom")
    private OffsetDateTime validFrom = null;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validTo")
    private OffsetDateTime validTo = null;

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", linkToPhoto='" + linkToPhoto + '\'' +
                ", allowedRole='" + allowedRole + '\'' +
                ", unAllowedRole='" + unAllowedRole + '\'' +
                ", readStatus=" + readStatus +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                '}';
    }
}
