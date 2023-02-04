package com.example.newsservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Created by jt on 2019-01-26.
 */
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
