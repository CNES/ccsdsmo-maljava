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
package fr.cnes.mal.provider;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALPublishDeregister extends CNESMALInteraction {

  public CNESMALPublishDeregister(MALMessageHeader messageHeader, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId) {
    super(messageHeader, messageSender, request, operation, authenticationId);
    setStage(MALPubSubOperation.PUBLISH_DEREGISTER_STAGE);
  }
  
  public void sendAcknowledgement() throws MALInteractionException, MALException {
    checkAckStage();
    sendResult(InteractionType.PUBSUB, 
        MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE, Boolean.FALSE);
    setStage(MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE);
  }
  
  public void sendError(MALStandardError error) throws MALInteractionException, MALException {
    checkAckStage();
    sendResult(InteractionType.PUBSUB, 
        MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE, 
        Boolean.TRUE, error.getErrorNumber(), error.getExtraInformation());
    setStage(MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE);
    setFailed(true);
  }
  
  private void checkAckStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
}
