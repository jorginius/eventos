package com.rodriguezmoreno.repository;

import com.rodriguezmoreno.dto.EventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepositoryCustom {
    Page<EventDto> findDto(Spec spec, Pageable page);
}
