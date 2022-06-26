package org.springframework.web.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Helper class for URL path matching. Provides for URL paths in
 * {@code RequestDispatcher} includes and support for consistent URL decoding.
 *
 * @author zhangdd on 2022/6/19
 */
public class UrlPathHelper {

    private static final String PATH_ATTRIBUTE = UrlPathHelper.class.getName() + ".PATH";

    private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";

    private Log logger = LogFactory.getLog(UrlPathHelper.class);

    @Nullable
    static volatile Boolean websphereComplianceFlag;

    private boolean removeSemicolonContent = true;

    private String defaultEncoding = WebUtils.DEFAULT_CHARACTER_ENCODING;

    private boolean uriDecode = true;


    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     *
     */
    public String resolvedAndCacheLookupPath(HttpServletRequest request) {
        String lookupPath = getLookupPathForRequest(request);
        request.setAttribute(PATH_ATTRIBUTE, lookupPath);
        return lookupPath;
    }

    public static String getResolvedLookupPath(ServletRequest request) {
        String lookupPath = (String) request.getAttribute(PATH_ATTRIBUTE);
        Assert.notNull(lookupPath, "Expected lookupPath in request attribute \"" + PATH_ATTRIBUTE + "\".");
        return lookupPath;
    }

    public String getLookupPathForRequest(HttpServletRequest request) {
        String pathWithinApp = getPathWithinApplication(request);
        String rest = getPathWithinServletMapping(request, pathWithinApp);
        if (StringUtils.hasLength(rest)) {
            return rest;
        } else {
            return pathWithinApp;
        }
    }


    protected String getPathWithinServletMapping(HttpServletRequest request, String pathWithinApp) {
        String servletPath = getServletPath(request);
        String sanitizedPathWithinApp = getSanitizedPath(pathWithinApp);
        String path;

        if (servletPath.contains(sanitizedPathWithinApp)) {
            path = getRemainingPath(sanitizedPathWithinApp, servletPath, false);
        } else {
            path = getRemainingPath(pathWithinApp, servletPath, false);
        }
        if (null != path) {
            return path;
        } else {
            String pathInfo = request.getPathInfo();
            if (null != pathInfo) {
                return pathInfo;
            }
            if (!this.uriDecode) {
                path = getRemainingPath(decodeInternal(request, pathWithinApp), servletPath, false);
            }
            if (null != path) {
                return pathWithinApp;
            }
        }
        return servletPath;
    }

