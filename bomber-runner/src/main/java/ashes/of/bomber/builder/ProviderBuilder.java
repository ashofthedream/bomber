package ashes.of.bomber.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ProviderBuilder {

    public static class Context {

        private final Map<Class<?>, Supplier<?>> providers;
        private final Class<?>[] types;
        private final Object[] args;

        public Context(Map<Class<?>, Supplier<?>> providers, Class<?>[] types, Object[] args) {
            this.providers = providers;
            this.types = types;
            this.args = args;
        }

        public Class<?>[] getTypes() {
            return types;
        }

        public Object[] getArgs() {
            return args;
        }


        public Supplier<?> getByType(Class<?> cls) {
            return providers.get(cls);
        }

    }

    private final Map<Class<?>, Supplier<?>> providers = new LinkedHashMap<>();

    public ProviderBuilder add(ProviderBuilder builder) {
        builder.providers.forEach(providers::put);
        return this;
    }

    public ProviderBuilder add(Class<?> cls, Supplier<?> supplier) {
        providers.put(cls, supplier);
        return this;
    }

    public Context build() {
        Class<?>[] types = new Class[providers.size()];
        Object[] args = new Object[providers.size()];

        AtomicInteger seq = new AtomicInteger();
        providers.forEach((type, supplier) -> {
            int i = seq.getAndIncrement();
            types[i] = type;
            Object arg = supplier.get();
            args[i] = arg;
        });

        return new Context(providers, types, args);
    }
}
