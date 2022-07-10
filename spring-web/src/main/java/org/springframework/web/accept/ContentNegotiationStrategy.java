package org.springframework.web.accept;

import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Collections;
import java.util.List;

/**
 * A strategy for resolving the requested media types for a request.
 *
 * @author zhangdd on 2022/7/9
 */
public interface ContentNegotiationStrategy {

    List<MediaType> MEDIA_TYPE_ALL_LIST = Collections.singletonList(MediaType.ALL);

    /**
     * Resolve the given request to a list of media type. The return list is
     * ordered by specificity first and by quality parameter second.
     */
    List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
            throws HttpMediaTypeNotAcceptableException;
}
