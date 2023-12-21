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
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALPublishRegister extends CNESMALInteraction {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(CNESMALPublishRegister.class.getName());

  public CNESMALPublishRegister(MALMessageHeader messageHeader, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId,
      NamedValueList providerSupplements) {
    super(messageHeader, messageSender, request, operation, authenticationId, providerSupplements);
    setStage(MALPubSubOperation.PUBLISH_REGISTER_STAGE);
  }
  
  public void sendAcknowledgement() throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALPublishRegister.sendAcknowledgement()");
    checkAckStage();
    sendResult(InteractionType.PUBSUB, 
        MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE, Boolean.FALSE, providerSupplements);
    setStage(MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE);
  }

  public void sendError(MOErrorException error) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALPublishRegister.sendException(" + error + ')');
    if (error == null) throw new IllegalArgumentException("Null error");
    checkAckStage();
    sendResult(InteractionType.PUBSUB, 
        MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE, Boolean.TRUE,
        providerSupplements, error.getErrorNumber(), error.getExtraInformation());
    setStage(MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE);
    setFailed(true);
  }
  
  private void checkAckStage() throws MALInteractionException, MALException {
    checkFailed();
    if (getStage().getValue() != MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
      throw CNESMALContext.createException(
          MALHelper.INCORRECT_STATE_ERROR_NUMBER,
          "Stage error: current stage = " + getStage());
    }
  }

}
