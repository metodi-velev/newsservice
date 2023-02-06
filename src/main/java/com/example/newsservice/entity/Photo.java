package com.example.newsservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.imaging.common.ImageMetadata;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Photo extends BaseEntity {

    @Builder
    public Photo(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String photoName,
                 byte[] photoData, ImageMetadata metaData, String linkToPhoto) {
        super(id, version, createdDate, lastModifiedDate);
        this.photoName = photoName;
        this.photoData = photoData;
        this.metaData = metaData;
        this.linkToPhoto = linkToPhoto;
    }

    private String photoName;
    @Lob
    @Column(name = "photodata", length = 1000)
    private byte[] photoData;

    @OneToOne(mappedBy = "photo")
    @JsonBackReference
    private News news;

    @Transient
    private ImageMetadata metaData;
    @Column(unique = true)
    private String linkToPhoto;

    @Override
    public String toString() {
        return "Photo{" +
                "photoName='" + photoName + '\'' +
                ", metaData='" + metaData + '\'' +
                ", linkToPhoto='" + linkToPhoto + '\'' +
                '}';
    }
}
