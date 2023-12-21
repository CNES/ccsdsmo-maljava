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
import org.ccsds.moims.mo.mal.MOErrorException;
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
import fr.cnes.mal.Interaction;

public class AsyncProgressInteraction extends Interaction {

  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(AsyncProgressInteraction.class.getName());
  
  private MALInteractionListener listener;
  
  public AsyncProgressInteraction(
      MALOperation operation, 
      MALMessageHeader initiationHeader,
      MALInteractionListener listener) {
    super(operation, initiationHeader);
    this.listener = listener;
    setStage(MALProgressOperation.PROGRESS_STAGE);
  }
  
  public boolean consumerIsActive() {
    return (listener != null);
  }
  
  public void setListener(MALInteractionListener listener) {
    this.listener = listener;
  }
  
  protected void onMessage(MALOperation op, 
      MALMessageHeader header, MALMessageBody body, Map qosProperties) throws MALException {
    if (header.getInteractionType().getOrdinal() == InteractionType._PROGRESS_INDEX) {
      try {
        ProgressInteraction.checkStageTransition(getStage(), header.getInteractionStage());
      } catch (MALException exc) {
        this.error = new MOErrorException(
            MALHelper.INCORRECT_STATE_ERROR_NUMBER,
            new Union(exc.getMessage()));
        throw exc;
      }
      switch (header.getInteractionStage().getValue()) {
      case MALProgressOperation._PROGRESS_ACK_STAGE:
        setStage(MALProgressOperation.PROGRESS_ACK_STAGE);
        listener.progressAckReceived(header, body, qosProperties);
        break;
      case MALProgressOperation._PROGRESS_UPDATE_STAGE:    
        setStage(MALProgressOperation.PROGRESS_UPDATE_STAGE);
        listener.progressUpdateReceived(header, body, qosProperties);
        break;
      case MALProgressOperation._PROGRESS_RESPONSE_STAGE:    
        setStage(MALProgressOperation.PROGRESS_RESPONSE_STAGE);
        setStatus(DONE);
        listener.progressResponseReceived(header, body, qosProperties);
        break;
      default:
        throw CNESMALContext.createException("Unexpected interaction stage: " + header.getInteractionStage());
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }

  protected void onError(MALOperation operation, 
      MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException {

    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
             "AsyncProgressInteraction.onError: " + header.getInteractionType() + "." + header.getInteractionStage());

    if (header.getInteractionType().getOrdinal() == InteractionType._PROGRESS_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      try {
        ProgressInteraction.checkStageTransition(getStage(), nextStage);
      } catch (MALException exc) {
        if (error == null) {
          this.error = new MOErrorException(
              MALHelper.INCORRECT_STATE_ERROR_NUMBER,
              new Union(exc.getMessage()));
          throw exc;
        }
        // the message has already been processed, this is an internal call
        nextStage = new UOctet((short) (getStage().getValue() + 1));
      }
      switch (nextStage.getValue()) {
      case MALProgressOperation._PROGRESS_ACK_STAGE:
        setStage(MALProgressOperation.PROGRESS_ACK_STAGE);
        setStatus(FAILED);
        if (listener != null) {
          listener.progressAckErrorReceived(header, body, qosProperties);
        }
        break;
      case MALProgressOperation._PROGRESS_UPDATE_STAGE:    
        setStage(MALProgressOperation.PROGRESS_UPDATE_STAGE);
        setStatus(FAILED);
        if (listener != null) {
          listener.progressUpdateErrorReceived(header, body, qosProperties);
        }
        break;
      case MALProgressOperation._PROGRESS_RESPONSE_STAGE:    
        setStage(MALProgressOperation.PROGRESS_RESPONSE_STAGE);
        setStatus(FAILED);
        if (listener != null) {
          listener.progressResponseErrorReceived(header, body, qosProperties);
        }
        break;
      default:
        throw CNESMALContext.createException("Unexpected interaction stage: " + header.getInteractionStage());
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }
}
