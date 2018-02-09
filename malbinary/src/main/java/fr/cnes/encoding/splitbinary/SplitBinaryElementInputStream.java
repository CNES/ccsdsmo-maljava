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

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALElementFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALOperationStage;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALEncodedElementList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.Decoder;
import fr.cnes.encoding.base.DurationDecoder;
import fr.cnes.encoding.base.FineTimeDecoder;
import fr.cnes.encoding.base.TimeDecoder;

public class SplitBinaryElementInputStream implements MALElementInputStream {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(SplitBinaryElementInputStream.class.getName());
  
  public final static long[] attributeShortForms = {
    Blob.BLOB_SHORT_FORM,
    Union.BOOLEAN_SHORT_FORM,
    Duration.DURATION_SHORT_FORM,
    Union.FLOAT_SHORT_FORM,
    Union.DOUBLE_SHORT_FORM,
    Identifier.IDENTIFIER_SHORT_FORM,
    Union.OCTET_SHORT_FORM,
    UOctet.SHORT_SHORT_FORM,
    Union.SHORT_SHORT_FORM,
    UShort.USHORT_SHORT_FORM,
    Union.INTEGER_SHORT_FORM,
    UInteger.UINTEGER_SHORT_FORM,
    Union.LONG_SHORT_FORM,
    ULong.ULONG_SHORT_FORM,
    Union.STRING_SHORT_FORM,
    Time.TIME_SHORT_FORM,
    FineTime.FINETIME_SHORT_FORM,
    URI.URI_SHORT_FORM
  };
  
  private Decoder decoder;
  
  private SplitBinaryDecoder malDecoder;
  
  private boolean encodedUpdate;
  
  private boolean keepPublishUpdateEncoded;
  
  SplitBinaryElementInputStream(InputStream is, boolean encodedUpdate, 
      boolean byteArrayString, TimeDecoder timeDecoder,
      FineTimeDecoder fineTimeDecoder, DurationDecoder durationDecoder) throws Exception {
    this.encodedUpdate = encodedUpdate;
    decoder = new InputStreamDecoder(is);
    //decoder = new BufferDecoder(new InputStreamReader(is));
    malDecoder = new SplitBinaryDecoder(decoder, byteArrayString, 
        timeDecoder, fineTimeDecoder, durationDecoder);
  }
  
  SplitBinaryElementInputStream(byte[] bytes, int offset, 
      boolean encodedUpdate, boolean byteArrayString, TimeDecoder timeDecoder,
      FineTimeDecoder fineTimeDecoder, DurationDecoder durationDecoder) throws Exception {
    this.encodedUpdate = encodedUpdate;
    decoder = new BufferDecoder(new BufferReader(bytes, offset));
    malDecoder = new SplitBinaryDecoder(decoder, byteArrayString, 
        timeDecoder, fineTimeDecoder, durationDecoder);
  }
  
  public boolean isVarintSupported() {
    return decoder.isVarintSupported();
  }

  public void setVarintSupported(boolean varintSupported) {
    decoder.setVarintSupported(varintSupported);
  }

  private boolean isNull() throws MALException {
    try {
      // Null => isPresent false
      return !malDecoder.getDecoder().readBoolean();
    } catch (Exception e) {
      throw new MALException(e.getMessage(), e);
    }
  }

