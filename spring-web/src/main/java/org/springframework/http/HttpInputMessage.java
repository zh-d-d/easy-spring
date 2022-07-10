package org.springframework.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP input message, consisting of {@linkplain #getHeaders()}
 * and a readable {@linkplain #getBody()}.
 * <p>
 * Typically implemented by an HTTP request handle on the server side,
 * or an HTTP response handle on the client side.
 *
 * @author zhangdd on 2022/7/9
 */
public interface HttpInputMessage extends HttpMessage {

    /**
     * Return the body of the message as an input stream.
     */
    InputStream getBody() throws IOException;
}
