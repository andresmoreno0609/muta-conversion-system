package com.sistem.conversion.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "collection_materials")
public class CollectionMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "quantity_collected")
    private Double quantityCollected;

    @Column(name = "net_quantity_collected")
    private Double netQuantityCollected = 0.0;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal = 0.0;

    @Column(name = "is_container", nullable = false)
    private Boolean container = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_collection_material_id")
    private CollectionMaterial child;

    @Column(name = "collection_verification_id")
    private Long collectionVerificationId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        container = (child != null);

        if (!container && netQuantityCollected != null && unitPrice != null) {
            subtotal = netQuantityCollected * unitPrice;
        }
        if (container && quantityCollected != null && unitPrice != null) {
            subtotal = quantityCollected * unitPrice;
        }
    }
}