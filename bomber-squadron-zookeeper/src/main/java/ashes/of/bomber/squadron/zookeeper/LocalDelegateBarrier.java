package ashes.of.bomber.squadron.zookeeper;

import ashes.of.bomber.core.Settings;
import ashes.of.bomber.squadron.Barrier;
import ashes.of.bomber.squadron.LocalBarrier;
import ashes.of.bomber.core.Stage;

public class LocalDelegateBarrier extends LocalBarrier {

    private final Barrier next;
    public LocalDelegateBarrier(int members, Barrier next) {
        super(members, next);
        this.next = next;
    }

    @Override
    public void init(String name, Settings settings) {
        next.init(name, settings);
    }

    @Override
    public void stageStart(Stage stage) {
        next.stageStart(stage);
    }

    @Override
    public void stageLeave(Stage stage) {
        next.stageLeave(stage);
    }
}
