package com.sistem.conversion.repository;

import com.sistem.conversion.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostgresCollectionRepository extends JpaRepository<Collection, Long>, JpaSpecificationExecutor<Collection> {

    Optional<Collection> findByConsecutiveAndOperatorId(Long consecutive, Long operatorId);
}
