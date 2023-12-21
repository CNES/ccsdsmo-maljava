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
package fr.cnes.mal;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.accesscontrol.MALCheckErrorException;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public abstract class MessageDeliveryTask<B extends Binding> implements Task {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MessageDeliveryTask.class.getName());
  
  private MALMessage msg;
  
  private B binding;
  
  private Map qosProperties;
  
  public MessageDeliveryTask(MALMessage msg, B binding) {
    super();
    this.msg = msg;
    this.binding = binding;
    qosProperties = msg.getQoSProperties();
  }

  public MALMessage getMessage() {
    return msg;
  }

  public void run() throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDeliveryTask.run()\n" + "msg=" + msg);
    if (binding.isClosed()) {
      String errorMsg = new String("Closed");
      MOErrorException error = new MOErrorException(
          MALHelper.DELIVERY_FAILED_ERROR_NUMBER, new Union(errorMsg));
      onDeliveryError(error);
      return;
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "qosProperties=" + qosProperties);
    if (qosProperties != null) {
      Integer timeToLive = (Integer) qosProperties.get("timeToLive");
      if (timeToLive != null && timeToLive.intValue() > 0) {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - msg.getHeader().getTimestamp().getValue() - 
            timeToLive.intValue();
        if (duration > 0) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "Expired message: " + msg);
          String errorMsg = new String(
              "Message delivery timed out: " + msg + 
              ", expiration duration = " + duration);
          MOErrorException error = new MOErrorException(
              MALHelper.DELIVERY_TIMEDOUT_ERROR_NUMBER, new Union(errorMsg));
          onDeliveryError(error);
          return;
        }
      }
    }
    try {
      msg = binding.getManager().getMalContext().getAccessControl().check(msg);
    } catch (MALCheckErrorException exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Check Error: " + exc.getStandardError());
      onDeliveryError(exc.getStandardError());
      return;
    }
    
    deliverMessage();
  }
  
  public void abort() {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, 
          "MessageDeliveryTask.abort()");
    String errorMsg = new String("Message delivery abort");
    MOErrorException error = new MOErrorException(
        MALHelper.DELIVERY_FAILED_ERROR_NUMBER, new Union(errorMsg));
    try {
      onDeliveryError(error);
    } catch (MALException exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
    } catch (MALInteractionException exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
    }
  }
  
  protected abstract void onDeliveryError(MOErrorException error) throws MALInteractionException, MALException;
  
  protected abstract void deliverMessage() throws MALInteractionException, MALException;
  
  public void free() throws MALException {
    // Synchronized on Binding
    binding.onHandledMessage(msg);
    try {
      msg.free();
    } catch (MALException exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
    }
  }
  
  public void setExecutorIndex(int index) throws MALException {
    // Do nothing
  }
  
  public int getExecutorIndex() {
    return -1;
  }

}
