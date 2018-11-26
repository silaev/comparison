package com.silaev.comparison.service;

import com.silaev.comparison.converter.DecodedConverter;
import com.silaev.comparison.dao.DiffDao;
import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.model.DiffStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Serves ComparisonController basic operations.
 */
@Service
@RequiredArgsConstructor
public class ComparisonServiceImpl implements ComparisonService {
    private final DecodedConverter decodedConverter;
    private final DiffDao diffDao;

    @Override
    public Mono<Diff> createData(Integer id,
                                 DataPart dataPart,
                                 Mono<EncodedRequestDto> dto) {

        return dto
                .map(d -> getDiffEntity(id, dataPart, d))
                .flatMap(diffDao::insert);
    }

    private Diff getDiffEntity(Integer id, DataPart dataPart, EncodedRequestDto d) {
        return Diff.builder()
                .data(decodedConverter.convert(d.getData()))
                .dataPart(dataPart)
                .userId(id)
                .build();
    }

    @Override
    public Flux<ResponseDto> getDifference(Integer id) {

        Mono<Diff> leftMonoDiff = diffDao.findByUserIdAndDataPart(id, DataPart.LEFT)
                .switchIfEmpty(Mono.error(new IllegalStateException("The left part is empty!")));

        Mono<Diff> rightMonoDiff = diffDao.findByUserIdAndDataPart(id, DataPart.RIGHT)
                .switchIfEmpty(Mono.error(new IllegalStateException("The right part is empty!")));

        return Mono.zip(leftMonoDiff, rightMonoDiff, this::getDiffDtos)
                .flatMapIterable(Function.identity());
    }

    private List<ResponseDto> getDiffDtos(Diff leftDiff, Diff rightDiff) {
        //basic validation for possible manual modifications to MongoDb.
        String leftData = Objects.requireNonNull(leftDiff.getData());
        String rightData = Objects.requireNonNull(rightDiff.getData());

        int leftDataLength = leftData.length();
        int rightDataLength = rightData.length();

        List<ResponseDto> responseDtos = new ArrayList<>();
        if (leftDataLength == rightDataLength) {
            if (leftData.equals(rightData)) {
                responseDtos.add(ResponseDto.builder()
                        .diffStatus(DiffStatus.EQUAL)
                        .build());
            } else {

                String[] leftArr = leftData.split("");
                String[] rightArr = rightData.split("");

                int count = 0;
                int offset = 0;
                for (int i = 0; i < leftDataLength; i++) {

                    String leftVal = leftArr[i];
                    String rightVal = rightArr[i];

                    boolean equals = leftVal.equals(rightVal);
                    if (equals && (count > 0)) {

                        responseDtos.add(ResponseDto.builder()
                                .length(count).offset(offset + 1).diffStatus(DiffStatus.DIFF)
                                .build());
                        offset = 0;
                        count = 0;

                    } else if (!equals) {
                        if (count == 0) {
                            offset = i;
                        }
                        count++;

                        if (i == leftDataLength - 1) {
                            responseDtos.add(ResponseDto.builder()
                                    .length(count).offset(offset + 1).diffStatus(DiffStatus.DIFF)
                                    .build());
                            offset = 0;
                            count = 0;
                        }
                    }
                }
            }
        } else {
            responseDtos.add(ResponseDto.builder()
                    .diffStatus(DiffStatus.NOT_EQUAL_SIZE)
                    .build());
        }

        return responseDtos;
    }
}
