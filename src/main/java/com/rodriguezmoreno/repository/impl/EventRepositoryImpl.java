package com.rodriguezmoreno.repository.impl;

import com.rodriguezmoreno.dto.EventDto;
import com.rodriguezmoreno.repository.EventRepositoryCustom;
import com.rodriguezmoreno.repository.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

@Repository
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final NamedParameterJdbcTemplate template;

    public EventRepositoryImpl(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Page<EventDto> findDto(Spec spec, Pageable page) {
        final var where = where(spec);
        final var params = params(spec);

        final var count = count(where, params);

        if (count == 0L) {
            return Page.empty();
        } else {
            final var sql = new StringJoiner(" ")
                    .add("SELECT EVENTS.ID, SOURCE, STAMP, MEASURE")
                    .add("FROM EVENTS INNER JOIN SOURCES ON SOURCES.ID = EVENTS.SOURCE_ID")
                    .add(where)
                    .add(order(page.getSort()))
                    .add("LIMIT")
                    .add(String.valueOf(page.getPageSize()))
                    .add("OFFSET")
                    .add(String.valueOf(page.getOffset()))
                    .toString();

            final var events = template.query(sql, params, new BeanPropertyRowMapper<>(EventDto.class));

            return new PageImpl<>(events, page, count);
        }
    }

    private long count(String where, Map<String, Object> params) {
        final var sql = new StringJoiner(" ")
                .add("SELECT COUNT(ID) FROM EVENTS")
                .add(where)
                .toString();

        return Optional.ofNullable(template.queryForObject(sql, params, Long.class)).orElse(0L);
    }

    private Map<String, Object> params(Spec spec) {
        final Map<String, Object> params = new HashMap<>();

        params.put("source_id", spec.getSourceId());
        params.put("ts1", Optional.ofNullable(spec.getTs1()).map(Timestamp::valueOf).orElse(null));
        params.put("ts2", Optional.ofNullable(spec.getTs2()).map(Timestamp::valueOf).orElse(null));
        params.put("min", spec.getMin());
        params.put("max", spec.getMax());

        return params;
    }

    private String where(Spec spec) {
        final var where = new StringJoiner(" AND ", "WHERE ", "").setEmptyValue("");

        if (spec.getSourceId() != null) {
            where.add("SOURCE_ID = :source_id");
        }

        if (spec.getTs1() != null) {
            where.add("STAMP >= :ts1");
        }

        if (spec.getTs2() != null) {
            where.add("STAMP <= :ts2");
        }

        if (spec.getMin() != null) {
            where.add("MEASURE >= :min");
        }

        if (spec.getMax() != null) {
            where.add("MEASURE <= :max");
        }

        return where.toString();
    }

    private String order(Sort sort) {
        final var order = new StringJoiner(", ", "ORDER BY ", "").setEmptyValue("");

        if (sort.isSorted()) {
            sort.stream().forEachOrdered(term ->
                    order.add(term.getProperty() + " " + (term.isAscending() ? "ASC" : "DESC")));
        }

        return order.toString();
    }
}
