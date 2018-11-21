package com.silaev.comparison.service;

import com.silaev.comparison.converter.DecodedConverter;
import com.silaev.comparison.dao.DiffDao;
import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.model.DiffStatus;
import com.silaev.comparison.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ComparisonServiceTest {
    @Mock
    DecodedConverter decodedConverter;

    @Mock
    DiffDao diffDao;

    @InjectMocks
    ComparisonService comparisonService;

    @ParameterizedTest(name = "{index} => data part={0}, data encoded={1}, data decoded={2}")
    @CsvSource({
            "left, aXkxMW15MjI1ODMxMXM=, iy11my2258311s",
            "right, dXQxMXh5MjM4MTMxMXk=, ut11xy2381311y",
    })
    void shouldCreateData(String dataPartParam, String dataParam, String dataDecodedParam) {
        //GIVEN
        int userId = 1;
        DataPart dataPart = DataPart.byPartName(dataPartParam);
        EncodedRequestDto encodedRequestDto = EncodedRequestDto.builder()
                .data(dataParam)
                .build();
        Mono<EncodedRequestDto> dtoMono = Mono.just(encodedRequestDto);
        Diff diffExpected = TestUtil.mockDiff(userId, dataParam, dataPart);
        when(decodedConverter.convert(dataParam))
                .thenReturn(dataDecodedParam);
        Diff entity = TestUtil.mockDiff(userId, dataDecodedParam, dataPart);
        when(diffDao.insert(entity))
                .thenReturn(Mono.just(entity));

        //WHEN
        Mono<Diff> diffMono = comparisonService.createData(userId, dataPart, dtoMono);

        //THEN
        StepVerifier.create(diffMono)
                .assertNext(x -> assertEquals(diffExpected, x))
                .verifyComplete();
    }

    @ParameterizedTest(name = "{index} => main data part={0}, opposite data part={1}")
    @CsvSource({
            "left, right",
            "right, left",
    })
    void shouldGetDifferenceDiff(String mainDataPartParam, String oppositeDataPartParam) {
        //GIVEN
        int userId = 1;
        String data = "iy11my2258311s";
        DataPart mainDataPart = DataPart.byPartName(mainDataPartParam);
        DataPart oppositeDataPart = DataPart.byPartName(oppositeDataPartParam);
        Diff diff = TestUtil.mockDiff(userId, data, mainDataPart);
        Mono<Diff> diffMono = Mono.just(diff);
        when(diffDao.findByUserIdAndDataPart(userId, mainDataPart))
                .thenReturn(diffMono);
        when(diffDao.findByUserIdAndDataPart(userId, oppositeDataPart))
                .thenReturn(Mono.empty());

        //WHEN
        Flux<ResponseDto> responseDtoFlux = comparisonService.getDifference(userId);

        //THEN
        StepVerifier.create(responseDtoFlux)
                .expectError()
                .verify();
    }

    @Test()
    void shouldNotGetDifference() {
        //GIVEN
        int userId = 1;
        String leftData = "iy11my2258311s";
        String rightData = "ut11xy2381311y";
        Diff diff1 = TestUtil.mockDiff(userId, leftData, DataPart.LEFT);
        Mono<Diff> diffMono1 = Mono.just(diff1);

        Diff diff2 = TestUtil.mockDiff(userId, rightData, DataPart.RIGHT);
        Mono<Diff> diffMono2 = Mono.just(diff2);

        when(diffDao.findByUserIdAndDataPart(userId, DataPart.LEFT))
                .thenReturn(diffMono1);
        when(diffDao.findByUserIdAndDataPart(userId, DataPart.RIGHT))
                .thenReturn(diffMono2);
        ResponseDto[] responseDtosExpected = {
                TestUtil.mockResponseDto(1, 2, DiffStatus.DIFF),
                TestUtil.mockResponseDto(5, 1, DiffStatus.DIFF),
                TestUtil.mockResponseDto(8, 3, DiffStatus.DIFF),
                TestUtil.mockResponseDto(14, 1, DiffStatus.DIFF)
        };

        //WHEN
        Flux<ResponseDto> responseDtoFlux = comparisonService.getDifference(userId);

        //THEN
        StepVerifier.create(responseDtoFlux)
                .expectNext(responseDtosExpected)
                .verifyComplete();
    }

    @Test()
    void shouldGetDifferenceEqual() {
        //GIVEN
        int userId = 1;
        String leftData = "iy11my2258311s";
        String rightData = "iy11my2258311s";
        Diff diff1 = TestUtil.mockDiff(userId, leftData, DataPart.LEFT);
        Mono<Diff> diffMono1 = Mono.just(diff1);

        Diff diff2 = TestUtil.mockDiff(userId, rightData, DataPart.RIGHT);
        Mono<Diff> diffMono2 = Mono.just(diff2);

        when(diffDao.findByUserIdAndDataPart(userId, DataPart.LEFT))
                .thenReturn(diffMono1);
        when(diffDao.findByUserIdAndDataPart(userId, DataPart.RIGHT))
                .thenReturn(diffMono2);
        ResponseDto responseDtoExpected =
                ResponseDto.builder().diffStatus(DiffStatus.EQUAL).build();

        //WHEN
        Flux<ResponseDto> responseDtoFlux = comparisonService.getDifference(userId);

        //THEN
        StepVerifier.create(responseDtoFlux)
                .expectNext(responseDtoExpected)
                .verifyComplete();
    }
}