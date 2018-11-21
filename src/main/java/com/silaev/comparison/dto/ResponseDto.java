package com.silaev.comparison.dto;

import com.silaev.comparison.model.DiffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    private Integer offset;
    private Integer length;
    private DiffStatus diffStatus;
}
