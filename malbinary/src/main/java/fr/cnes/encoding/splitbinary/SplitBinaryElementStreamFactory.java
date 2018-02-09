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
package fr.cnes.encoding.splitbinary;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.DurationDecoder;
import fr.cnes.encoding.base.DurationEncoder;
import fr.cnes.encoding.base.FineTimeDecoder;
import fr.cnes.encoding.base.FineTimeEncoder;
import fr.cnes.encoding.base.JavaDurationDecoder;
import fr.cnes.encoding.base.JavaDurationEncoder;
import fr.cnes.encoding.base.JavaTimeDecoder;
import fr.cnes.encoding.base.JavaTimeEncoder;
import fr.cnes.encoding.base.OpenByteArrayOutputStream;
import fr.cnes.encoding.base.TimeDecoder;
import fr.cnes.encoding.base.TimeEncoder;

public class SplitBinaryElementStreamFactory extends MALElementStreamFactory {

  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(SplitBinaryElementStreamFactory.class.getName());

  public final static String ENCODED_UPDATE_PROPERTY = "fr.cnes.encoding.binary.encoded.update";
  
  public final static String BYTE_ARRAY_STRING_PROPERTY = "fr.cnes.encoding.binary.byte.array.string";
  
  public final static String TIME_ENCODER_PROPERTY = "fr.cnes.encoding.binary.time.encoder";
  
  public final static String TIME_DECODER_PROPERTY = "fr.cnes.encoding.binary.time.decoder";
  
  public final static String FINETIME_ENCODER_PROPERTY = "fr.cnes.encoding.binary.finetime.encoder";
  
  public final static String FINETIME_DECODER_PROPERTY = "fr.cnes.encoding.binary.finetime.decoder";
  
  public final static String DURATION_ENCODER_PROPERTY = "fr.cnes.encoding.binary.duration.encoder";
  
  public final static String DURATION_DECODER_PROPERTY = "fr.cnes.encoding.binary.duration.decoder";

  //private OpenByteArrayOutputStream baos;
  
  private boolean encodedUpdate;
  
  private boolean byteArrayString;
  
  private TimeEncoder timeEncoder;

  private TimeDecoder timeDecoder;
  
  private FineTimeEncoder fineTimeEncoder;

  private FineTimeDecoder fineTimeDecoder;
  
  private DurationEncoder durationEncoder;
  
  private DurationDecoder durationDecoder;
  
  // TOTO (AF):
//  private boolean varintSupported;
  
  public SplitBinaryElementStreamFactory() {

  }

  // TODO (AF):
//  public boolean isVarintSupported() {
//    return varintSupported;
//  }
//
//  public void setVarintSupported(boolean varintSupported) {
//    this.varintSupported = varintSupported;
//  }

  public boolean isEncodedUpdate() {
    return encodedUpdate;
  }

  public void setEncodedUpdate(boolean encodedUpdate) {
    this.encodedUpdate = encodedUpdate;
  }

  public boolean isByteArrayString() {
    return byteArrayString;
  }

  public void setByteArrayString(boolean byteArrayString) {
    this.byteArrayString = byteArrayString;
  }

  public TimeEncoder getTimeEncoder() {
    return timeEncoder;
  }

  public void setTimeEncoder(TimeEncoder timeEncoder) {
    this.timeEncoder = timeEncoder;
  }

  public TimeDecoder getTimeDecoder() {
    return timeDecoder;
  }

  public void setTimeDecoder(TimeDecoder timeDecoder) {
    this.timeDecoder = timeDecoder;
  }

  public FineTimeEncoder getFineTimeEncoder() {
    return fineTimeEncoder;
  }

  public void setFineTimeEncoder(FineTimeEncoder fineTimeEncoder) {
    this.fineTimeEncoder = fineTimeEncoder;
  }

  public FineTimeDecoder getFineTimeDecoder() {
    return fineTimeDecoder;
  }

  public void setFineTimeDecoder(FineTimeDecoder fineTimeDecoder) {
    this.fineTimeDecoder = fineTimeDecoder;
  }

  public DurationEncoder getDurationEncoder() {
    return durationEncoder;
  }

  public void setDurationEncoder(DurationEncoder durationEncoder) {
    this.durationEncoder = durationEncoder;
  }

  public DurationDecoder getDurationDecoder() {
    return durationDecoder;
  }

  public void setDurationDecoder(DurationDecoder durationDecoder) {
    this.durationDecoder = durationDecoder;
  }

  public MALElementInputStream createInputStream(InputStream is) throws MALException {
    if (is == null)
      throw new IllegalArgumentException("Null input stream");
    SplitBinaryElementInputStream eis;
	try {
		eis = new SplitBinaryElementInputStream(is,
		    encodedUpdate, byteArrayString, timeDecoder, fineTimeDecoder,
		    durationDecoder);
	} catch (Exception exc) {
		throw new MALException(exc.getMessage());
	}
    eis.setVarintSupported(true);
    return eis;
  }

