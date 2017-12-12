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
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALProgress;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;

public class CNESMALProgress extends CNESMALInteraction implements MALProgress {
  
  private int updateSequenceCount;

  CNESMALProgress(MALMessageHeader messageHeader, 
      CNESMALProvider provider, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId) {
    super(messageHeader, provider, request, operation, authenticationId);
    setStage(MALProgressOperation.PROGRESS_STAGE);
    updateSequenceCount = 1;
  }

  public MALMessage sendUpdate(Object... body) throws MALInteractionException, MALException {
    checkUpdateStage();
    setQoSProperty(CNESMALContext.SEQUENCE_COUNT, new Integer(updateSequenceCount++));
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_UPDATE_STAGE,
        Boolean.FALSE, body);
    setStage(MALProgressOperation.PROGRESS_UPDATE_STAGE);
    return msg;
  }
  
  public MALMessage sendUpdate(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkUpdateStage();
    setQoSProperty(CNESMALContext.SEQUENCE_COUNT, new Integer(updateSequenceCount++));
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_UPDATE_STAGE,
        Boolean.FALSE, encodedBody);
    setStage(MALProgressOperation.PROGRESS_UPDATE_STAGE);
    return msg;
  }

  public MALMessage sendAcknowledgement(Object... body) throws MALInteractionException, MALException {
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_ACK_STAGE,
        Boolean.FALSE, body);
    setStage(MALProgressOperation.PROGRESS_ACK_STAGE);
    return msg;
  }
  
  public MALMessage sendAcknowledgement(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_ACK_STAGE,
        Boolean.FALSE, encodedBody);
    setStage(MALProgressOperation.PROGRESS_ACK_STAGE);
    return msg;
  }

  public MALMessage sendResponse(Object... body) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_RESPONSE_STAGE,
        Boolean.FALSE, body);
    setStage(MALProgressOperation.PROGRESS_RESPONSE_STAGE);
    return msg;
  }
  
  public MALMessage sendResponse(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_RESPONSE_STAGE,
        Boolean.FALSE, encodedBody);
    setStage(MALProgressOperation.PROGRESS_RESPONSE_STAGE);
    return msg;
  }

  public MALMessage sendError(MALStandardError error) throws MALInteractionException, MALException {
    checkFailed();
    UOctet nextStage;
    switch (getStage().getValue()) {
      case MALProgressOperation._PROGRESS_STAGE:
        nextStage = MALProgressOperation.PROGRESS_ACK_STAGE;
        break;
      case MALProgressOperation._PROGRESS_ACK_STAGE:
      case MALProgressOperation._PROGRESS_UPDATE_STAGE:
        nextStage = MALProgressOperation.PROGRESS_RESPONSE_STAGE;
        break;
      default:
        throw CNESMALContext.createException(
            MALHelper.INCORRECT_STATE_ERROR_NUMBER,
            "Stage error: current stage = " + getStage());
    }
    setStage(nextStage);
    MALMessage msg = sendResult(InteractionType.PROGRESS, nextStage, Boolean.TRUE,
        error.getErrorNumber(), error.getExtraInformation());
    setFailed(true);
    return msg;
  }
  
  public MALMessage sendUpdateError(MALStandardError error) throws MALInteractionException, MALException {
    checkUpdateErrorStage();
    MALMessage msg = sendResult(InteractionType.PROGRESS, 
        MALProgressOperation.PROGRESS_UPDATE_STAGE, Boolean.TRUE, 
        error.getErrorNumber(), error.getExtraInformation());
    setStage(MALProgressOperation.PROGRESS_UPDATE_STAGE);
    return msg;
  }
  
  private void checkAckStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALProgressOperation._PROGRESS_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
  
  private void checkUpdateStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALProgressOperation._PROGRESS_ACK_STAGE &&
        getStage().getValue() != MALProgressOperation._PROGRESS_UPDATE_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
  
  private void checkResponseStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALProgressOperation._PROGRESS_ACK_STAGE &&
        getStage().getValue() != MALProgressOperation._PROGRESS_UPDATE_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }

  public void checkUpdateErrorStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALProgressOperation._PROGRESS_ACK_STAGE &&
        getStage().getValue() != MALProgressOperation._PROGRESS_UPDATE_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
}
