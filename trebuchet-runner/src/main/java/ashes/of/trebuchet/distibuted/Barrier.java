package ashes.of.trebuchet.distibuted;


public interface Barrier {
    void enter(String test);
    void leave(String test);
}
