package ashes.of.bomber.dispatcher;

import ashes.of.bomber.core.Application;

public class Dispatcher {

    protected final Application application;

    public Dispatcher(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }
}
