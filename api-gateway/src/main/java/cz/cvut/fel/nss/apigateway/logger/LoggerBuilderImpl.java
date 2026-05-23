package cz.cvut.fel.nss.apigateway.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LoggerBuilder interface for building the log message and logging it.
 */
@Service
@Scope("prototype")
public class LoggerBuilderImpl implements LoggerBuilder {
    private String delimiter;
    private final List<String> logMessageParts;
    private Logger logger;

    public LoggerBuilderImpl() {
        this.delimiter = ";";
        this.logMessageParts = new ArrayList<>();
        logger = LoggerFactory.getLogger(LoggerBuilderImpl.class);
    }

    /**
     * Resets the builder to its initial state with empty log message.
     */
    @Override
    public void reset() {
        this.logMessageParts.clear();
    }

    /**
     * Sets which delimiter will be used to separate log message parts.
     *
     * @param delimiter Delimiter String
     */
    @Override
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Change the class for the logger to set to which class should the logger point.
     *
     * @param clazz Class to which I want to display in log origin
     */
    @Override
    public void setLoggerClass(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Adds request URI to the log message.
     *
     * @param requestURI request URI
     */
    @Override
    public void setRequestURI(String requestURI) {
        this.logMessageParts.add("Request URI: " + requestURI);
    }

    /**
     * Adds request method to the log message.
     *
     * @param requestMethod request method
     */
    @Override
    public void setRequestMethod(String requestMethod) {
        this.logMessageParts.add("Request Method: " + requestMethod);
    }

    /**
     * Adds request headers to the log message.
     *
     * @param requestHeaders request headers
     */
    @Override
    public void setRequestHeaders(String requestHeaders) {
        this.logMessageParts.add("Request Headers: " + requestHeaders);
    }

    /**
     * Adds response status code to the log message.
     *
     * @param responseStatusCode response status code
     */
    @Override
    public void setResponseStatusCode(String responseStatusCode) {
        this.logMessageParts.add("Response status code: " + responseStatusCode);
    }

    /**
     * Logs the message with TRACE level.
     */
    @Override
    public void trace() {
        logger.trace(String.join(delimiter, logMessageParts));
    }

    /**
     * Logs the message with DEBUG level.
     */
    @Override
    public void debug() {
        logger.debug(String.join(delimiter, logMessageParts));
    }

    /**
     * Logs the message with INFO level.
     */
    @Override
    public void info() {
        logger.info(String.join(delimiter, logMessageParts));
    }

    /**
     * Logs the message with WARN level.
     */
    @Override
    public void warn() {
        logger.warn(String.join(delimiter, logMessageParts));
    }

    /**
     * Logs the message with ERROR level.
     */
    @Override
    public void error() {
        logger.error(String.join(delimiter, logMessageParts));
    }
}
