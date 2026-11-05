package com.comercial.domain.billing;

import java.math.BigDecimal;
import java.util.Objects;

public record MonetaryTotal(
        BigDecimal lineExtensionAmount,
        BigDecimal taxExclusiveAmount,
        BigDecimal taxInclusiveAmount,
        BigDecimal payableAmount) {

    public MonetaryTotal {
        Objects.requireNonNull(lineExtensionAmount, "lineExtensionAmount");
        Objects.requireNonNull(taxExclusiveAmount, "taxExclusiveAmount");
        Objects.requireNonNull(taxInclusiveAmount, "taxInclusiveAmount");
        Objects.requireNonNull(payableAmount, "payableAmount");
    }
}
