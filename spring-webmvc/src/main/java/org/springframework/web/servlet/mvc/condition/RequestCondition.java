package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * Contract for request mapping conditions.
 * <p>
 * Request conditions can be combined via {@link #combine(Object)}, matched to
 * a request via {@link #getMatchingCondition(HttpServletRequest)}, and compared
 * to each other via {@link #compareTo(Object, HttpServletRequest)} to determine
 * which is a closer match for a given request.
 *
 * @author zhangdd on 2022/6/18
 */
public interface RequestCondition<T> {

    /**
     * Combine this condition with another such as conditions from a type-level and
     * method-level {@code RequestMapping} annotation.
     */
    T combine(T other);

    /**
     * Check if the condition matches the request returning a potentially new
     * instance created for the current request. For example a condition with
     * multiple URL patterns may return a new instance only with those patterns that
     * match the request.
     */
    @Nullable
    T getMatchingCondition(HttpServletRequest request);
}
