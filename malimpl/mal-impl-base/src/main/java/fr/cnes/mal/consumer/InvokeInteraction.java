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
package fr.cnes.mal.consumer;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.SyncInteraction;

public class InvokeInteraction extends SyncInteraction {
  
  static void checkStageTransition(UOctet currentStage, UOctet nextStage) 
    throws MALException {
    boolean isValid;
    switch (currentStage.getValue()) {
      case MALInvokeOperation._INVOKE_STAGE:
        isValid = (nextStage.getValue() == MALInvokeOperation._INVOKE_ACK_STAGE);
        break;
      case MALInvokeOperation._INVOKE_ACK_STAGE:
        isValid = (nextStage.getValue() == MALInvokeOperation._INVOKE_RESPONSE_STAGE);
        break;
     default:
       isValid = false; 
    }
    if (! isValid) {
      throw CNESMALContext.createException("Invalid stage transition from " + currentStage + " to " + nextStage);
    }
  }
  
  private MALInteractionListener listener;
  
  public InvokeInteraction(MALOperation operation, 
      MALMessageHeader initiationHeader,
      MALInteractionListener listener) {
    super(operation, initiationHeader);
    this.listener = listener;
    setStage(MALInvokeOperation.INVOKE_STAGE);
  }
  
  public boolean consumerIsActive() {
    return true;
  }
  
  protected void onMessage(MALOperation op, 
      MALMessageHeader header, MALMessageBody body, Map qosProperties) throws MALException {    
    if (header.getInteractionType().getOrdinal() == InteractionType._INVOKE_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      switch (nextStage.getValue()) {
      case MALInvokeOperation._INVOKE_ACK_STAGE:
        notifyInitiator(body);
        break;
      case MALInvokeOperation._INVOKE_RESPONSE_STAGE:
        setStatus(DONE);
        listener.invokeResponseReceived(header, body, qosProperties);
        break;
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }

  protected void onError(MALOperation operation,
      MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
    if (header.getInteractionType().getOrdinal() == InteractionType._INVOKE_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      switch (nextStage.getValue()) {
      case MALInvokeOperation._INVOKE_ACK_STAGE:
        setStatus(FAILED);
        notifyInitiator(body);
        break;
      case MALInvokeOperation._INVOKE_RESPONSE_STAGE:
        setStatus(FAILED);
        listener.invokeResponseErrorReceived(header, body, qosProperties);
        break;
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }
}
