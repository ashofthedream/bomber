package ashes.of.bomber.dispatcher;

import ashes.of.bomber.core.Application;

public interface DispatcherBuilder {
    Dispatcher build(Application application);
}
