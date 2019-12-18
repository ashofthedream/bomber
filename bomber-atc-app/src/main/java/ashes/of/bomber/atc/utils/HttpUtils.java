package ashes.of.bomber.atc.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.List;

public class HttpUtils {
    private static final Logger log = LogManager.getLogger();

    public static Mono<ClientResponse> logRequest(ClientRequest request, ExchangeFunction next) {
        StringBuilder headers = new StringBuilder();
        request.headers()
                .forEach((name, values) -> writeHeaders(headers, name, values));

        StringBuilder cookies = new StringBuilder();
        request.cookies()
                .toSingleValueMap()
                .forEach((name, value) -> cookies.append(name).append(": ").append(value).append("\n"));

        log.info("{} {}\n{}\n\t{}",
                request.method(),
                request.url(),
                headers.toString(),
                cookies.toString());

        return next.exchange(request);
    }

    public static void logResponse(ClientResponse response) {
        StringBuilder headers = new StringBuilder();
        response.headers().asHttpHeaders()
                .forEach((name, values) -> writeHeaders(headers, name, values));

        StringBuilder cookies = new StringBuilder();
        response.cookies().toSingleValueMap()
                .forEach((name, value) -> cookies.append(name).append(" = ").append(value).append("\n"));

        log.info("Response status code: {}\n\t{}\n\t{}", response.statusCode(), headers.toString(), cookies.toString());
    }

    private static void writeHeaders(StringBuilder headers, String name, List<String> values) {
        values.forEach(value -> headers.append(name).append(": ").append(value).append("\n"));
    }

}
