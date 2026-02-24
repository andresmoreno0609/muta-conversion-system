package com.sistem.conversion.adapter;

import com.sistem.conversion.dto.CollectionDTO;
import com.sistem.conversion.usecase.GetCollectionByConsecutiveUseCase;
import org.springframework.stereotype.Service;

@Service
public class CollectionAdapterImpl {

    private final GetCollectionByConsecutiveUseCase getCollectionByConsecutiveUseCase;

    public CollectionAdapterImpl(GetCollectionByConsecutiveUseCase getCollectionByConsecutiveUseCase) {
        this.getCollectionByConsecutiveUseCase = getCollectionByConsecutiveUseCase;
    }

    public CollectionDTO getCollectionByConsecutive(Long consecutive) {
        return getCollectionByConsecutiveUseCase.execute(consecutive);
    }
}
