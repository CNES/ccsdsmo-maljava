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
package fr.cnes.malspp.transport;

import java.io.ByteArrayInputStream;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperationStage;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.objectweb.util.monolog.api.Logger;

public class MALSPPMessageBody implements MALMessageBody {

  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(MALSPPMessageBody.class.getName());

  private MALEncodingContext encodingContext;

  private MALElementStreamFactory elementStreamFactory;
  
  private int decodedIndex;

  private Object[] body;

  private ByteArrayInputStream encodedBody;
  
  private MALElementInputStream meis;
  
  public MALSPPMessageBody(Object[] body) {
    this.body = body;
    decodedIndex = body.length - 1;
  }

  public MALSPPMessageBody(MALEncodingContext encodingContext,
      MALElementStreamFactory elementStreamFactory, ByteArrayInputStream encodedBody) {
    this.encodingContext = encodingContext;
    this.elementStreamFactory = elementStreamFactory;
    this.encodedBody = encodedBody;
    UOctet stage = encodingContext.getHeader().getInteractionStage();
    MALOperationStage operationStage = encodingContext.getOperation().getOperationStage(stage);
    int bodySize;
    if (encodingContext.getHeader().getIsErrorMessage().booleanValue()) {
      bodySize = 2;
    } else if (operationStage == null) {
      bodySize = 0;
    } else {
      bodySize = operationStage.getElementShortForms().length;
    }
    body = new Object[bodySize];
    decodedIndex = -1;
  }
  
  private MALElementInputStream getElementInputStream() throws MALException {
    if (meis == null) {
      meis = elementStreamFactory.createInputStream(encodedBody);
    }
    return meis;
  }
  
  public synchronized Object getBodyElement(int index, Object element) throws MALException {
    if (decodedIndex < index) {
      MALElementInputStream meis = getElementInputStream();
      while (decodedIndex < index) {
        decodedIndex++;
        encodingContext.setBodyElementIndex(decodedIndex);
        Object bodyElement = meis.readElement((Element) element, encodingContext);
        body[decodedIndex] = bodyElement;
      }
    }
    return body[index]; 
  }
  
  public int getElementCount() {
    return body.length;
  }

  public MALEncodedElement getEncodedBodyElement(int index)
      throws MALException {
    throw new MALException("Not available");
  }

  public MALEncodedBody getEncodedBody() throws MALException {
    throw new MALException("Not yet implemented");
  }
}
