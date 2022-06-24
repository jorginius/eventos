package com.rodriguezmoreno.repository;

import com.rodriguezmoreno.entity.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SourceRepository extends CrudRepository<Source, Long> {
    Page<Source> findAll(Pageable pageable);

    Page<Source> findBySourceContaining(String source, Pageable pageable);
}
