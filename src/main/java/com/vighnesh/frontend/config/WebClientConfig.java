//package com.vighnesh.frontend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    // Point this to your BACKEND (your HRMS app runs on 9090 per your logs)
//    private static final String BACKEND_BASE_URL = "http://localhost:9090";
//
//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder()
//                .baseUrl(BACKEND_BASE_URL)
//                .exchangeStrategies(
//                        ExchangeStrategies.builder()
//                                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
//                                .build()
//                )
//                .build();
//    }
//}
