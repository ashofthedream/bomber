package ashes.of.bomber.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ProviderBuilder {

    public static class Context {
        private final Map<Class<?>, Supplier<?>> scope;
        private final Class<?>[] types;
        private final Object[] args;

        public Context(Map<Class<?>, Supplier<?>> scope, Class<?>[] types, Object[] args) {
            this.scope = scope;
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
            return scope.get(cls);
        }
    }

    private final List<ProviderBuilder> other = new ArrayList<>();
    private final Map<Class<?>, Supplier<?>> scope = new LinkedHashMap<>();

    public ProviderBuilder addAll(ProviderBuilder builder) {
        other.add(builder);
        return this;
    }

    public ProviderBuilder add(Class<?> cls, Supplier<?> supplier) {
        scope.put(cls, supplier);
        return this;
    }

    public Context build() {
        other.forEach(provider -> {
            provider.scope.forEach(scope::putIfAbsent);
        });

        Class<?>[] types = new Class[scope.size()];
        Object[] args = new Object[scope.size()];

        AtomicInteger seq = new AtomicInteger();
        scope.forEach((type, supplier) -> {
            int i = seq.getAndIncrement();
            types[i] = type;
            Object arg = supplier.get();
            args[i] = arg;
        });

        return new Context(scope, types, args);
    }
}
