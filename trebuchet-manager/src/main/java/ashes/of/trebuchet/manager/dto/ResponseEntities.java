package ashes.of.trebuchet.manager.dto;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;


public class ResponseEntities {

    public static <T> ResponseEntity<T> ok() {
        return ResponseEntity.ok().build();
    }

    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    public static <T> ResponseEntity<T> of(Optional<T> data) {
        return ResponseEntity.of(data);
    }

    public static ResponseEntity<?> failed(Throwable th) {
        return failed(getCauseOf(th).getMessage());
    }

    public static ResponseEntity<?> failed(String error) {
        ImmutableMap<String, Object> body = ImmutableMap.<String, Object>builder()
                .put("error", error)
                .build();

        return ResponseEntity.badRequest()
                .body(body);
    }

    public static ResponseEntity<?> failed(String error, Object... args) {
        return failed(String.format(error, args));
    }

    private static Throwable getCauseOf(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null)
            cause = cause.getCause();
        return cause;
    }

    public static <T> Collector<T, List<T>, ResponseEntity<List<T>>> toResponseEntity() {
        return Collector.of(ArrayList::new, List::add, (a, b) -> {
            a.addAll(b);
            return a;
        }, ResponseEntities::ok);
    }
}
