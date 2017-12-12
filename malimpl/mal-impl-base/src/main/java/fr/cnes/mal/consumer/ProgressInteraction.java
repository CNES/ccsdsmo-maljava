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
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.CNESMALErrorBody;
import fr.cnes.mal.SyncInteraction;

public class ProgressInteraction extends SyncInteraction {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(ProgressInteraction.class.getName());
  
  static void checkStageTransition(UOctet currentStage, UOctet nextStage)
      throws MALException {
    boolean isValid;
    switch (currentStage.getValue()) {
    case MALProgressOperation._PROGRESS_STAGE:
      isValid = (nextStage.getValue() == MALProgressOperation._PROGRESS_ACK_STAGE);
      break;
    case MALProgressOperation._PROGRESS_ACK_STAGE:
    case MALProgressOperation._PROGRESS_UPDATE_STAGE:
      isValid = (
          nextStage.getValue() == MALProgressOperation._PROGRESS_UPDATE_STAGE || 
          nextStage.getValue() == MALProgressOperation._PROGRESS_RESPONSE_STAGE);
      break;
    default:
      isValid = false;
    }
    if (!isValid) {
      throw CNESMALContext.createException("Invalid stage transition from " + currentStage + " to " + nextStage);
    }
  }
  
  private MALInteractionListener listener;
  
  private int updateSequenceCount;
  
  public ProgressInteraction(MALOperation operation, 
      MALMessageHeader initiationHeader,
      MALInteractionListener listener) {
    super(operation, initiationHeader);
    this.listener = listener;
    updateSequenceCount = 1;
    setStage(MALProgressOperation.PROGRESS_STAGE);
  }
  
  public boolean consumerIsActive() {
    return true;
  }
  
  protected void onMessage(MALOperation op, 
      MALMessageHeader header, MALMessageBody body, Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "ProgressInteraction.onMessage(" +
          op + ',' + header + ',' + body + ')');
    if (header.getInteractionType().getOrdinal() == InteractionType._PROGRESS_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      switch (nextStage.getValue()) {
      case MALProgressOperation._PROGRESS_ACK_STAGE:
        notifyInitiator(body);
        break;
      case MALProgressOperation._PROGRESS_UPDATE_STAGE:
        Integer msgSequenceCount = (Integer) qosProperties.get(CNESMALContext.SEQUENCE_COUNT);
        if (msgSequenceCount != null) {
          Integer nbMsgLost = msgSequenceCount.intValue() - updateSequenceCount;
          if (nbMsgLost > 0) {
            listener.progressUpdateErrorReceived(header, 
                new CNESMALErrorBody(new MALStandardError(MALHelper.DELIVERY_FAILED_ERROR_NUMBER, 
                    new Union(nbMsgLost))), qosProperties);
          }
          updateSequenceCount = msgSequenceCount.intValue() + 1;
        }
        listener.progressUpdateReceived(header, body, qosProperties);
        break;
      case MALProgressOperation._PROGRESS_RESPONSE_STAGE:
        setStatus(DONE);
        listener.progressResponseReceived(header, body, qosProperties);
        break;
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }
  
  protected void onError(MALOperation operation, 
      MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "ProgressInteraction.onError(" +
          operation + ',' + header + ',' + body + ')');
    if (header.getInteractionType().getOrdinal() == InteractionType._PROGRESS_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      switch (nextStage.getValue()) {
      case MALProgressOperation._PROGRESS_ACK_STAGE:
        setStatus(FAILED);
        notifyInitiator(body);
        break;
      case MALProgressOperation._PROGRESS_UPDATE_STAGE:
        setStatus(FAILED);
        listener.progressUpdateErrorReceived(header, body, qosProperties);
        break;
      case MALProgressOperation._PROGRESS_RESPONSE_STAGE:
        setStatus(FAILED);
        listener.progressResponseErrorReceived(header, body, qosProperties);
        break;
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }
}
