package org.springframework.web.accept;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Central class to determine requested {@linkplain MediaType}
 * for a request. This is done by delegating to a list of configured
 * {@code ContentNegotiationStrategy} instances.
 *
 * @author zhangdd on 2022/7/9
 */
public class ContentNegotiationManager implements ContentNegotiationStrategy {

    private final List<ContentNegotiationStrategy> strategies = new ArrayList<>();

    public ContentNegotiationManager() {
        this(new HeaderContentNegotiationStrategy());
    }

    public ContentNegotiationManager(ContentNegotiationStrategy... strategies) {
        this(Arrays.asList(strategies));
    }

    public ContentNegotiationManager(Collection<ContentNegotiationStrategy> strategies) {
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.strategies.addAll(strategies);
    }

    public List<ContentNegotiationStrategy> getStrategies() {
        return strategies;
    }

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        for (ContentNegotiationStrategy strategy : this.strategies) {
            List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
            if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
                continue;
            }
            return mediaTypes;
        }
        return MEDIA_TYPE_ALL_LIST;
    }
}
