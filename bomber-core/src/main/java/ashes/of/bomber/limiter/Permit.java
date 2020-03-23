package ashes.of.bomber.limiter;

class Permit {
    public final long time;
    public final int position;

    Permit(long time, int position) {
        this.position = position;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Permit{" +
                "time=" + time +
                ", position=" + position +
                '}';
    }
}
