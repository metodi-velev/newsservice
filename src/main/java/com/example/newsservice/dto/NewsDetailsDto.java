package com.example.newsservice.dto;

import com.example.newsservice.entity.Photo;
import com.example.newsservice.entity.ReadStatus;
import com.example.newsservice.utils.AdvanceInfo;
import com.example.newsservice.utils.BasicInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "text" })
public class NewsDetailsDto {

    private UUID id;

    private Long version;

    private Timestamp createdDate;

    private Timestamp lastModifiedDate;

    @NotBlank(groups = BasicInfo.class)
    @Size(min = 6, max = 30, groups = BasicInfo.class)
    private String title;

    @NotBlank(groups = BasicInfo.class)
    @Size(min = 20, max = 200, groups = BasicInfo.class)
    private String text;

    private String linkToPhoto;

    @NotBlank(groups = AdvanceInfo.class)
    private String allowedRole;

    @NotBlank(groups = AdvanceInfo.class)
    private String unAllowedRole;

    private ReadStatus readStatus;

    private Photo photo;

    @NotBlank(groups = AdvanceInfo.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validFrom")
    private OffsetDateTime validFrom = null;

    @NotBlank(groups = AdvanceInfo.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validTo")
    private OffsetDateTime validTo = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsDetailsDto)) return false;
        NewsDetailsDto that = (NewsDetailsDto) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
