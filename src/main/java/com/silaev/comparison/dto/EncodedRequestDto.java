package com.silaev.comparison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A dto is for a request payload.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EncodedRequestDto {
    @NotNull
    @Size(min = 1)
    private String data;
}
