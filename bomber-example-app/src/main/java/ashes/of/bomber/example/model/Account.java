package ashes.of.bomber.example.model;

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class Account {
    @Nullable
    private Long id;

    private BigDecimal amount = BigDecimal.ZERO;

    public Account(@Nullable Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public Account() {
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
