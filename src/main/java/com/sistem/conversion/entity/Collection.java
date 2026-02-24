package com.sistem.conversion.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "collector_observation", columnDefinition = "text")
    private String collectorObservation;

    @Column(name = "warehouse_observation", columnDefinition = "text")
    private String warehouseObservation;

    @Column(name = "failure_reason", columnDefinition = "text")
    private String failureReason;

    @Column(name = "collection_window_start")
    private LocalTime collectionWindowStart;

    @Column(name = "collection_window_end")
    private LocalTime collectionWindowEnd;

    @Column(name = "collector_id", nullable = true)
    private Long collectorId;

    @Column(name = "operator_id", nullable = true)
    private Long operatorId;



    @Column(name = "warehouse_id", nullable = true)
    private Long warehouseId;

    @Column(name = "address_id", nullable = false, insertable = false, updatable = false)
    private Long addressId;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "license_id", nullable = false)
    private Long licenseId;

    @Column(name = "recurrent_collection_id")
    private Long recurrentCollectionId;

    @Column(name = "consecutive")
    private Long consecutive;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CollectionMaterial> materials = new ArrayList<>();


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum CollectionStatus {
        PENDING,
        ASSIGNED,
        COLLECTED,
        CANCELLED,
        FAILED,
        COMPLETED,
        VERIFIED
    }

    public enum PaymentMethod {
        BANK_TRANSFER, CASH
    }

    public enum EntityType {
        LICENSE,
        EXTERNAL
    }
}
