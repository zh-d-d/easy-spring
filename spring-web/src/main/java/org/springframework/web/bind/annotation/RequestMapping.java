package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangdd on 2022/6/18
 */
@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {

    /**
     * Assign a name to this mapping.
     * <p>
     * Supported at the type level as well as at the method level! When used on both
     * levels, a combined name is derived by concatenation with "#" as separator.
     */
    String name() default "";

    /**
     * The primary mapping expressed by this annotation.
     * <p>
     * This is an alias for {@link #path()}. For example, {@code @RequestMapping("/foo")} is equivalent to
     * {@code @RequestMapping(path="/foo")}
     * <p>
     * Supported at the type level as well as at the method level! When use at the
     * type level, all method-level mappings inherit this primary mapping, narrowing
     * it for a specific handler method.
     */
    @AliasFor("path")
    String[] value() default {};

    @AliasFor("value")
    String[] path() default {};
}
