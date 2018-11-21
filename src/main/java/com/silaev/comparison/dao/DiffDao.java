package com.silaev.comparison.dao;

import com.silaev.comparison.entity.Diff;
import com.silaev.comparison.model.DataPart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DiffDao extends ReactiveMongoRepository<Diff, String> {
    Mono<Diff> findByUserIdAndDataPart(Integer userId, DataPart dataPart);
}