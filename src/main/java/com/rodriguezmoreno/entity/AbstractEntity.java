package com.rodriguezmoreno.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.io.Serializable;
import java.util.Objects;

@Getter
public abstract class AbstractEntity<ID extends Serializable> implements Persistable<ID> {
    @Id
    @Setter
    private ID id;

    @Version
    private long version;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (id == null) {
            return false;
        }

        if (other instanceof AbstractEntity<?> that && getClass() == ProxyUtils.getUserClass(other)) {
            return Objects.equals(id, that.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final var hashCode = isNew() ? 0 : id.hashCode();
        return 17 + hashCode * 31;
    }
}
