package com.fh.filter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class ToolKitsObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 4359709211352400087L;

    public ToolKitsObjectMapper(){
        SimpleModule module = new SimpleModule("XssStringJsonSerializer");
        module.addDeserializer(String.class, new JsonHtmlXssDeserializer(String.class));
        this.registerModule(module);
    };

    /*
        对出参进行转义
     */
    class JsonHtmlXssSerializer extends JsonSerializer<String>{

        public JsonHtmlXssSerializer(Class<String> string){
            super();
        }

        @Override
        public Class<String> handledType(){
            return String.class;
        }

        @Override
        public void serialize(String value,
                              JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            if(!StringUtils.isEmpty(value)){
                String encodedValue = HtmlUtils.htmlEscape(value);
                jsonGenerator.writeString(encodedValue);
            }
        }
    }

    /**
     * 对入参进行转义
     */
    class JsonHtmlXssDeserializer extends JsonDeserializer<String>{
        public JsonHtmlXssDeserializer(Class<String> string){
            super();
        }

        @Override
        public String deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String value = jsonParser.getValueAsString();
            if(!StringUtils.isEmpty(value)){
                return value.replaceAll("<", "＜").replaceAll(">","＞");
            }
            return value;
        }

        @Override
        public Class<String> handledType(){
            return String.class;
        }
    }
}
