package com.garvk.api_gateway.filter;

import com.garvk.api_gateway.config.JwtConfig;
import com.garvk.api_gateway.validations.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtFilter extends AbstractGatewayFilterFactory<JwtConfig> {

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public JwtFilter(){
        super(JwtConfig.class);
    }

    @Override
    public GatewayFilter apply(JwtConfig config) {
        return (((exchange, chain) -> {

            if(routeValidator.isSecured.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey("Authorization")){
                    throw new RuntimeException("Missing Authorization Header" + exchange.getRequest().getURI());
                }

                String authHeader = exchange.getRequest().getHeaders().get("Authorization").get(0);

                if(null != authHeader && authHeader.startsWith("Bearer")){
                    authHeader = authHeader.substring(7);
                }

                if(null == authHeader) throw new RuntimeException("auth is null");

                return webClientBuilder.build()
                        .post()
                        .uri("http://AUTH-SERVICE/validate")
                        .contentType(MediaType.TEXT_PLAIN)
                        .bodyValue(authHeader)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .flatMap(isValid -> {
                            if (Boolean.TRUE.equals(isValid)) {
                                return chain.filter(exchange);
                            } else {
                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                                return exchange.getResponse().setComplete();
                            }
                        })
                        .onErrorResume(error -> {
                            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                            return exchange.getResponse().setComplete();
                        });

//                try{
//                    // REST Call for token validation
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.setContentType(MediaType.TEXT_PLAIN);  // Set content type to plain text
//
//                    // Create the request entity with just the token as the body
//                    HttpEntity<String> requestEntity = new HttpEntity<>(authHeader, headers);
//
//                    // Use postForObject with the request entity
//                    Boolean isValid = restTemplate.postForObject(
//                            "http://AUTH-SERVICE/validate",
//                            requestEntity,
//                            Boolean.class
//                    );
//                } catch (Exception e){
//                    e.printStackTrace();
//                    throw new RuntimeException(e);
//                }
            }

            return chain.filter(exchange);
        }));
    }
}
