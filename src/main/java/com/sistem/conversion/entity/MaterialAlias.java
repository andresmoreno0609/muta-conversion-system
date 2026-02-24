package com.sistem.conversion.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "material_aliases")
public class MaterialAlias {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_aliases_seq")
    @SequenceGenerator(name = "material_aliases_seq", sequenceName = "material_aliases_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "license_id")
    private Long license;

    @ManyToOne
    @JoinColumn(name = "material_id")
    @ToString.Exclude
    private Material material;

    @Column(name = "alias", nullable = false)
    private String alias;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
