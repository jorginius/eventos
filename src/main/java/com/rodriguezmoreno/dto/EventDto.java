package com.rodriguezmoreno.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class EventDto {
    private long id;
    private String source;
    private Timestamp stamp;
    private long measure;

    public LocalDateTime getLocalStamp() {
        return stamp.toLocalDateTime();
    }
}
