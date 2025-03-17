package com.garvk.api_gateway.validations;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/validate",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request ->{
        boolean shouldSecure = openApiEndpoints
                .stream()
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
                System.out.println("Is secured: " + shouldSecure + " for path: " + request.getURI().getPath());
        return shouldSecure;
    };
}
