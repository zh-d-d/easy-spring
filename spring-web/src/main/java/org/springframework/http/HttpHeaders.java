package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.*;

import java.io.Serializable;
import java.util.*;

/**
 * A data structure representing HTTP request or response headers, mapping String
 * header names to a list of String values, also offering accessors for common
 * application-level data types.
 *
 * @author zhangdd on 2022/7/9
 */
public class HttpHeaders implements MultiValueMap<String, String>, Serializable {

    /**
     * The HTTP {@code Accept} header field name.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">Section 5.3.2 of RFC 7231</a>
     */
    public static final String ACCEPT = "Accept";

    /**
     * The HTTP {@code Content-Length} header field name.
     *
     * @see <a href="https://tools.ietf.org/html/rfc7230#section-3.3.2">Section 3.3.2 of RFC 7230</a>
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    public static final String CONTENT_TYPE = "Content-Type";

    final MultiValueMap<String, String> headers;

    /**
     * Construct a new, empty instance of the {@code HttpHeaders} object.
     * <p>This is the common constructor, using a case-insensitive map structure.
     */
    public HttpHeaders() {
        this(CollectionUtils.toMultiValueMap(new LinkedCaseInsensitiveMap<>(8, Locale.ENGLISH)));
    }

    /**
     * Construct a new {@code HttpHeaders} instance backed by an existing map.
     * <p>This constructor is available as an optimization for adapting to existing
     * headers map structures, primarily for internal use within the framework.
     *
     * @param headers the headers map (expected to operate with case-insensitive keys)
     * @since 5.1
     */
    public HttpHeaders(MultiValueMap<String, String> headers) {
        Assert.notNull(headers, "MultiValueMap must not be null");
        this.headers = headers;
    }

    @Override
    public String getFirst(String headerName) {
        return this.headers.getFirst(headerName);
    }

    @Override
    public void add(String key, String value) {
        this.headers.add(key, value);
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        this.headers.addAll(key, values);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        this.headers.addAll(values);
    }

    @Override
    public void set(String key, String value) {
        this.headers.set(key, value);
    }

    @Override
    public void setAll(Map<String, String> values) {
        this.headers.setAll(values);
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return this.headers.toSingleValueMap();
    }

    @Override
    public int size() {
        return this.headers.size();
    }

    @Override
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return this.headers.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return this.headers.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return this.headers.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        this.headers.putAll(m);
    }

    @Override
    public void clear() {
        this.headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return this.headers.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return this.headers.entrySet();
    }

    @Nullable
    public MediaType getContentType() {
        String value = getFirst(CONTENT_TYPE);
        return (StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null);
    }

    public void setContentType(@Nullable MediaType mediaType) {
        if (mediaType != null) {
            Assert.isTrue(!mediaType.isWildcardType(), "Content-Type cannot contain wildcard type '*'");
            Assert.isTrue(!mediaType.isWildcardSubtype(), "Content-Type cannot contain wildcard subtype '*'");
            set(CONTENT_TYPE, mediaType.toString());
        } else {
            remove(CONTENT_TYPE);
        }
    }

    public long getContentLength() {
        String value = getFirst(CONTENT_LENGTH);
        return (value != null ? Long.parseLong(value) : -1);
    }

    /**
     * Set the length of the body in bytes, as specified by the
     * {@code Content-Length} header.
     */
    public void setContentLength(long contentLength) {
        set(CONTENT_LENGTH, Long.toString(contentLength));
    }

}
