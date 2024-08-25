package com.example.loggingmonitoring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@SpringBootApplication
@Slf4j
public class LoggingMonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingMonitoringApplication.class, args);
    }

    public static class LoggingInterceptor {
        @Bean
        public WebMvcConfigurer loggingConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(new HandlerInterceptor() {
                        @Override
                        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                            String requestIdHeader = request.getHeader("X-Request-Id");
                            MDC.put("requestId", Objects.isNull(requestIdHeader) ? UUID.randomUUID().toString() : requestIdHeader);
                            MDC.put("requestUri", request.getRequestURI());
                            MDC.put("requestMethod", request.getMethod());
                            MDC.put("requestDate", LocalDateTime.now().toString());
                            log.info("request {} {}", request.getMethod(), request.getRequestURI());
                            return true;
                        }
                    });
                }
            };
        }
    }

    @Slf4j
    @RestController
    @RequestMapping
    public static class GreetingController {
        @GetMapping("/greeting")
        public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
            log.info("greeting called with name={}", name);
            return "greeting%s".formatted(name);
        }
    }

}
