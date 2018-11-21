package com.silaev.comparison.controller;

import com.silaev.comparison.converter.StringToDataPartConverter;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import com.silaev.comparison.service.ComparisonService;
import com.silaev.comparison.version.ApiV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Performs basic REST operations.
 */
@RestController
@ApiV1
@RequiredArgsConstructor
@Slf4j
@Validated
public class ComparisonController {
    private final ComparisonService comparisonService;

    @PostMapping(value = "/{id}/{dataPart}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Diff> createPartData(@PathVariable("id") Integer id,
                                     @PathVariable("dataPart") DataPart dataPart,
                                     @Validated @RequestBody Mono<EncodedRequestDto> encodedRequestDto) {

        return comparisonService.createData(id, dataPart, encodedRequestDto);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ResponseDto> getDifference(@PathVariable("id") Integer id) {
        return comparisonService.getDifference(id);

    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(DataPart.class, new StringToDataPartConverter());
    }
}
