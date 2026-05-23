package cz.cvut.fel.nss.apigateway.interceptor;

import cz.cvut.fel.nss.apigateway.logger.LoggerBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class LoggerInterceptor implements GlobalFilter {
    private final LoggerBuilder loggerBuilder;

    public LoggerInterceptor(LoggerBuilder loggerBuilder) {
        this.loggerBuilder = loggerBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log the request details
        loggerBuilder.reset();
        loggerBuilder.setDelimiter("\n");
        loggerBuilder.setLoggerClass(this.getClass());
        loggerBuilder.setRequestURI(exchange.getRequest().getURI().toString());
        loggerBuilder.setRequestMethod(exchange.getRequest().getMethod().name());
        loggerBuilder.setRequestHeaders(exchange.getRequest().getHeaders().toString());
        loggerBuilder.info();

        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    // Log the response details
                    loggerBuilder.reset();
                    loggerBuilder.setLoggerClass(this.getClass());
                    loggerBuilder.setResponseStatusCode(exchange.getResponse().getStatusCode().toString());
                    loggerBuilder.info();
                }));
    }
}
