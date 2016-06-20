package com.kodcu.util.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.LongCodec;

/**
 * @author wwinder
 *         Created on: 5/27/16
 */
public class CustomLongCodec extends LongCodec {
    @Override
    public void encode(final BsonWriter writer, final Long value, final EncoderContext encoderContext) {
        writer.writeString(value.toString());
    }
}
