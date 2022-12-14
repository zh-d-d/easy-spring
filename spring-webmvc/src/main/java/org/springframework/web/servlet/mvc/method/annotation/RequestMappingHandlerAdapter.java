package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangdd on 2022/7/2
 */
public class RequestMappingHandlerAdapter extends AbstractHandlerMethodAdapter
        implements InitializingBean {

    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    private List<HttpMessageConverter<?>> messageConverters;


    public RequestMappingHandlerAdapter() {
        this.messageConverters = new ArrayList<>(4);
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return messageConverters;
    }

    /**
     * Always return true since any method argument and return value
     * type will be processed in some way. A method argument not recoginzed
     * by any HandlerMethodArgumentResolver is interpreted as a request parameter
     * if it is a simple type, or as a model attribute otherwise. A return value
     * not recoginzed by any HandlerMethodReturnValueHandler will be interpreted
     * as a model attribute
     */
    @Override
    protected boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }

    @Override
    protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return invokeHandlerMethod(request, response, handlerMethod);
    }

    @Nullable
    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);

        if (this.returnValueHandlers != null) {
            invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        //??????@ResponseBody??????????????????????????????????????????????????????????????????response???body???
        invocableMethod.invokeAndHandle(webRequest, mavContainer);

        //??????????????????ModelAndView, ??????@ResponseBody???????????????????????????response???body???, ???????????????null
//        return getModelAndView(mavContainer, modelFactory, webRequest);
        return null;
    }


    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new ServletInvocableHandlerMethod(handlerMethod);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }

    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(20);
//        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(),
//                this.contentNegotiationManager, this.requestResponseBodyAdvice));
        handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters()));

        return handlers;
    }
}
