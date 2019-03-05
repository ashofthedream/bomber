package ashes.of.trebuchet.distributed.zookeeper;

import ashes.of.trebuchet.builder.Settings;
import ashes.of.trebuchet.distibuted.Barrier;
import ashes.of.trebuchet.distibuted.LocalBarrier;
import ashes.of.trebuchet.runner.Stage;

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
