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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;


public abstract class SyncInteraction extends Interaction {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(SyncInteraction.class.getName());

  private Object lock;
  
  private MALMessageBody result;
  
  private boolean resultReceived;
  
  public SyncInteraction(MALOperation operation, MALMessageHeader header) {
    super(operation, header);
    this.lock = new Object();
    resultReceived = false;
  }
  
  public void notifyInitiator(MALMessageBody body) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SyncInteraction.notifyInitiator(" +
          body + ')');
    synchronized (lock) {
      result = body;
      resultReceived = true;
      lock.notify();
    }
  }
  
  public final Object getLock() {
    return lock;
  }
  
  public MALMessageBody waitForResponse() throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SyncInteraction.waitForResponse(" +
          result + ')');
    synchronized (lock) {
      while (!resultReceived) {
        try {
          lock.wait();
        } catch (InterruptedException ex) {
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "", ex);
          return null;
        }
      }
    }

    if (result instanceof MALErrorBody) {
      MALErrorBody errorBody = (MALErrorBody) result;
      MALStandardError error;
      try {
        error = errorBody.getError();
      } catch (MALException exc) {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "", exc);
        throw exc;
      }
      throw new MALInteractionException(error);
    } else {
      return result;
    }
  }

}
