package ashes.of.bomber.methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class LifeCycleHolder<T> implements LifeCycleMethod<T> {
    private static final Logger log = LogManager.getLogger();

    private final boolean onlyOnce;
    private final AtomicBoolean check = new AtomicBoolean();
    private final LifeCycleMethod<T> method;

    public LifeCycleHolder(boolean onlyOnce, LifeCycleMethod<T> method) {
        this.onlyOnce = onlyOnce;
        this.method = method;
    }

    @Override
    public void call(T instance) throws Throwable {
        if (!onlyOnce) {
            method.call(instance);
            return;
        }

        // first thread initializes the content, all other - wait for initialization
        synchronized (check) {
            if (check.compareAndSet(false, true))
                method.call(instance);
        }
    }

    public void reset() {
        check.set(false);
    }
}
