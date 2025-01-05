package org.fm.fury.converter;

import com.google.common.collect.Lists;
import org.apache.fury.BaseFury;
import org.apache.fury.io.FuryInputStream;
import org.fm.fury.FuryMediaType;
import org.fm.fury.annotation.FuryObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FuryMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final List<MediaType> mimeTypeList = Lists.newArrayList();

    static {
        mimeTypeList.add(new MediaType("application", FuryMediaType.furySubType, DEFAULT_CHARSET));
        mimeTypeList.add(new MediaType("application", FuryMediaType.allFurySubType, DEFAULT_CHARSET));
    }

    private final BaseFury fury;

    public FuryMessageConverter(BaseFury fury) {
        this.fury = fury;
        this.setSupportedMediaTypes(mimeTypeList);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        return Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().getName().equals(FuryObject.class.getName()));
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream body = inputMessage.getBody();
        FuryInputStream furyInputStream = new FuryInputStream(body);
        return fury.deserialize(furyInputStream);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        byte[] serialize = fury.serialize(object);
        StreamUtils.copy(serialize, outputMessage.getBody());
    }
}
