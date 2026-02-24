package com.sistem.conversion.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "material_categories")
public class MaterialCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "material_categories_seq")
    @SequenceGenerator(name = "material_categories_seq", sequenceName = "material_categories_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Material> materials;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}