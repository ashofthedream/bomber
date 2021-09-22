package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Test;

public interface Barrier {
    default void enterCase(Test test) {}
    default void leaveCase(Test test) {}
}
