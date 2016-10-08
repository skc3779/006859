package readinglist;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NonTypedScalarSerializerBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by seokangchun on 2016. 10. 8..
 */
@Configuration
public class CommonConfiguration {
    @Bean
    public ObjectMapper objectMapper() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule("BooleanAsString", new com.fasterxml.jackson.core.Version(1, 0, 0, null, null, null));
        module.addSerializer(new NonTypedScalarSerializerBase<Boolean>(Boolean.class) {
            @Override
            public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider)
                    throws IOException {
                jgen.writeString(value.toString());
            }
        });
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        return objectMapper;
    }
}
