/**
 * DateCodec.java - Traackr, Inc.
 *
 * This document set is the property of Traackr, Inc., a Massachusetts
 * Corporation, and contains confidential and trade secret information. It
 * cannot be transferred from the custody or control of Traackr except as
 * authorized in writing by an officer of Traackr. Neither this item nor the
 * information it contains can be used, transferred, reproduced, published,
 * or disclosed, in whole or in part, directly or indirectly, except as
 * expressly authorized by an officer of Traackr, pursuant to written
 * agreement.
 *
 * Copyright 2012-2015 Traackr, Inc. All Rights Reserved.
 */
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
