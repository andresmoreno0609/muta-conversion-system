package com.sistem.conversion.usecase;

import com.sistem.conversion.dto.CollectionDTO;
import com.sistem.conversion.entity.Collection;
import com.sistem.conversion.mapper.CollectionMapperV2;
import com.sistem.conversion.repository.PostgresCollectionRepository;
import com.sistem.conversion.template.UseCaseAdvance;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GetCollectionByConsecutiveUseCase extends UseCaseAdvance<Long, CollectionDTO> {

    private final PostgresCollectionRepository collectionRepository;
    private final CollectionMapperV2 collectionMapperV2;

    private Collection collection;

    public GetCollectionByConsecutiveUseCase(PostgresCollectionRepository collectionRepository, CollectionMapperV2 collectionMapperV2) {
        this.collectionRepository = collectionRepository;
        this.collectionMapperV2 = collectionMapperV2;
    }

    @Override
    protected void preConditions(Long consecutive) {

        collection = collectionRepository
                .findByConsecutiveAndOperatorId(consecutive, 6L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No trajo datos."));
    }

    @Override
    protected CollectionDTO core(Long request) {
        return collectionMapperV2.toDTO(collection);
    }
}