    /**
     * Return the path within the web application for the given request.
     */
    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        //获取去掉contextPath的部分
        String path = getRemainingPath(requestUri, contextPath, true);
        if (path != null) {
            // Normal case: URI contains context path.
            return (StringUtils.hasText(path) ? path : "/");
        } else {
            return requestUri;
        }
    }


    @Nullable
    public String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
        int index1 = 0;
        int index2 = 0;
        for (; (index1 < requestUri.length()) && (index2 < mapping.length()); index1++, index2++) {
            char c1 = requestUri.charAt(index1);
            char c2 = mapping.charAt(index2);
            if (c1 == ';') {
                index1 = requestUri.indexOf('/', index1);
                if (index1 == -1) {
                    return null;
                }
                c1 = requestUri.charAt(index1);
            }
            if (c1 == c2 || (ignoreCase && (Character.toLowerCase(c1) == Character.toLowerCase(c2)))) {
                continue;
            }
            return null;
        }
        if (index2 != mapping.length()) {
            return null;
        } else if (index1 == requestUri.length()) {
            return "";
        } else if (requestUri.charAt(index1) == ';') {
            index1 = requestUri.indexOf('/', index1);
        }
        return (index1 != -1 ? requestUri.substring(index1) : "");
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute(WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE);
        if (null == contextPath) {
            contextPath = request.getContextPath();
        }
        if (StringUtils.matchesCharacter(contextPath, '/')) {
            contextPath = "";
        }
        return decodeRequestString(request, contextPath);
    }

    public String getServletPath(HttpServletRequest request) {
        String servletPath = (String) request.getAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE);
        if (null == servletPath) {
            servletPath = request.getServletPath();
        }
        if (servletPath.length() > 1 && servletPath.endsWith("/") && shouldRemoveTrailingServletPathSlash(request)) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        return servletPath;
    }

    public String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (null == uri) {
            uri = request.getRequestURI();
        }
        return decodeAndCleanUriString(request, uri);
    }


    private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        uri = removeSemicolonContent(uri);
        uri = decodeRequestString(request, uri);
        uri = getSanitizedPath(uri);
        return uri;
    }

    public String removeSemicolonContent(String requestUri) {
        return this.removeSemicolonContent ?
                removeSemicolonContentInternal(requestUri) :
                removeJsessionid(requestUri);
    }

    /**
     * 移除请求URI中所有的分号内容
     * <p>
     * 如 /users/name;v=1.1/gender;value=male
     * 处理后就是/users/name/gender
     */
    private static String removeSemicolonContentInternal(String requestUri) {
        int semicolonIndex = requestUri.indexOf(';');
        if (semicolonIndex == -1) {
            return requestUri;
        }
        StringBuilder sb = new StringBuilder(requestUri);
        while (semicolonIndex != -1) {
            int slashIndex = sb.indexOf("/", semicolonIndex + 1);
            if (slashIndex == -1) {
                return sb.substring(0, semicolonIndex);
            }
            sb.delete(semicolonIndex, slashIndex);
            semicolonIndex = sb.indexOf(";", semicolonIndex);
        }
        return sb.toString();
    }

    /**
     * 移除请求URI中;jsessionid=xxx的部分
     */
    private String removeJsessionid(String requestUri) {
        String key = ";jsessionid=";
        int index = requestUri.toLowerCase().indexOf(key);
        if (index == -1) {
            return requestUri;
        }
        String start = requestUri.substring(0, index);
        for (int i = index + key.length(); i < requestUri.length(); i++) {
            char c = requestUri.charAt(i);
            if (c == ';' || c == '/') {
                return start + requestUri.substring(i);
            }
        }
        return start;
    }

    private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
        if (request.getAttribute(WEBSPHERE_URI_ATTRIBUTE) == null) {
            // Regular servlet container: behaves as expected in any case,
            // so the trailing slash is the result of a "/" url-pattern mapping.
            // Don't remove that slash.
            return false;
        }
        Boolean flagToUse = websphereComplianceFlag;
        if (flagToUse == null) {
            ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
            String className = "com.ibm.ws.webcontainer.WebContainer";
            String methodName = "getWebContainerProperties";
            String propName = "com.ibm.ws.webcontainer.removetrailingservletpathslash";
            boolean flag = false;
            try {
                Class<?> cl = classLoader.loadClass(className);
                Properties prop = (Properties) cl.getMethod(methodName).invoke(null);
                flag = Boolean.parseBoolean(prop.getProperty(propName));
            } catch (Throwable ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not introspect WebSphere web container properties: " + ex);
                }
            }
            flagToUse = flag;
            websphereComplianceFlag = flag;
        }
        // Don't bother if WebSphere is configured to be fully Servlet compliant.
        // However, if it is not compliant, do remove the improper trailing slash!
        return !flagToUse;
    }

    /**
     * 替换“//”为“/”
     * <p>
     * Sanitize the given path. Uses the following rules:
     * <ul>
     * <li>replace all "//" by "/"</li>
     * </ul>
     */
    private static String getSanitizedPath(final String path) {
        int start = path.indexOf("//");
        if (start == -1) {
            return path;
        }
        char[] content = path.toCharArray();
        int slowIndex = start;
        for (int fastIndex = start + 1; fastIndex < content.length; fastIndex++) {
            if (content[fastIndex] != '/' || content[slowIndex] != '/') {
                content[++slowIndex] = content[fastIndex];
            }
        }
        return new String(content, 0, slowIndex + 1);
    }

    public String decodeRequestString(HttpServletRequest request, String source) {
        if (this.uriDecode) {
            return decodeInternal(request, source);
        }
        return source;
    }

    private String decodeInternal(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return UriUtils.decode(source, enc);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not decode request string [" + source + "] with encoding '" + enc +
                        "': falling back to platform default encoding; exception message: " + e.getMessage());
            }
            return URLDecoder.decode(source);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (null == enc) {
            enc = getDefaultEncoding();
        }
        return enc;
    }
}
