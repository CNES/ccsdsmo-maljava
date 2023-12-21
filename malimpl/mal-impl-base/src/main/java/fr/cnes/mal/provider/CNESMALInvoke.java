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
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.provider.MALInvoke;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALInvoke extends CNESMALInteraction implements MALInvoke {

  CNESMALInvoke(MALMessageHeader messageHeader, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId,
      NamedValueList providerSupplements) {
    super(messageHeader, messageSender, request, operation, authenticationId, providerSupplements);
    setStage(MALInvokeOperation.INVOKE_STAGE);
  }

  public MALMessage sendAcknowledgement(Object... body) throws MALInteractionException, MALException {
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.INVOKE, 
        MALInvokeOperation.INVOKE_ACK_STAGE, Boolean.FALSE, providerSupplements, body);
    setStage(MALInvokeOperation.INVOKE_ACK_STAGE);
    return msg;
  }
  
  public MALMessage sendAcknowledgement(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkAckStage();
    MALMessage msg = sendResult(InteractionType.INVOKE, 
        MALInvokeOperation.INVOKE_ACK_STAGE, Boolean.FALSE, providerSupplements, encodedBody);
    setStage(MALInvokeOperation.INVOKE_ACK_STAGE);
    return msg;
  }

  public MALMessage sendResponse(Object... body) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.INVOKE, 
        MALInvokeOperation.INVOKE_RESPONSE_STAGE, Boolean.FALSE, providerSupplements, body);
    setStage(MALInvokeOperation.INVOKE_RESPONSE_STAGE);
    return msg;
  }
  
  public MALMessage sendResponse(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.INVOKE, 
        MALInvokeOperation.INVOKE_RESPONSE_STAGE, Boolean.FALSE, providerSupplements, encodedBody);
    setStage(MALInvokeOperation.INVOKE_RESPONSE_STAGE);
    return msg;
  }

  public MALMessage sendError(MOErrorException error) throws MALInteractionException, MALException {
    if (error == null) throw new IllegalArgumentException("Null error");
    checkFailed();
    UOctet nextStage;
    switch (getStage().getValue()) {
      case MALInvokeOperation._INVOKE_STAGE:
        nextStage = MALInvokeOperation.INVOKE_ACK_STAGE;
        break;
      case MALInvokeOperation._INVOKE_ACK_STAGE:
        nextStage = MALInvokeOperation.INVOKE_RESPONSE_STAGE;
        break;
      default:
        throw CNESMALContext.createException(
            MALHelper.INCORRECT_STATE_ERROR_NUMBER,
            "Stage error: current stage = " + getStage());
    }
    setStage(nextStage);
    MALMessage msg = sendResult(InteractionType.INVOKE, nextStage, Boolean.TRUE, 
        providerSupplements, error.getErrorNumber(), error.getExtraInformation());
    setFailed(true);
    return msg;
  }
  
  private void checkAckStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALInvokeOperation._INVOKE_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
  
  private void checkResponseStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALInvokeOperation._INVOKE_ACK_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }

}
