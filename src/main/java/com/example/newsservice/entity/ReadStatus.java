package com.example.newsservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ReadStatus extends BaseEntity {

    @Builder
    public ReadStatus(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                      Integer accountId, Instant readDate) {
        super(id, version, createdDate, lastModifiedDate);
        this.accountId = accountId;
        this.readDate = readDate;
    }

    private Integer accountId;

    @OneToOne(mappedBy = "readStatus")
    @JsonManagedReference
    private News news;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", shape = JsonFormat.Shape.STRING, timezone = "UTC")
    @JsonProperty("readDate")
    private Instant readDate;

    @Override
    public String toString() {
        return "ReadStatus{" +
                "accountId=" + accountId +
                ", readDate=" + readDate +
                '}';
    }
}
