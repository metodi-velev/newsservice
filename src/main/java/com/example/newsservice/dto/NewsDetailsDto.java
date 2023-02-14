package com.example.newsservice.dto;

import com.example.newsservice.utils.AdvanceInfo;
import com.example.newsservice.utils.BasicInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "title", "text" })
public class NewsDetailsDto {

    @Size(min=6, max=30, groups = BasicInfo.class)
    private String title;

    @NotBlank(groups = BasicInfo.class)
    @Size(min=20, max=200, groups = BasicInfo.class)
    private String text;

    @NotBlank(groups = AdvanceInfo.class)
    private String allowedRole;

    @NotBlank(groups = AdvanceInfo.class)
    private String unAllowedRole;

    @NotBlank(groups = AdvanceInfo.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validFrom")
    private OffsetDateTime validFrom = null;

    @NotBlank(groups = AdvanceInfo.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    @JsonProperty("validTo")
    private OffsetDateTime validTo = null;
}
