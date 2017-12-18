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
package fr.cnes.maljoram.malencoding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALOperationStage;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALEncodedElementList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.maljoram.encoding.DurationEncoder;
import fr.cnes.maljoram.encoding.Encoder;
import fr.cnes.maljoram.encoding.FineTimeEncoder;
import fr.cnes.maljoram.encoding.TimeEncoder;

public class JORAMElementOutputStream implements MALElementOutputStream {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(JORAMElementOutputStream.class.getName());

  public final static byte[] PAD = new byte[4];
  
  public final static int ATTRIBUTE_TYPES_COUNT = 18;

  private OutputStream os;

  protected JORAMEncoder encoder;
  
  private boolean encodedUpdate;
  
  private TimeEncoder timeEncoder;
  
  private FineTimeEncoder fineTimeEncoder;
  
  private DurationEncoder durationEncoder;

  JORAMElementOutputStream(OutputStream os, boolean encodedUpdate, 
      boolean byteArrayString, TimeEncoder timeEncoder,
      FineTimeEncoder fineTimeEncoder,
      DurationEncoder durationEncoder) {
    this.os = os;
    this.encodedUpdate = encodedUpdate;
    this.timeEncoder = timeEncoder;
    this.fineTimeEncoder = fineTimeEncoder;
    this.durationEncoder = durationEncoder;
    encoder = new JORAMEncoder(os, byteArrayString, 
        timeEncoder, fineTimeEncoder, durationEncoder);
  }
  
  public boolean isVarintSupported() {
    return encoder.isVarintSupported();
  }

  public void setVarintSupported(boolean varintSupported) {
    encoder.setVarintSupported(varintSupported);
  }

  private void encodeByte(byte b) throws MALException {
    try {
      encoder.getEncoder().writeByte(b);
    } catch (Exception e) {
      throw new MALException(e.getMessage(), e);
    }
  }

