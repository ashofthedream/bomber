package ashes.of.bomber.example.models;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private Long userId;

    private BigDecimal amount = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Account setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Account setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
}
