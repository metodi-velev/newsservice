package com.example.newsservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by jt on 2019-01-26.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Photo extends BaseEntity {

    @Builder
    public Photo(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String photoName,
                 byte[] photoData, String metaData, String linkToPhoto) {
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

    private String metaData;
    @Column(unique = true)
    private String linkToPhoto;

}
