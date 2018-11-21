package com.silaev.comparison.util;

import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.model.DiffStatus;

public class TestUtil {
    public static ResponseDto mockResponseDto(int offset, int length, DiffStatus status) {
        return ResponseDto.builder()
                .offset(offset).length(length).diffStatus(status)
                .build();
    }

    public static Diff mockDiff(int userId, String data, DataPart dataPart) {
        return Diff.builder()
                .data(data)
                .dataPart(dataPart)
                .userId(userId)
                .build();
    }
}
