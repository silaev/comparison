package com.silaev.comparison;

import com.silaev.comparison.dao.DiffDao;
import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.model.DiffStatus;
import com.silaev.comparison.util.TestUtil;
import com.silaev.comparison.version.ApiV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ComparisonApplicationIT {
    private static final String BASE_URL = ApiV1.BASE_URL;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private DiffDao diffDao;

    @ParameterizedTest(name = "{index} => dataPart={0}, data encoded={1}, data decoded={2}")
    @CsvSource({
            "left, aXkxMW15MjI1ODMxMXM=, iy11my2258311s",
            "right, dXQxMXh5MjM4MTMxMXk=, ut11xy2381311y",
    })
    void shouldCreatePartDataAndAvoidDuplication(String dataPartParam,
                                                 String dataEncodedParam,
                                                 String dataDecodedParam) {
        //GIVEN
        initDb();
        int userId = 1;
        DataPart dataPart = DataPart.byPartName(dataPartParam);
        EncodedRequestDto encodedRequestDto = EncodedRequestDto.builder()
                .data(dataEncodedParam)
                .build();
        Mono<EncodedRequestDto> dtoMono = Mono.just(encodedRequestDto);
        Diff diffExpected = TestUtil.mockDiff(userId, dataDecodedParam, dataPart);

        //WHEN
        WebTestClient.ResponseSpec exchange = webClient.post()
                .uri(BASE_URL + "/" + userId + "/" + dataPartParam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dtoMono, EncodedRequestDto.class)
                .exchange();

        WebTestClient.ResponseSpec exchangeDuplication = webClient.post()
                .uri(BASE_URL + "/" + userId + "/" + dataPartParam)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dtoMono, EncodedRequestDto.class)
                .exchange();

        //THEN
        exchange.expectStatus()
                .isCreated()
                .expectBody(Diff.class)
                .isEqualTo(diffExpected);

        exchangeDuplication.expectStatus()
                .isBadRequest();
    }

    @Test
    void shouldNotCreatePartDataDueToEmptyPayload() {
        //GIVEN
        int userId = 1;
        String dataEncodedParam = "";
        DataPart dataPart = DataPart.RIGHT;
        EncodedRequestDto encodedRequestDto = EncodedRequestDto.builder()
                .data(dataEncodedParam)
                .build();
        Mono<EncodedRequestDto> dtoMono = Mono.just(encodedRequestDto);

        //WHEN
        WebTestClient.ResponseSpec exchange = webClient.post()
                .uri(BASE_URL + "/" + userId + "/" + dataPart.getPart())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dtoMono, EncodedRequestDto.class)
                .exchange();

        //THEN
        exchange.expectStatus()
                .isBadRequest();

    }

    @Test
    void shouldGetDifferenceDiff() {
        //GIVEN
        int userId = 1;
        Diff leftDiff = TestUtil.mockDiff(userId, "iy11my2258311s", DataPart.LEFT);
        Diff rightDiff = TestUtil.mockDiff(userId, "ut11xy2381311y", DataPart.RIGHT);
        insertMockDiffsIntoDb(Arrays.asList(leftDiff, rightDiff));
        ResponseDto[] responseDtosExpected = {
                TestUtil.mockResponseDto(1, 2, DiffStatus.DIFF),
                TestUtil.mockResponseDto(5, 1, DiffStatus.DIFF),
                TestUtil.mockResponseDto(8, 3, DiffStatus.DIFF),
                TestUtil.mockResponseDto(14, 1, DiffStatus.DIFF)
        };
        //WHEN
        WebTestClient.ResponseSpec exchange = webClient
                .get()
                .uri(BASE_URL + "/" + userId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange();
        //THEN
        exchange
                .expectStatus()
                .isOk()
                .expectBodyList(ResponseDto.class)
                .contains(responseDtosExpected)
                .hasSize(responseDtosExpected.length);
    }

    @Test
    void shouldGetDifferenceEqual() {
        //GIVEN
        int userId = 1;
        Diff leftDiff = TestUtil.mockDiff(userId, "iy11my2258311s", DataPart.LEFT);
        Diff rightDiff = TestUtil.mockDiff(userId, "iy11my2258311s", DataPart.RIGHT);
        insertMockDiffsIntoDb(Arrays.asList(leftDiff, rightDiff));
        ResponseDto responseDto = ResponseDto.builder()
                .diffStatus(DiffStatus.EQUAL).build();

        //WHEN
        WebTestClient.ResponseSpec exchange = webClient
                .get()
                .uri(BASE_URL + "/" + userId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange();
        //THEN
        exchange
                .expectStatus()
                .isOk()
                .expectBody(ResponseDto.class)
                .isEqualTo(responseDto);
    }

    @ParameterizedTest(name = "{index} => data part={0}")
    @ValueSource(strings = {"left", "right"})
    void shouldNotGetDifference(String dataPartParam) {
        //GIVEN
        int userId = 1;
        Diff diff = TestUtil.mockDiff(userId, "iy11my2258311s", DataPart.byPartName(dataPartParam));
        insertMockDiffsIntoDb(Collections.singletonList(diff));

        //WHEN
        WebTestClient.ResponseSpec exchange = webClient
                .get()
                .uri(BASE_URL + "/" + userId)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange();
        //THEN
        exchange
                .expectStatus()
                .isNoContent();
    }

    /**
     * Helper method to insert mock products into MongoDB
     * @param diffs
     */
    private void insertMockDiffsIntoDb(List<Diff> diffs) {
        Flux<Diff> diffFlux = diffDao.deleteAll()
                .thenMany(
                        Flux.fromIterable(diffs)
                                .flatMap(diffDao::save)
                );

        StepVerifier.create(diffFlux)
                .expectNextCount(diffs.size())
                .verifyComplete();
    }

    private void initDb() {
        Mono<Void> mono = diffDao.deleteAll();

        StepVerifier.create(mono)
                .verifyComplete();
    }
}
