package com.kodcu.util.codecs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.BsonWriter;
import org.bson.codecs.DateCodec;
import org.bson.codecs.EncoderContext;

/**
 * @author wwinder
 *         Created on: 5/27/16
 */
public class CustomDateCodec extends DateCodec {
  private final SimpleDateFormat formatter;
  public CustomDateCodec(String format) {
    formatter = new SimpleDateFormat(format);
  }

  @Override
  public void encode(final BsonWriter writer, final Date value, final EncoderContext encoderContext) {
    writer.writeString(formatter.format(value));
  }
}
