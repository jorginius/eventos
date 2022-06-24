package com.rodriguezmoreno.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("SOURCES")
public class Source extends AbstractEntity<Long> {

    @NotNull
    private String source;
}