  public Object readElement(Object object, MALEncodingContext ctx) throws MALException {
    Element element = (Element) object;
    if (ctx == null) {
      return malDecoder.decodeElement(element);
    }
    MALOperation op = ctx.getOperation();
    MALMessageHeader header = ctx.getHeader();
    
    if (logger.isLoggable(BasicLevel.WARN))
      logger.log(BasicLevel.WARN, "Reads op=" + op + ", header=" + header);
    
    if (header.getIsErrorMessage()) {
      switch (ctx.getBodyElementIndex()) {
      case 0:
        // Error number (no polymorphism)
        if (element == null) {
          element = new UInteger();
        }
        return malDecoder.decodeElement(element);
      case 1:
        // Extra information (polymorphism)
        if (isNull()) {
          return null;
        } else {
          Long shortForm;
          try {
            shortForm = malDecoder.getDecoder().read64();
            MALElementFactory elementFactory = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(shortForm);
            if (elementFactory == null) {
              throw new MALException("Element factory not found: " + shortForm);
            } else {
              element = (Element) elementFactory.createElement();
              return malDecoder.decodeElement(element);
            }
          } catch (Exception e) {
            throw new MALException(e.getMessage(), e);
          }
        }
      default:
        throw new MALException("Incorrect body element index: " + ctx.getBodyElementIndex());
      }
    } else {
      if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
        if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          if (encodedUpdate && ctx.getBodyElementIndex() > 0) {
            // Do not decode the updates
            MALOperationStage opStage = resolveOperationStage(ctx);
            try {
              if (keepPublishUpdateEncoded) {
                Object shortForm = resolveShortForm(opStage, ctx);
                Integer listSize = malDecoder.getDecoder().readUnsignedInt();
                MALEncodedElementList malEncodedList = new MALEncodedElementList(shortForm, listSize);
                for (int i = 0; i < listSize; i++) { 
                  if (isNull()) {
                    malEncodedList.add(null);
                  } else {
                    int length = malDecoder.getDecoder().readUnsignedInt();
                    MALEncodedElement encodedElement = new MALEncodedElement(new Blob(decoder.readByteArray(length)));
                    malEncodedList.add(encodedElement);
                  }
                }
                return malEncodedList;
              } else {
                ElementList updateList = (ElementList) checkPassedElement(element, ctx);
                Integer listSize = malDecoder.getDecoder().readUnsignedInt();
                // Apply a mask to get the short form of the list element
                long mask = 0xffffffffff000000L;
                long listType = updateList.getShortForm().longValue() | mask;
                long updateType = -listType;
                long areaServiceVersion = updateList.getShortForm() & mask;
                long updateShortForm = areaServiceVersion | updateType;
                MALElementFactory updateFactory = MALContextFactory
                    .getElementFactoryRegistry().lookupElementFactory(
                        updateShortForm);
                if (updateFactory == null) {
                  String errorInfo = "Update factory not found: " + updateShortForm;
                  if (logger.isLoggable(BasicLevel.DEBUG))
                    logger.log(BasicLevel.DEBUG, errorInfo);
                  throw new MALException(errorInfo);
                }
                for (int i = 0; i < listSize; i++) {
                  if (isNull()) {
                    updateList.add(null);
                  } else {  
                    // Decode the update length but does not use it
                    malDecoder.getDecoder().readUnsignedInt();
                  
                    Element update = (Element) updateFactory.createElement();
                    update = update.decode(malDecoder);
                    
                    if (update instanceof Union) {
                      Union union = (Union) update;
                      updateList.add(getUnionValue(union));
                    } else {
                      updateList.add(update);
                    }
                  }
                }
                return updateList;
              }
            } catch (Exception e) {
              if (logger.isLoggable(BasicLevel.DEBUG))
                logger.log(BasicLevel.DEBUG, "", e);
              throw new MALException("", e);
            }
          } else {
            return doReadElement(element, ctx);
          }
        } else {
          return doReadElement(element, ctx);
        }
      } else {
        if (isNull()) {
          return null;
        } else {
          return doReadElement(element, ctx);
        }
      }
    }
  }
  
  // TODO (AF): Patch needed by API divergence.
  private Object getUnionValue(Union union) throws MALException {
	  switch (union.getTypeShortForm()) {
	  case Attribute._BOOLEAN_TYPE_SHORT_FORM:
		  return union.getBooleanValue();
	  case Attribute._FLOAT_TYPE_SHORT_FORM:
		  return union.getFloatValue();
	  case Attribute._DOUBLE_TYPE_SHORT_FORM:
		  return union.getDoubleValue();
	  case Attribute._OCTET_TYPE_SHORT_FORM:
		  return union.getOctetValue();
	  case Attribute._SHORT_TYPE_SHORT_FORM:
		  return union.getShortValue();
	  case Attribute._INTEGER_TYPE_SHORT_FORM:
		  return union.getIntegerValue();
	  case Attribute._LONG_TYPE_SHORT_FORM:
		  return union.getLongValue();
	  case Attribute._STRING_TYPE_SHORT_FORM:
		  return union.getStringValue();
	  default:
		  throw new MALException();
	  }
  }
  
  private Element checkPassedElement(Element element, MALEncodingContext ctx) throws MALException {
    if (element == null) {
      MALOperationStage opStage = resolveOperationStage(ctx);
      Object shortForm = resolveShortForm(opStage, ctx);
      MALElementFactory elementFactory = MALContextFactory.getElementFactoryRegistry().lookupElementFactory(shortForm);
      if (elementFactory == null) {
        String errorInfo = "Element factory not found: " + shortForm;
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, errorInfo);
        throw new MALException(errorInfo);
      }
      element = (Element) elementFactory.createElement();
    }
    return element;
  }
  
  private Object doReadElement(Element element, MALEncodingContext ctx) throws MALException {
    element = checkPassedElement(element, ctx);
    Element decodedElement = malDecoder.decodeElement(element);
    return decodedElement;
  }
  
  private MALOperationStage resolveOperationStage(MALEncodingContext ctx) {
    MALOperation op = ctx.getOperation();
    MALMessageHeader header = ctx.getHeader();
    return op.getOperationStage(header.getInteractionStage());
  }
  
  private Object resolveShortForm(MALOperationStage opStage, MALEncodingContext ctx) throws MALException { 
    Object[] shortForms = opStage.getElementShortForms();
    if (shortForms == null) {
      return null;
    } else {
      Object shortForm = shortForms[ctx.getBodyElementIndex()];
      if (shortForm == null) {
        if (ctx.getBodyElementIndex() == shortForms.length - 1) {
          // Last body element
          return resolveShortForm(opStage.getLastElementShortForms());
        } else {
          return resolveShortForm(null);
        }
      }
      return shortForm;
    }
  }
  
  private Object resolveShortForm(Object[] lastShortForms) throws MALException {
    if (lastShortForms != null
        && lastShortForms.length == SplitBinaryElementOutputStream.ATTRIBUTE_TYPES_COUNT
        && Blob.BLOB_SHORT_FORM.equals(lastShortForms[0])) {
      // Element declared as a MAL::Attribute
      try {
        int attributeTag = malDecoder.getDecoder().readByte();
        return attributeShortForms[attributeTag];
      } catch (Exception e) {
        throw new MALException("", e);
      }
    } else {
      try {
        long readShortForm = malDecoder.getDecoder().read64();
        return readShortForm;
      } catch (Exception e) {
        throw new MALException(e.getMessage(), e);
      }
    }
  }

  public void close() throws MALException {
    try {
      malDecoder.close();
    } catch (Exception e) {
      throw new MALException(e.getMessage(), e);
    }
  }
}