  public void writeElement(Object object, MALEncodingContext ctx)
      throws MALException {
    if (ctx == null) {
      encoder.encodeElement(castToElement(object));
      return;
    }
    MALMessageHeader header = ctx.getHeader();
    MALOperation op = ctx.getOperation();
    
    if (logger.isLoggable(BasicLevel.WARN))
      logger.log(BasicLevel.WARN, "op=" + op);
    if (logger.isLoggable(BasicLevel.WARN))
      logger.log(BasicLevel.WARN, "header=" + header);
    
    if (header.getIsErrorMessage()) {
      switch (ctx.getBodyElementIndex()) {
      case 0:
        // Error number (no polymorphism)
        encoder.encodeElement(castToElement(object));
        break;
      case 1:
        // Extra information (polymorphism)
        if (object == null) {
          encodeByte(Encoder.NULL);
        } else {
          encodeByte(Encoder.NOT_NULL);
          Element element = castToElement(object);
          try {
            encoder.getEncoder().write64(element.getShortForm());
          } catch (IOException e) {
            throw new MALException(e.getMessage(), e);
          }
          //encoder.encodeUOctet(element.getAreaVersion());
          encoder.encodeElement(element);
        }
        break;
      default:
        throw new MALException("Incorrect body element index: "
            + ctx.getBodyElementIndex());
      }
    } else {
      if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
        if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          if (encodedUpdate && ctx.getBodyElementIndex() > 0) {
            ElementList updateList = (ElementList) object;
            checkPolymorphism(updateList, op, header, ctx);
            encode(updateList);
          } else {
            doWriteElement(object, ctx);
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
          if (encodedUpdate && ctx.getBodyElementIndex() > 0) {
            if (object instanceof MALEncodedElementList) {
              MALEncodedElementList encodedElementList = (MALEncodedElementList) object;
              checkPolymorphism(encodedElementList, op, header, ctx);
              writeEncodedElementList((MALEncodedElementList) object);
            } else {
              // Local publish (private broker)
              doWriteElement(object, ctx);
            }
          } else {
            doWriteElement(object, ctx);
          }
        } else {
          doWriteElement(object, ctx);
        }
      } else {
        if (object == null) {
          encodeByte(Encoder.NULL);
        } else {
          encodeByte(Encoder.NOT_NULL);
          if (object instanceof MALEncodedElementList) {
            MALEncodedElementList encodedElementList = (MALEncodedElementList) object;
            checkPolymorphism(encodedElementList, op, header, ctx);
            writeNullableEncodedElementList(encodedElementList);
          } else {
            doWriteElement(object, ctx);
          }
        }
      }
    }
  }
  
  private Element castToElement(Object object) throws MALException {
    if (object instanceof Element) {
      return (Element) object;
    } else if (object instanceof Boolean) {
      return new Union((Boolean) object);
    } else if (object instanceof Byte) {
      return new Union((Byte) object);
    } else if (object instanceof Double) {
      return new Union((Double) object);
    } else if (object instanceof Float) {
      return new Union((Float) object);
    } else if (object instanceof Integer) {
      return new Union((Integer) object);
    } else if (object instanceof Long) {
      return new Union((Long) object);
    } else if (object instanceof Short) {
      return new Union((Short) object);
    } else if (object instanceof String) {
      return new Union((String) object);
    } else {
      throw new MALException("Cannot be cast to Element: " + object);
    }
  }
  
  private void doWriteElement(Object object, MALEncodingContext ctx) throws MALException {
    MALMessageHeader header = ctx.getHeader();
    MALOperation op = ctx.getOperation();
    Element element = castToElement(object);
    checkPolymorphism(element, op, header, ctx);
    encoder.encodeElement(element);
  }
  
  private void checkPolymorphism(MALEncodedElementList encodedElementList, 
      MALOperation op, MALMessageHeader header, MALEncodingContext ctx) throws MALException {
    MALOperationStage opStage = op.getOperationStage(header.getInteractionStage());
    Object[] shortForms = opStage.getElementShortForms();
    if (shortForms == null || shortForms[ctx.getBodyElementIndex()] == null) {
      try {
        encoder.getEncoder().write64((Long) encodedElementList.getShortForm());
      } catch (Exception e) {
        throw new MALException(e.getMessage(), e);
      }
    }
  }
  
  private void checkPolymorphism(Element element, MALOperation op, 
      MALMessageHeader header, MALEncodingContext ctx) throws MALException {
    MALOperationStage opStage = op.getOperationStage(header.getInteractionStage());
    Object[] shortForms = opStage.getElementShortForms();
    if (shortForms == null) {
      writeTypeId(element, opStage.getLastElementShortForms());
    } else if (shortForms[ctx.getBodyElementIndex()] == null) {
      if (ctx.getBodyElementIndex() == shortForms.length - 1) {
        // Last body element
        writeTypeId(element, opStage.getLastElementShortForms());
      } else {
        writeTypeId(element, null);
      }
    }
    // else nothing to write (no polymorphism)
  }
  
  private void writeTypeId(Element element, Object[] lastShortForms) throws MALException {
    if (lastShortForms != null && lastShortForms.length == ATTRIBUTE_TYPES_COUNT &&
        Blob.BLOB_SHORT_FORM.equals(lastShortForms[0])) {
      // Element declared as a MAL::Attribute
      Attribute attribute = (Attribute) element;
      try {
        encoder.getEncoder().writeByte(
            (byte) (attribute.getTypeShortForm().intValue() - 1));
      } catch (IOException e) {
        throw new MALException("", e);
      }
    } else {
      try {
        encoder.getEncoder().write64(element.getShortForm());
      } catch (IOException e) {
        throw new MALException("", e);
      }
    }
  }
  
  private void writeEncodedElementList(MALEncodedElementList encodedUpdateList) throws MALException {
    try {
      encoder.getEncoder().writeUnsignedInt(encodedUpdateList.size());
      for (MALEncodedElement encodedUpdate : encodedUpdateList) {
        if (encodedUpdate == null) {
          encodeByte(Encoder.NULL);
        } else {
          encodeByte(Encoder.NOT_NULL);
          os.write(encodedUpdate.getEncodedElement().getValue());
        }
      }
    } catch (IOException e) {
      throw new MALException("", e);
    }
  }
  
  private void writeNullableEncodedElementList(MALEncodedElementList encodedUpdateList) throws MALException {
    try {
      encoder.getEncoder().writeUnsignedInt(encodedUpdateList.size());
      for (MALEncodedElement encodedUpdate : encodedUpdateList) {
        if (encodedUpdate == null) {
          encoder.getEncoder().writeNull();
        } else {
          encoder.getEncoder().writeNotNull();
          os.write(encodedUpdate.getEncodedElement().getValue());
        }
      }
    } catch (IOException e) {
      throw new MALException("", e);
    }
  }

  /**
   * Encode the updates separately
   * 
   * @param updateList
   */
  protected void encode(ElementList updateList) throws MALException {
    try {
      OpenByteArrayOutputStream baos = new OpenByteArrayOutputStream();
      
      // Need to create another encoder for separate updates.
      // Should have exactly the same configuration as 'encoder'.
      JORAMEncoder updateEncoder = new JORAMEncoder(baos, encoder.isByteArrayString(), 
          timeEncoder, fineTimeEncoder, durationEncoder);
      updateEncoder.setVarintSupported(isVarintSupported());
      
      encoder.getEncoder().writeUnsignedInt(updateList.size());
      for (Object update : updateList) {
        if (update == null) {
          encoder.getEncoder().writeNull();
        } else {
          encoder.getEncoder().writeNotNull();
          
          updateEncoder.encodeElement(castToElement(update));
           
          int length = baos.size();
          encoder.getEncoder().writeUnsignedInt(length);
          
          byte[] encodedUpdate = baos.getInternalArray();
          os.write(encodedUpdate, 0, baos.size());
          baos.reset();
        }
      }
    } catch (IOException e) {
      throw new MALException("", e);
    }
  }

  public void flush() throws MALException {
    try {
      encoder.flush();
    } catch (Exception e) {
      throw new MALException(e.getMessage(), e);
    }
  }

  public void close() throws MALException {
    try {
      os.close();
    } catch (Exception e) {
      throw new MALException(e.getMessage(), e);
    }
  }
}
