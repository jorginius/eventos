package com.rodriguezmoreno.repository;

import com.rodriguezmoreno.entity.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long>, EventRepositoryCustom {
}
