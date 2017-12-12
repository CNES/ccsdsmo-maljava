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
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.Interaction;

public class AsyncSubmitInteraction extends Interaction {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(AsyncSubmitInteraction.class.getName());
  
  private MALInteractionListener listener;
  
  public AsyncSubmitInteraction(
      MALOperation operation, 
      MALMessageHeader initiationHeader,
      MALInteractionListener listener) {
    super(operation, initiationHeader);
    this.listener = listener;
    setStage(MALSubmitOperation.SUBMIT_STAGE);
  }
  
  public void setListener(MALInteractionListener listener) {
    this.listener = listener;
  }

  protected void onMessage(MALOperation op, 
      MALMessageHeader header, MALMessageBody body, Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "AsyncSubmitInteraction.onMessage(" +
          op + ',' + header + ',' + body + ',' + qosProperties + ')');
    if (header.getInteractionType().getOrdinal() == 
      InteractionType._SUBMIT_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      SubmitInteraction.checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      setStatus(DONE);
      listener.submitAckReceived(header, qosProperties);
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: " + header.getInteractionType());
    }
  }
  
  public boolean consumerIsActive() {
    return (listener != null);
  }

  protected void onError(MALOperation operation, MALMessageHeader header,
      MALErrorBody body, Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "AsyncSubmitInteraction.onError(" +
          operation + ',' + header + ',' + body + ',' + qosProperties + ')');
    if (header.getInteractionType().getOrdinal() == InteractionType._SUBMIT_INDEX) {
      UOctet nextStage = header.getInteractionStage();
      SubmitInteraction.checkStageTransition(getStage(), nextStage);
      setStage(nextStage);
      setStatus(FAILED);
      if (listener != null) {
        listener.submitErrorReceived(header, body, qosProperties);
      } else {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Null listener: " + header);
      }
    } else {
      throw CNESMALContext.createException("Unexpected interaction type: "
          + header.getInteractionType());
    }
  }
}
