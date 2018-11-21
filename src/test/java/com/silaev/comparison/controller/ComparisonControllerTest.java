package com.silaev.comparison.controller;

import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.model.DiffStatus;
import com.silaev.comparison.service.ComparisonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComparisonControllerTest {

    @Mock
    ComparisonService comparisonService;

    @InjectMocks
    ComparisonController comparisonController;

    @ParameterizedTest(name = "{index} => dataPart={0}, data={1}")
    @CsvSource({
            "left, aXkxMW15MjI1ODMxMXM=",
            "right, dXQxMXh5MjM4MTMxMXk=",
    })
    void shouldCreatePartData(String dataPartParam, String dataParam) {
        //GIVEN
        int userId = 1;
        DataPart dataPart = DataPart.byPartName(dataPartParam);
        EncodedRequestDto encodedRequestDto = EncodedRequestDto.builder()
                .data(dataParam)
                .build();
        Diff diffExpected = Diff.builder()
                .data(dataParam).dataPart(dataPart).userId(userId)
                .build();
        Mono<EncodedRequestDto> dtoMono = Mono.just(encodedRequestDto);
        when(comparisonService.createData(userId, dataPart, dtoMono))
                .thenReturn(Mono.just(diffExpected));

        //WHEN
        Mono<Diff> partData =
                comparisonController.createPartData(userId, dataPart, dtoMono);

        //THEN
        StepVerifier.create(partData)
                .assertNext(x -> assertEquals(diffExpected, x))
                .verifyComplete();
    }

    @Test
    void shouldGetDifference() {
        //GIVEN
        int userId = 1;
        ResponseDto responseDtoExpected = ResponseDto.builder()
                .diffStatus(DiffStatus.DIFF).length(2).offset(1).build();
        when(comparisonService.getDifference(userId)).thenReturn(Flux.just(responseDtoExpected));

        //WHEN
        Flux<ResponseDto> dtoFlux = comparisonController.getDifference(userId);

        //THEN
        StepVerifier.create(dtoFlux)
                .assertNext(x -> assertEquals(responseDtoExpected, x))
                .verifyComplete();
    }
}