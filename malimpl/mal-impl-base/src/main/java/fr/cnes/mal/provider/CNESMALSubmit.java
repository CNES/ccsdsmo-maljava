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
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.provider.MALSubmit;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALSubmit extends CNESMALInteraction implements MALSubmit {

  CNESMALSubmit(MALMessageHeader messageHeader, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId) {
    super(messageHeader, messageSender, request, operation, authenticationId);
    setStage(MALSubmitOperation.SUBMIT_STAGE);
  }

  public MALMessage sendAcknowledgement() throws MALInteractionException, MALException {
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.SUBMIT, 
        MALSubmitOperation.SUBMIT_ACK_STAGE, Boolean.FALSE);
    return msg;
  }

  public MALMessage sendError(MALStandardError error) throws MALInteractionException, MALException {
    if (error == null) throw new IllegalArgumentException("Null error");
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.SUBMIT, 
        MALSubmitOperation.SUBMIT_ACK_STAGE, Boolean.TRUE, 
        error.getErrorNumber(), error.getExtraInformation());
    setFailed(true);
    return msg;
  }
  
  private void checkAckStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALSubmitOperation._SUBMIT_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
}
