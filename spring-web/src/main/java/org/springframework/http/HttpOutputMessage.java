package org.springframework.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an HTTP output message, consisting of {@linkplain #getHeaders()}
 * and a writable {@linkplain #getBody()}
 * <p>
 * Typically implemented by an HTTP request handle on the client side,
 * or an HTTP response handle on the server side.
 *
 * @author zhangdd on 2022/7/9
 */
public interface HttpOutputMessage extends HttpMessage {

    /**
     * Return the body of the message as an output stream.
     */
    OutputStream getBody() throws IOException;
}
