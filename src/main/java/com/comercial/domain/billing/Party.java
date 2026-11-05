package com.comercial.domain.billing;

import java.util.Objects;

public record Party(String name) {
    public Party {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Party name must not be blank");
        }
    }
}
