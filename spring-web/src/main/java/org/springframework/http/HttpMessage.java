package org.springframework.http;

/**
 * Represents the base interface for HTTP request and response message. Consists of
 * {@link HttpHeaders}, retrievable via{@link #getHeaders()}
 *
 * @author zhangdd on 2022/7/9
 */
public interface HttpMessage {

    /**
     * Return the header of this message.
     */
    HttpHeaders getHeaders();
}
