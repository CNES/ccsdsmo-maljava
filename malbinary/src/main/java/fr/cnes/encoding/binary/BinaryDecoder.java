/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 CNES
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

import java.util.List;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;
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
import org.ccsds.moims.mo.mal.structures.factory.BlobFactory;
import org.ccsds.moims.mo.mal.structures.factory.BooleanFactory;
import org.ccsds.moims.mo.mal.structures.factory.DoubleFactory;
import org.ccsds.moims.mo.mal.structures.factory.DurationFactory;
import org.ccsds.moims.mo.mal.structures.factory.FineTimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.FloatFactory;
import org.ccsds.moims.mo.mal.structures.factory.IdentifierFactory;
import org.ccsds.moims.mo.mal.structures.factory.IntegerFactory;
import org.ccsds.moims.mo.mal.structures.factory.LongFactory;
import org.ccsds.moims.mo.mal.structures.factory.OctetFactory;
import org.ccsds.moims.mo.mal.structures.factory.ShortFactory;
import org.ccsds.moims.mo.mal.structures.factory.StringFactory;
import org.ccsds.moims.mo.mal.structures.factory.TimeFactory;
import org.ccsds.moims.mo.mal.structures.factory.UIntegerFactory;
import org.ccsds.moims.mo.mal.structures.factory.ULongFactory;
import org.ccsds.moims.mo.mal.structures.factory.UOctetFactory;
import org.ccsds.moims.mo.mal.structures.factory.URIFactory;
import org.ccsds.moims.mo.mal.structures.factory.UShortFactory;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.binary.Decoder;
import fr.cnes.encoding.binary.DurationDecoder;
import fr.cnes.encoding.binary.FineTimeDecoder;
import fr.cnes.encoding.binary.TimeDecoder;

public class BinaryDecoder implements MALDecoder {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(BinaryDecoder.class.getName());
  
  private static MALElementFactory[] attributeFactories;
  
  static {
    attributeFactories = new MALElementFactory[Attribute._URI_TYPE_SHORT_FORM + 1];
    attributeFactories[Attribute._BLOB_TYPE_SHORT_FORM] = new BlobFactory();
    attributeFactories[Attribute._BOOLEAN_TYPE_SHORT_FORM] = new BooleanFactory();
    attributeFactories[Attribute._DOUBLE_TYPE_SHORT_FORM] = new DoubleFactory();
    attributeFactories[Attribute._DURATION_TYPE_SHORT_FORM] = new DurationFactory();
    attributeFactories[Attribute._FINETIME_TYPE_SHORT_FORM] = new FineTimeFactory();
    attributeFactories[Attribute._FLOAT_TYPE_SHORT_FORM] = new FloatFactory();
    attributeFactories[Attribute._IDENTIFIER_TYPE_SHORT_FORM] = new IdentifierFactory();
    attributeFactories[Attribute._INTEGER_TYPE_SHORT_FORM] = new IntegerFactory();
    attributeFactories[Attribute._LONG_TYPE_SHORT_FORM] = new LongFactory();
    attributeFactories[Attribute._OCTET_TYPE_SHORT_FORM] = new OctetFactory();
    attributeFactories[Attribute._SHORT_TYPE_SHORT_FORM] = new ShortFactory();
    attributeFactories[Attribute._STRING_TYPE_SHORT_FORM] = new StringFactory();
    attributeFactories[Attribute._TIME_TYPE_SHORT_FORM] = new TimeFactory();
    attributeFactories[Attribute._UINTEGER_TYPE_SHORT_FORM] = new UIntegerFactory();
    attributeFactories[Attribute._ULONG_TYPE_SHORT_FORM] = new ULongFactory();
    attributeFactories[Attribute._UOCTET_TYPE_SHORT_FORM] = new UOctetFactory();
    attributeFactories[Attribute._URI_TYPE_SHORT_FORM] = new URIFactory();
    attributeFactories[Attribute._USHORT_TYPE_SHORT_FORM] = new UShortFactory();
  }
  
  private Decoder decoder;
  
  private boolean byteArrayString;
  
  private TimeDecoder timeDecoder;
  
  private FineTimeDecoder fineTimeDecoder;
  
  private DurationDecoder durationDecoder;

  public BinaryDecoder(Decoder decoder, boolean byteArrayString, 
      TimeDecoder timeDecoder, FineTimeDecoder fineTimeDecoder,
      DurationDecoder durationDecoder) {
    this.decoder = decoder;
    this.byteArrayString = byteArrayString;
    this.timeDecoder = timeDecoder;
    this.fineTimeDecoder = fineTimeDecoder;
    this.durationDecoder = durationDecoder;
  }
  
