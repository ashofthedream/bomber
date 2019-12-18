package ashes.of.bomber.squadron;

public abstract class BarrierBuilder {

    protected int workers;

    public BarrierBuilder workers(int workers) {
        this.workers = workers;
        return this;
    }


    public abstract Barrier build();
}
