package com.sistem.conversion.controller;


import com.sistem.conversion.adapter.CollectionAdapterImpl;
import com.sistem.conversion.dto.CollectionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v2/collections")
@RequiredArgsConstructor
public class CollectionControllerV2 {

    private final CollectionAdapterImpl adapter;

    @GetMapping("/{consecutive}")
    public ResponseEntity<CollectionDTO> getCollectionByConsecutive(@PathVariable Long consecutive) {
        return ResponseEntity.ok(adapter.getCollectionByConsecutive(consecutive));
    }

}
