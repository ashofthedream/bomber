package ashes.of.bomber.squadron;

public class NoBarrier implements Barrier {

    public static class Builder extends BarrierBuilder {

        @Override
        public Barrier build() {
            return new NoBarrier();
        }
    }

}
