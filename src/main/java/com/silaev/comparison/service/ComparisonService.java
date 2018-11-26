package com.silaev.comparison.service;

import com.silaev.comparison.dto.EncodedRequestDto;
import com.silaev.comparison.dto.ResponseDto;
import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ComparisonService {
    Mono<Diff> createData(Integer id,
                          DataPart dataPart,
                          Mono<EncodedRequestDto> dto);

    Flux<ResponseDto> getDifference(Integer id);


}
