package com.sistem.conversion.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materials_seq")
    @SequenceGenerator(name = "materials_seq", sequenceName = "materials_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private MaterialCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "measure", nullable = false)
    private MeasureType measure;

    @Column(name = "density", nullable = false, precision = 10, scale = 3)
    private BigDecimal density = BigDecimal.valueOf(1.0);

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MaterialAlias> aliases;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum MeasureType {
        UNI,
        KG
    }
}