  public Decoder getDecoder() {
    return decoder;
  }

  public Boolean decodeNullableBoolean() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeBoolean();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Boolean decodeBoolean() throws MALException {
    try {
      return new Boolean(decoder.readBoolean());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public Integer decodeNullableInteger() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeInteger();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Integer decodeInteger() throws MALException {
    try {
      return new Integer(decoder.readSignedInt());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public String decodeNullableString() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeString();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public String decodeString() throws MALException {
    try {
      return decoder.readString();
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public URI decodeNullableURI() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeURI();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public URI decodeURI() throws MALException {
    try {
      String s = decoder.readString();
      return new URI(s);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public Identifier decodeNullableIdentifier() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeIdentifier();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Identifier decodeIdentifier() throws MALException {
    try {
      String s = decoder.readString();
      return new Identifier(s);
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Element decodeNullableElement(Element element) throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeElement(element);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }
  
  public Element decodeElement(Element element) throws MALException {
    Element decodedElement = element.decode(this);
    return decodedElement;
  }

  public Blob decodeNullableBlob() throws MALException {
    try {
      byte[] ba = decoder.readNullableByteArray();
      if (ba == null) {
        return null; 
      } else {
        return new Blob(ba);
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public Blob decodeBlob() throws MALException {
    try {
      byte[] ba = decoder.readByteArray();
      return new Blob(ba);
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public Duration decodeNullableDuration() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeDuration();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Duration decodeDuration() throws MALException {
    try {
      return new Duration(durationDecoder.decode(decoder));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public Float decodeNullableFloat() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeFloat();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Float decodeFloat() throws MALException {
    try {
      return new Float(Float.intBitsToFloat(decoder.read32()));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Double decodeNullableDouble() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeDouble();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Double decodeDouble() throws MALException {
    try {
      return new Double(Double.longBitsToDouble(decoder.read64()));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public Byte decodeNullableOctet() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeOctet();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Byte decodeOctet() throws MALException {
    try {
      return new Byte(decoder.readByte());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public Short decodeNullableShort() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeShort();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Short decodeShort() throws MALException {
    try {
      return new Short(decoder.readSignedShort());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Long decodeNullableLong() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeLong();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Long decodeLong() throws MALException {
    try {
      return new Long(decoder.readSignedLong());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Time decodeNullableTime() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeTime();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Time decodeTime() throws MALException {
    try {
      return new Time(timeDecoder.decode(decoder));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public FineTime decodeNullableFineTime() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeFineTime();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public FineTime decodeFineTime() throws MALException {
    try {
      return new FineTime(fineTimeDecoder.decode(decoder));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public MALListDecoder createListDecoder(List list) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "JORAMDecoder.createListDecoder(" +
          list + ')');
    try {
      int size = decoder.readUnsignedInt();
      BinaryListDecoder listDecoder = new BinaryListDecoder(list, size, this);
      return listDecoder;
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
  }

  public Attribute decodeAttribute() throws MALException {
    Attribute element;
    try {
      int typeShortForm = decoder.readByte() + 1;
      /*
      if (byteArrayString && typeShortForm == Attribute._STRING_TYPE_SHORT_FORM) {
        byte[] bytes = decoder.readByteArray();
        element = new ByteArrayString(bytes);
      } else {
      */
      element = (Attribute) attributeFactories[typeShortForm].createElement();
      element = (Attribute) element.decode(this);
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw new MALException(e.toString(), e);
    }
    return element;
  }
  
  public UOctet decodeNullableUOctet() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeUOctet();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public UOctet decodeUOctet() throws MALException {
    try {
      return new UOctet((short) (decoder.readByte() & 0xFF));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public UShort decodeNullableUShort() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeUShort();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public UShort decodeUShort() throws MALException {
    try {
      return new UShort((int) (decoder.readUnsignedShort() & 0xFFFF));
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public UInteger decodeNullableUInteger() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeUInteger();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public UInteger decodeUInteger() throws MALException {
    try {
      return new UInteger(decoder.readUnsignedInt() & 0xFFFFFFFFL);
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public ULong decodeNullableULong() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeULong();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public ULong decodeULong() throws MALException {
    try {
      return new ULong(decoder.readUnsignedLong());
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }

  public Attribute decodeNullableAttribute() throws MALException {
    try {
      if (decoder.isNull()) {
        return null;
      } else {
        return decodeAttribute();
      }
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  public void close() throws Exception {
    decoder.close();
  }
}
