package org.springframework.http.server;

import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author zhangdd on 2022/7/3
 */
public class ServletServerHttpRequest implements ServerHttpRequest {

    private final HttpServletRequest servletRequest;

    @Nullable
    private HttpHeaders headers;

    public ServletServerHttpRequest(HttpServletRequest servletRequest) {
        Assert.notNull(servletRequest, "HttpServletRequest must not be null");
        this.servletRequest = servletRequest;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();

            for (Enumeration<?> names = this.servletRequest.getHeaderNames(); names.hasMoreElements(); ) {
                String headerName = (String) names.nextElement();
                for (Enumeration<?> headerValues = this.servletRequest.getHeaders(headerName);
                     headerValues.hasMoreElements(); ) {
                    String headerValue = (String) headerValues.nextElement();
                    this.headers.add(headerName, headerValue);
                }
            }

            // HttpServletRequest exposes some headers as properties:
            // we should include those if not already present
            try {
                MediaType contentType = this.headers.getContentType();
                if (contentType == null) {
                    String requestContentType = this.servletRequest.getContentType();
                    if (StringUtils.hasLength(requestContentType)) {
                        contentType = MediaType.parseMediaType(requestContentType);
                        this.headers.setContentType(contentType);
                    }
                }
                if (contentType != null && contentType.getCharset() == null) {
                    String requestEncoding = this.servletRequest.getCharacterEncoding();
                    if (StringUtils.hasLength(requestEncoding)) {
                        Charset charSet = Charset.forName(requestEncoding);
                        Map<String, String> params = new LinkedCaseInsensitiveMap<>();
                        params.putAll(contentType.getParameters());
                        params.put("charset", charSet.toString());
                        MediaType mediaType = new MediaType(contentType.getType(), contentType.getSubtype(), params);
                        this.headers.setContentType(mediaType);
                    }
                }
            } catch (InvalidMediaTypeException ex) {
                // Ignore: simply not exposing an invalid content type in HttpHeaders...
            }

            if (this.headers.getContentLength() < 0) {
                int requestContentLength = this.servletRequest.getContentLength();
                if (requestContentLength != -1) {
                    this.headers.setContentLength(requestContentLength);
                }
            }
        }
        return this.headers;
    }


    @Override
    public InputStream getBody() throws IOException {
        return null;
    }
}
