/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 - 2018 CNES
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
package fr.cnes.mal;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;


public abstract class Interaction {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(Interaction.class.getName());
  
  public static final int RUN = 0;
  
  public static final int DONE = 1;
  
  public static final int FAILED = 2;
  
  public static final String[] statusNames = {
      "RUN", "DONE", "FAILED"
  };
  
  private int status; 
  
  private UOctet stage;
  
  private MALOperation operation;
  
  private MALMessageHeader initiationHeader;
  
  private MALMessage msg;
  
  protected MOErrorException error;
  
  private long timestamp;
  
  public Interaction(MALOperation operation, MALMessageHeader initiationHeader) {
    this.operation = operation;
    this.initiationHeader = initiationHeader;
    status = RUN;
    timestamp = System.currentTimeMillis();
  }
  
  public Long getTransactionId() {
    return initiationHeader.getTransactionId();
  }

  public long getTimestamp() {
    return timestamp;
  }

  public MALMessageHeader getInitiationHeader() {
    return initiationHeader;
  }

  protected MALMessage getMessage() {
    return msg;
  }
  
  final int getStatus() {
    return status;
  }
  
  protected void setStatus(int status) {
    this.status = status;
  }
  
  protected void setStage(UOctet stage) {
    this.stage = stage;
  }
  
  protected final UOctet getStage() {
    return stage;
  }
  
  protected abstract void onError(MALOperation operation,
      MALMessageHeader header, MALErrorBody body, Map qosProperties) throws MALException;
  
  protected abstract void onMessage(MALOperation operation,
      MALMessageHeader header, MALMessageBody body, Map qosProperties) throws MALException;

  public synchronized void onMessage(MALOperation op, MALMessage msg) {
    this.msg = msg;
    timestamp = System.currentTimeMillis();
    
    MALMessageHeader header = msg.getHeader();
    MALMessageBody body = msg.getBody();

    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
    		     "&&&&& onMessage: T" + msg.getHeader().getTransactionId() + ", " + msg.getHeader().getInteractionType() + "." + msg.getHeader().getInteractionStage());

    try {
      if (status == RUN) {
        if (msg.getHeader().getIsErrorMessage().booleanValue()) {
          MALErrorBody errorBody = (MALErrorBody) body;
          onError(op, header, errorBody, msg.getQoSProperties());
          // Changes the status after the 'onError' call
          // because it may fail (e.g. incorrect message)
          if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "&&&&& onMessage: T" + msg.getHeader().getTransactionId() + " isError");
          
          status = FAILED;
        } else {
          if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, "&&&&& onMessage ok");
          onMessage(op, header, body, msg.getQoSProperties());
        }
      } else {
          if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "&&&&& onMessage: T" + msg.getHeader().getTransactionId() + " interaction already completed");

          throw CNESMALContext.createException("Interaction already completed");
      }
    } catch (MALException exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      try {
        close(exc.getMessage());
      } catch (MALException e) {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "Exception in error handling code ", e);
      }
    }
    // TODO SL check if this does not corrupt the implementation
    this.msg = null;
  }
  
  public boolean isCompleted() {
    return (status == FAILED || status == DONE);
  }
  
  public abstract boolean consumerIsActive();
  
  public void close() throws MALException {
    close("Closed interaction");
  }

  public void close(String errMsg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Interaction.close " + errMsg);
    if (isCompleted())
      return;
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Interaction.close, stage=" + getStage().getValue()
                 + ", close stage=" + (getStage().getValue() + 1));
    MALMessageHeader errorHeader = null;
    // the MAL testbed requires to send the header of the original message in the upcall
    // this is a change of the Java API introduced during the prototyping of MAL v2
    // I do not believe that this is a good idea, as this function may be called in case of an internal error
    // without any original message.
    // also keep in mind that this header may have the isErrorMessage set to false
    if (this.msg != null) {
      errorHeader = msg.getHeader();
    } else {
      errorHeader = new CNESMALMessageHeader(
          initiationHeader.getTo(),
          initiationHeader.getAuthenticationId(), 
          initiationHeader.getFrom(), 
          initiationHeader.getTimestamp(), 
          initiationHeader.getInteractionType(), 
          new UOctet((short) (getStage().getValue() + 1)), 
          initiationHeader.getTransactionId(), 
          initiationHeader.getServiceArea(), 
          initiationHeader.getService(), 
          initiationHeader.getOperation(), 
          initiationHeader.getServiceVersion(), 
          Boolean.TRUE,
          initiationHeader.getSupplements());
    }
    if (error != null) {
      error = new MOErrorException(
          MALHelper.INTERNAL_ERROR_NUMBER,
          new Union(errMsg));
    }
    onError(operation, errorHeader, new CNESMALErrorBody(error), null);
  }

  @Override
  public String toString() {
    return "Interaction [status=" + status + ", stage=" + stage
        + ", operation=" + operation + ", initiationHeader=" + initiationHeader
        + ", timestamp=" + timestamp + "]";
  }
}
