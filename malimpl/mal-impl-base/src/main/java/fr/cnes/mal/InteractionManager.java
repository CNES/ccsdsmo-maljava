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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.consumer.AsyncInvokeInteraction;
import fr.cnes.mal.consumer.AsyncProgressInteraction;
import fr.cnes.mal.consumer.AsyncRequestInteraction;
import fr.cnes.mal.consumer.AsyncSubmitInteraction;

public class InteractionManager {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(InteractionManager.class.getName());
  
  private volatile long transIdCounter;
  
  private Hashtable<Long, Interaction> interactions;
  
  private volatile boolean closed;
  
  public InteractionManager() {
    transIdCounter = 0;
    interactions = new Hashtable<Long, Interaction>();
  }
  
  public synchronized Long getTransactionId() throws MALException {
    if (closed)
      throwClosedError();
    Long tid = new Long(transIdCounter++);
    return tid;
  }
  
  public synchronized Long putInteraction(Long tid, Interaction interact) throws MALException {
    interactions.put(tid, interact);
    return tid;
  }
  
  public synchronized void removeInteraction(Long tid) {
    Interaction interact = interactions.remove(tid);
    try {
      interact.close();
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      // continue
    }
  }
  
  public boolean signalResponse(MALOperation op, MALMessage response) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "InteractionManager.signalResponse(" +
          op + ',' + response + ')');
    Long tid = response.getHeader().getTransactionId();
    Interaction interact;
    synchronized (this) {
      if (closed) throwClosedError();
      interact = (Interaction) interactions.get(tid);
      if (interact == null) {
        // Assume that this interaction has been closed or 
        // has never existed (error).
        // If the interaction needs to be recovered then the 
        // consumer has to start the message 
        // delivery in an explicit way (using MALEndpoint)
        // after having called 'continueInteraction'
        
        // Ignore the message
        //throwClosedError();
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN,
              "Interaction not found: " + response.getHeader());
        return true;
      }
    }
    
    interact.onMessage(op, response);
    if (interact.isCompleted()) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Interaction " + tid + " is completed");
      interactions.remove(tid);
      return true;
    } else {
      return false;
    }
  }
  
  private void throwClosedError() throws MALException {
    throw CNESMALContext.createException("Closed");
  }
  
  public synchronized void close() {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "InteractionManager.close()");
    if (! closed) {
      closed = true;
      Enumeration enumer = interactions.elements();
      while (enumer.hasMoreElements()) {
        Interaction interact = (Interaction) enumer.nextElement();
        try {
          interact.close();
        } catch (MALException e) {
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "", e);
          // continue
        }
      }
      interactions.clear();
    }
  }
  
  private MALMessageHeader getInitialHeader(MALMessageHeader responseHeader) {
    MALMessageHeader initHeader = new CNESMALMessageHeader(
        responseHeader.getURITo(),
        responseHeader.getAuthenticationId(), 
        responseHeader.getURIFrom(),
        responseHeader.getTimestamp(), 
        responseHeader.getQoSlevel(), 
        responseHeader.getPriority(), 
        responseHeader.getDomain(), 
        responseHeader.getNetworkZone(), 
        responseHeader.getSession(), 
        responseHeader.getSessionName(), 
        responseHeader.getInteractionType(), 
        new UOctet((short) 1), 
        responseHeader.getTransactionId(), 
        responseHeader.getServiceArea(), 
        responseHeader.getService(), 
        responseHeader.getOperation(), 
        responseHeader.getAreaVersion(), 
        Boolean.FALSE);
    return initHeader;
  }
  
  public void continueInteraction(MALOperation op, 
      MALMessageHeader initHeader,
      UOctet lastInteractionStage,
      MALInteractionListener listener) throws MALException {
    Long tid = initHeader.getTransactionId();
    Interaction interact;
    synchronized (this) {
      if (closed) throwClosedError();
      interact = (Interaction) interactions.get(tid);
      if (interact != null) {
        throw new MALException("Already running interaction: " + tid);
      }
      switch (initHeader.getInteractionType().getOrdinal()) {
      case InteractionType._SUBMIT_INDEX:
        interact = new AsyncSubmitInteraction(op, initHeader, listener);
        interactions.put(tid, interact);
        break;
      case InteractionType._REQUEST_INDEX:
        interact = new AsyncRequestInteraction(op, initHeader, listener);
        interactions.put(tid, interact);
        break;
      case InteractionType._INVOKE_INDEX:
        interact = new AsyncInvokeInteraction(op, initHeader, listener);
        interactions.put(tid, interact);
        break;
      case InteractionType._PROGRESS_INDEX:
        interact = new AsyncProgressInteraction(op, initHeader, listener);
        interactions.put(tid, interact);
        break;
      default:
        throw CNESMALContext.createException("Unknown transaction " + tid);
      }
      interact.setStage(lastInteractionStage);
    }
  }
  
  public void checkInteractionActivity(long currentTime, int timeout) {
    Enumeration enumer = interactions.elements();
    while (enumer.hasMoreElements()) {
      Interaction interact = (Interaction) enumer.nextElement();
      if (interact != null &&
          (currentTime - interact.getTimestamp() > timeout)) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Interaction timeout:" + interact.getInitiationHeader());
        removeInteraction(interact.getTransactionId());
      }
    }
  }
  
  public String[] getInteractions() {
    String[] res = new String[interactions.size()];
    Collection<Interaction> values = interactions.values();
    Iterator<Interaction> iterator = values.iterator();
    int index = 0;
    while (iterator.hasNext()) {
      res[index++] = iterator.next().toString();
    }
    return res;
  }
  
  public int getInteractionCount() {
    return interactions.size();
  }
}