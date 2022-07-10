package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;

/**
 * @author zhangdd on 2022/7/9
 */
public class Jackson2ObjectMapperBuilder {

    private boolean createXmlMapper = false;

    @Nullable
    private JsonFactory factory;

    @Nullable
    private Boolean defaultUseWrapper;

    /**
     * Obtain a {@link Jackson2ObjectMapperBuilder} instance in order to
     * build a regular JSON {@link ObjectMapper} instance.
     */
    public static Jackson2ObjectMapperBuilder json() {
        return new Jackson2ObjectMapperBuilder();
    }


    @SuppressWarnings("unchecked")
    public <T extends ObjectMapper> T build(){
//        ObjectMapper mapper;
//        if (this.createXmlMapper){
//            mapper=(null!=this.defaultUseWrapper?
//                    new xmlobj)
//        }else {
//            mapper=null!=this.factory?new ObjectMapper(this.factory):new ObjectMapper();
//        }
//        return mapper;
        return (T) new ObjectMapper();
    }
}
