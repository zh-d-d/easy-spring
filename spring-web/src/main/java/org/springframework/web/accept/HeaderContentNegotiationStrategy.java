package org.springframework.web.accept;

import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

/**
 * A ContentNegotiationStrategy that checks the 'Accept' request header.
 *
 * @author zhangdd on 2022/7/9
 */
public class HeaderContentNegotiationStrategy implements ContentNegotiationStrategy {


    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request)
            throws HttpMediaTypeNotAcceptableException {
//        String[] headerValueArray = request.getHeaderValues(HttpHeaders.ACCEPT);
//        if (null==headerValueArray){
//            return MEDIA_TYPE_ALL_LIST;
//        }

        return MEDIA_TYPE_ALL_LIST;
    }
}
