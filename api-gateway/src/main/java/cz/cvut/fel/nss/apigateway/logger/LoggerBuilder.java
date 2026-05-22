package cz.cvut.fel.nss.apigateway.logger;

/**
 * Interface is for building log messages and logging them with given severity level. Default delimiter is ';'
 */
public interface LoggerBuilder {

    /**
     * Resets the builder to its initial state with empty log message.
     */
    void reset();

    /**
     * Sets which delimiter will be used to separate log message parts.
     * @param delimiter Delimiter String
     */
    void setDelimiter(String delimiter);

    /**
     * Change the class for the logger to set to which class should the logger point.
     * @param clazz Class to which I want to display in log origin
     */
    void setLoggerClass(Class<?> clazz);

    /**
     * Adds request URI to the log message.
     * @param requestURI request URI
     */
    void setRequestURI(String requestURI);

    /**
     * Adds request method to the log message.
     * @param requestMethod request method
     */
    void setRequestMethod(String requestMethod);

    /**
     * Adds request headers to the log message.
     * @param requestHeaders request headers
     */
    void setRequestHeaders(String requestHeaders);

    /**
     * Adds response status code to the log message.
     * @param responseStatusCode response status code
     */
    void setResponseStatusCode(String responseStatusCode);

    /**
     * Logs the message with TRACE level.
     */
    void trace();

    /**
     * Logs the message with DEBUG level.
     */
    void debug();

    /**
     * Logs the message with INFO level.
     */
    void info();

    /**
     * Logs the message with WARN level.
     */
    void warn();

    /**
     * Logs the message with ERROR level.
     */
    void error();
}
