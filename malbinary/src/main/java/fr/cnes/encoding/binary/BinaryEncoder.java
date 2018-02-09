/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017  2018 CNES
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
  *******************************************************************************/
package fr.cnes.encoding.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListEncoder;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.BinaryListEncoder;
import fr.cnes.encoding.base.DurationEncoder;
import fr.cnes.encoding.base.Encoder;
import fr.cnes.encoding.base.FineTimeEncoder;
import fr.cnes.encoding.base.TimeEncoder;
import fr.cnes.encoding.binary.OutputStreamEncoder;

public class BinaryEncoder implements MALEncoder {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(BinaryEncoder.class.getName());

  private Encoder encoder;
  
  private boolean byteArrayString;
  
  private TimeEncoder timeEncoder;
  
  private FineTimeEncoder fineTimeEncoder;
  
  private DurationEncoder durationEncoder;

  public BinaryEncoder(OutputStream os, boolean byteArrayString, 
      TimeEncoder timeEncoder, FineTimeEncoder fineTimeEncoder,
      DurationEncoder durationEncoder) {
    encoder = new OutputStreamEncoder(os);
    this.byteArrayString = byteArrayString;
    this.timeEncoder = timeEncoder;
    this.fineTimeEncoder = fineTimeEncoder;
    this.durationEncoder = durationEncoder;
  }
  
  public boolean isVarintSupported() {
    return encoder.isVarintSupported();
  }

  public void setVarintSupported(boolean varintSupported) {
    encoder.setVarintSupported(varintSupported);
  }
  
  public boolean isByteArrayString() {
    return byteArrayString;
  }

  public Encoder getEncoder() {
    return encoder;
  }

  public void encodeNullableBoolean(Boolean element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeBoolean(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeBoolean(Boolean element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeBoolean(element.booleanValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableInteger(Integer element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeInteger(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeInteger(Integer element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeSignedInt(element.intValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableString(String element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeString(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeString(String element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeString(element);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableURI(URI element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeString(element.getValue());
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeURI(URI element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeString(element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableIdentifier(Identifier element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeString(element.getValue());
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeIdentifier(Identifier element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeString(element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableAttribute(Attribute element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeAttribute(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeAttribute(Attribute element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    int typeShortForm = element.getTypeShortForm().intValue();
    try {
      encoder.writeByte((byte) (typeShortForm - 1));
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
    /*
    if (byteArrayString && typeShortForm == Attribute._STRING_TYPE_SHORT_FORM &&
        element instanceof ByteArrayString) {
      byte[] bytes = ((ByteArrayString) element).getBytes();
      try {
        encoder.writeByteArray(bytes);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "", e);
        throw new MALException(e.toString(), e);
      }
    } else {
    */
    element.encode(this);
  }
  
  public void encodeNullableBlob(Blob element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNullableByteArray(null);
      } else {
        encoder.writeNullableByteArray(element.getValue());
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeBlob(Blob element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeByteArray(element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableDuration(Duration element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeDuration(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeDuration(Duration element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      durationEncoder.encode(element.getValue(), encoder);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableFloat(Float element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeFloat(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeFloat(Float element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encodeInteger(Float.floatToIntBits(element.floatValue()));
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableDouble(Double element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeDouble(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeDouble(Double element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encodeLong(Double.doubleToLongBits(element.doubleValue()));
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableOctet(Byte element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeOctet(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeOctet(Byte element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeByte(element.byteValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableShort(Short element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeShort(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeShort(Short element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeSignedShort(element.shortValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableLong(Long element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeLong(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeLong(Long element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeSignedLong(element.longValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableTime(Time element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeTime(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeTime(Time element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      timeEncoder.encode(element.getValue(), encoder);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableFineTime(FineTime element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeFineTime(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeFineTime(FineTime element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      fineTimeEncoder.encode(element.getValue(), encoder);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public MALListEncoder createListEncoder(List list) throws MALException {
    try {
      encoder.writeUnsignedInt(list.size());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
    BinaryListEncoder listEncoder = new BinaryListEncoder(this);
    return listEncoder;
  }

  public void flush() throws IOException {
    encoder.flush();
  }

  public void encodeNullableElement(Element element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeElement(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeElement(Element element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    element.encode(this);
  }
  
  public void encodeNullableUInteger(UInteger element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeUInteger(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeUInteger(UInteger element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeUnsignedInt((int) element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableULong(ULong element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeULong(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeULong(ULong element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeUnsignedLong(element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeNullableUOctet(UOctet element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeUOctet(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeUOctet(UOctet element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeByte((byte) element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public void encodeNullableUShort(UShort element) throws MALException {
    try {
      if (element == null) {
        encoder.writeNull();
      } else {
        encoder.writeNotNull();
        encodeUShort(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public void encodeUShort(UShort element) throws MALException {
    if (element == null) throw new IllegalArgumentException("Null element");
    try {
      encoder.writeUnsignedShort((short) element.getValue());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
}
