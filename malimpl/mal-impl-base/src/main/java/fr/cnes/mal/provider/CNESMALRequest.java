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
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.provider.MALRequest;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALRequest extends CNESMALInteraction implements MALRequest {
  
  CNESMALRequest(MALMessageHeader messageHeader, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId,
      NamedValueList providerSupplements) {
    super(messageHeader, messageSender, request, operation, authenticationId, providerSupplements);
    setStage(MALRequestOperation.REQUEST_STAGE);
  }

  public MALMessage sendResponse(Object... body) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.REQUEST, 
        MALRequestOperation.REQUEST_RESPONSE_STAGE, Boolean.FALSE, providerSupplements, body);
    return msg;
  }
  
  public MALMessage sendResponse(MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.REQUEST, 
        MALRequestOperation.REQUEST_RESPONSE_STAGE, Boolean.FALSE, providerSupplements, encodedBody);
    return msg;
  }

  public MALMessage sendError(MOErrorException error) throws MALInteractionException, MALException {
    if (error == null) throw new IllegalArgumentException("Null error");
    checkResponseStage();
    MALMessage msg = sendResult(InteractionType.REQUEST, 
        MALRequestOperation.REQUEST_RESPONSE_STAGE, 
        Boolean.TRUE, providerSupplements, error.getErrorNumber(), error.getExtraInformation());
    setFailed(true);
    return msg;
  }
  
  private void checkResponseStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALRequestOperation._REQUEST_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }
  
}
