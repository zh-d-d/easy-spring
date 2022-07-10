package org.springframework.http.server;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangdd on 2022/7/3
 */
public class ServletServerHttpResponse implements ServerHttpResponse {

    private final HttpServletResponse servletResponse;

    private final HttpHeaders headers;

    private boolean headersWritten = false;



    public ServletServerHttpResponse(HttpServletResponse servletResponse) {
        Assert.notNull(servletResponse, "HttpServletResponse must not be null");
        this.servletResponse = servletResponse;
        this.headers = new ServletResponseHttpHeaders();
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    @Override
    public HttpHeaders getHeaders() {

        return this.headers;
    }

    @Override
    public OutputStream getBody() throws IOException {
        return this.servletResponse.getOutputStream();
    }

    /**
     * Extends HttpHeaders with the ability to look up headers already present in
     * the underlying HttpServletResponse.
     *
     * <p>The intent is merely to expose what is available through the HttpServletResponse
     * i.e. the ability to look up specific header values by name. All other
     * map-related operations (e.g. iteration, removal, etc) apply only to values
     * added directly through HttpHeaders methods.
     *
     * @since 4.0.3
     */
    private class ServletResponseHttpHeaders extends HttpHeaders {

        private static final long serialVersionUID = 3410708522401046302L;

        @Override
        public boolean containsKey(Object key) {
            return (super.containsKey(key) || (get(key) != null));
        }

        @Override
        @Nullable
        public String getFirst(String headerName) {
            if (headerName.equalsIgnoreCase(CONTENT_TYPE)) {
                // Content-Type is written as an override so check super first
                String value = super.getFirst(headerName);
                return (value != null ? value : servletResponse.getHeader(headerName));
            } else {
                String value = servletResponse.getHeader(headerName);
                return (value != null ? value : super.getFirst(headerName));
            }
        }

        @Override
        public List<String> get(Object key) {
            Assert.isInstanceOf(String.class, key, "Key must be a String-based header name");

            String headerName = (String) key;
            if (headerName.equalsIgnoreCase(CONTENT_TYPE)) {
                // Content-Type is written as an override so don't merge
                return Collections.singletonList(getFirst(headerName));
            }

            Collection<String> values1 = servletResponse.getHeaders(headerName);
            if (headersWritten) {
                return new ArrayList<>(values1);
            }
            boolean isEmpty1 = CollectionUtils.isEmpty(values1);

            List<String> values2 = super.get(key);
            boolean isEmpty2 = CollectionUtils.isEmpty(values2);

            if (isEmpty1 && isEmpty2) {
                return null;
            }

            List<String> values = new ArrayList<>();
            if (!isEmpty1) {
                values.addAll(values1);
            }
            if (!isEmpty2) {
                values.addAll(values2);
            }
            return values;
        }
    }

}
