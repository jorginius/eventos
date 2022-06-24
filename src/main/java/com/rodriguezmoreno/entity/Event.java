package com.rodriguezmoreno.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("EVENTS")
public class Event extends AbstractEntity<Long> {

    private long sourceId;

    @NotNull
    private Timestamp stamp;

    private long measure;

    public LocalDateTime getLocalStamp() {
        return stamp.toLocalDateTime();
    }
}
