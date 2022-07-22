package ashes.of.bomber.cli.config;

import java.util.Map;

public record HttpRequestConfig(String method, String url, Map<String, Object> payload) {
}