  public MALElementOutputStream createOutputStream(OutputStream os) throws MALException {
    if (os == null)
      throw new IllegalArgumentException("Null output stream");
    SplitBinaryElementOutputStream eos = new SplitBinaryElementOutputStream(os,
        encodedUpdate, byteArrayString, timeEncoder, fineTimeEncoder,
        durationEncoder);
    eos.setVarintSupported(true);
    return eos;
  }

  protected void init(String protocol, Map properties) throws MALException {
    if (protocol == null)
      throw new IllegalArgumentException("Null protocol");
    encodedUpdate = getBooleanProperty(ENCODED_UPDATE_PROPERTY, properties, true);
    byteArrayString = getBooleanProperty(BYTE_ARRAY_STRING_PROPERTY, properties);
    timeEncoder = (TimeEncoder) newInstance(TIME_ENCODER_PROPERTY, properties);
    if (timeEncoder == null) {
      timeEncoder = new JavaTimeEncoder();
    }
    timeDecoder = (TimeDecoder) newInstance(TIME_DECODER_PROPERTY, properties);
    if (timeDecoder == null) {
      timeDecoder = new JavaTimeDecoder();
    }
    fineTimeEncoder = (FineTimeEncoder) newInstance(FINETIME_ENCODER_PROPERTY, properties);
    if (fineTimeEncoder == null) {
      fineTimeEncoder = new JavaTimeEncoder();
    }
    fineTimeDecoder = (FineTimeDecoder) newInstance(FINETIME_DECODER_PROPERTY, properties);
    if (fineTimeDecoder == null) {
      fineTimeDecoder = new JavaTimeDecoder();
    }
    durationEncoder = (DurationEncoder) newInstance(DURATION_ENCODER_PROPERTY, properties);
    if (durationEncoder == null) {
      durationEncoder = new JavaDurationEncoder();
    }
    durationDecoder = (DurationDecoder) newInstance(DURATION_DECODER_PROPERTY, properties);
    if (durationDecoder == null) {
      durationDecoder = new JavaDurationDecoder();
    }
  }
  
  static Object newInstance(String propName, Map properties) throws MALException {
    String className = (String) properties.get(propName);
    if (className != null) {
      try {
        Class clazz = Class.forName(className);
          return clazz.newInstance();
      } catch (ClassNotFoundException e) {
        throw new MALException("", e);
      } catch (InstantiationException e) {
        throw new MALException("", e);
      } catch (IllegalAccessException e) {
        throw new MALException("", e);
      }
    }
    return null;
  }
  
  static boolean getBooleanProperty(String propName, Map properties,
      boolean defaultValue) {
    Object prop = properties.get(propName);
    if (prop != null) {
      if (prop instanceof String) {
        return Boolean.parseBoolean((String) prop);
      } else {
        return ((Boolean) prop).booleanValue();
      }
    } else {
      return defaultValue;
    }
  }
  
  static boolean getBooleanProperty(String propName, Map properties) {
    Object prop = properties.get(propName);
    if (prop != null) {
      if (prop instanceof String) {
        return Boolean.parseBoolean((String) prop);
      } else {
        return ((Boolean) prop).booleanValue();
      }
    } else {
      return false;
    }
  }

  @Override
  public MALElementInputStream createInputStream(byte[] bytes, int offset)
      throws IllegalArgumentException, MALException {
    if (bytes == null)
      throw new IllegalArgumentException("Null input stream");
    SplitBinaryElementInputStream eis;
	try {
		eis = new SplitBinaryElementInputStream(bytes, offset, encodedUpdate, 
		    byteArrayString, timeDecoder, fineTimeDecoder, durationDecoder);
	} catch (Exception exc) {
		throw new MALException(exc.getMessage());
	}
    eis.setVarintSupported(true);
    return eis;
  }

  @Override
  public Blob encode(Object[] elements, MALEncodingContext ctx)
      throws MALException {
    if (elements == null)
      throw new IllegalArgumentException("Null elements");
    if (ctx == null)
      throw new IllegalArgumentException("Null MALEncodingContext");
    OpenByteArrayOutputStream baos = new OpenByteArrayOutputStream();
    SplitBinaryElementOutputStream eos = new SplitBinaryElementByteArrayOutputStream(
        baos, encodedUpdate, byteArrayString, timeEncoder, fineTimeEncoder,
        durationEncoder);
    eos.setVarintSupported(true);
    for (int i = 0; i < elements.length; i++) {
      ctx.setBodyElementIndex(i);
      eos.writeElement(elements[i], ctx);
    }
    Blob res = new Blob(baos.getInternalArray(), 0, baos.size());
    baos.reset();
    return res;
  }
}
