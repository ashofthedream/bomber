package ashes.of.bomber.squadron;

import ashes.of.bomber.core.Test;

public interface Barrier {

    void init(int members);

    void enterCase(Test test);
    void leaveCase(Test test);
}
