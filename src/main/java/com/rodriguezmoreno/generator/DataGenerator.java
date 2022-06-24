package com.rodriguezmoreno.generator;

import com.github.javafaker.Faker;
import com.rodriguezmoreno.entity.Event;
import com.rodriguezmoreno.entity.Source;
import com.rodriguezmoreno.repository.EventRepository;
import com.rodriguezmoreno.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

@Component
public class DataGenerator {
    private final int sourceMax;
    private final int eventMax;

    public DataGenerator(@Value("${wise.sources:10}") int sourceMax, @Value("${wise.events:1000}") int eventMax) {
        this.sourceMax = sourceMax;
        this.eventMax = eventMax;
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(SourceRepository sourceRepository, EventRepository eventRepository) {
        return event -> {
            if (sourceRepository.count() == 0L) {
                generate(sourceRepository, eventRepository, sourceMax, eventMax);
            }
        };
    }

    private void generate(SourceRepository sourceRepository, EventRepository eventRepository, int sourceMax, int eventMax) {
        final var faker = new Faker(Locale.forLanguageTag("es-ES"));

        final var sources = sourceRepository.saveAll(LongStream.rangeClosed(1, sourceMax)
                .mapToObj(i -> new Source(faker.nation().capitalCity()))
                .collect(Collectors.toList()));

        final var randomSourceId = StreamSupport.stream(sources.spliterator(), false)
                .map(Source::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), list -> {
                    Collections.shuffle(list);
                    return list;
                }));

        final var date = ZonedDateTime.now();

        eventRepository.saveAll(LongStream.rangeClosed(1, eventMax)
                .mapToObj(id -> new Event(
                        randomSourceId.get(faker.number().numberBetween(0, sourceMax)),
                        Timestamp.from(date.toInstant()
                                .minus(faker.number().numberBetween(5, 16L), ChronoUnit.MINUTES)),
                        faker.number().numberBetween(1, 10L))).collect(Collectors.toList()));
    }
}
