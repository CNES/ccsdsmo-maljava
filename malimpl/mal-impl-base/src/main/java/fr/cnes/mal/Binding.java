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
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.dyade.aaa.util.management.MXWrapper;

public abstract class Binding implements MessageSender, MALMessageListener {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(Binding.class.getName());
  
  private BindingManager manager;
  
  private MALService service;
  
  private MALEndpoint endpoint;
  
  private MessageDispatcher messageDispatcher;
  
  private MALTransmitErrorListener transmitErrorListener;
  
  private String jmxName;
  
  private int sentMessageCount;
  
  private int receivedMessageCount;

  private int pendingMessageCount;
  
  private volatile boolean closed;
  
  public Binding(BindingManager manager, MALService service, MALEndpoint endpoint,
      MessageDispatcher messageDispatcher, String jmxName) {
    super();
    this.manager = manager;
    this.service = service;
    this.endpoint = endpoint;
    this.messageDispatcher = messageDispatcher;
    this.jmxName = jmxName;
    sentMessageCount = 0;
    receivedMessageCount = 0;
    pendingMessageCount = 0;
  }

  public MALService getService() {
    return service;
  }

  public BindingManager getManager() {
    return manager;
  }

  public int getSentMessageCount() {
    return sentMessageCount;
  }
  
  public int getPendingMessageCount() {
    return pendingMessageCount;
  }
  
  public int getReceivedMessageCount() {
    return receivedMessageCount;
  }
  
  public String getURIAsString() {
    return endpoint.getURI().toString();
  }
  
  public URI getURI() {
    return endpoint.getURI();
  }
  
  public void checkClosed() throws MALException {
    if (closed) throw new MALException("Closed");
  }
  
  public boolean isClosed() {
    return closed;
  }
  
  public void startMessageDelivery() throws MALException {
    if (messageDispatcher == null) {
      endpoint.startMessageDelivery();
    } else {
      throw CNESMALContext.createException("The shared endpoint should be explicitly activated");
    }
  }

  public MALMessage sendMessage(MALMessage msg) throws MALInteractionException,
      MALException {
    msg = manager.getMalContext().getAccessControl().check(msg);
    endpoint.sendMessage(msg);
    sentMessageCount++;
    return msg;
  }

  public void sendMessages(MALMessage[] messages)
      throws MALInteractionException, MALException {
    for (int i = 0; i < messages.length; i++) {
      messages[i] = manager.getMalContext().getAccessControl().check(messages[i]);
    }
    endpoint.sendMessages(messages);
    sentMessageCount += messages.length;
  }

  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      Object... body) throws MALException {
    return endpoint.createMessage(
        authenticationId, uRITo,
        timestamp, qoSlevel,
        priority, domain,
        networkZone, session, sessionName,
        transactionId,
        isErrorMessage,
        op, interactionStage,
        qosProperties, body);
  }

  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qosLevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      MALEncodedBody encodedBody) throws MALException {
    return endpoint.createMessage(
        authenticationId, uriTo,
        new Time(System.currentTimeMillis()), qosLevel,
        priority, domain,
        networkZone, sessionType, sessionName,
        transactionId,
        Boolean.FALSE,
        op, interactionStage,
        qosProperties, encodedBody);
  }
  
  protected abstract MessageDeliveryTask createMessageDeliveryTask(MALMessage msg);
  
  public synchronized void onMessage(MALEndpoint sourceEndPoint, MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Binding.onMessage(" + 
          msg.getHeader() + ',' + msg.getBody() + ')');
    MessageDeliveryTask task = createMessageDeliveryTask(msg);
    if (! closed) {
      try {
        manager.executeTask(task);
        receivedMessageCount++;
        pendingMessageCount++;
      } catch (MALException exc) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "Cannot deliver message: " + msg, exc);
      }
    } else {
      task.abort();
    }
  }
  
  synchronized void onHandledMessage(MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Binding.onHandledMessage(" +
          msg.getHeader() + ')');
    pendingMessageCount--;
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "nbMsgPending=" + pendingMessageCount);
    //notify();
  }
  
  public void onMessages(MALEndpoint sourceEndpoint, MALMessage[] messages) {
    for (int i = 0; i < messages.length; i++) {
      onMessage(sourceEndpoint, messages[i]);
    }
  }
  
  public void onInternalError(MALEndpoint sourceEndPoint, Throwable error) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Binding.onInternalError(" + error + ')');
    try {
      finalizeBinding();
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, e);
    }
  }
  
  protected abstract void finalizeBinding() throws MALException;
  
  public void onTransmitError(MALEndpoint callingEndpoint,
      MALMessageHeader header, MALStandardError standardError, Map qosProperties) {
    try {
      handleTransmitError(header, standardError);
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, e);
    }
    if (transmitErrorListener != null) {
      transmitErrorListener.onTransmitError(callingEndpoint, header, standardError, qosProperties);
    }
  }
  
  protected abstract void handleTransmitError(MALMessageHeader header,
      MALStandardError standardError) throws MALException;

  public void setTransmitErrorListener(MALTransmitErrorListener transmitErrorListener)
      throws MALException {
    this.transmitErrorListener = transmitErrorListener;
  }

  public MALTransmitErrorListener getTransmitErrorListener()
      throws MALException {
    return transmitErrorListener;
  }
  
  public synchronized void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Binding.close()");
    if (! closed) {
      // 1- stop the message delivery
      try {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "Stop the message delivery");
        if (messageDispatcher == null) {
          endpoint.stopMessageDelivery();
          //endpoint.setMessageListener(null);
        } else {
          removeFromDispatcher(messageDispatcher);
          if (! messageDispatcher.isBound()) {
            endpoint.stopMessageDelivery();
          }
        }
      } catch (MALException e) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "", e);
      }
      // 2- wait for all the pending messages to be handled
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Wait for all the pending messages to be handled");
      while (pendingMessageCount > 0) {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "nbMsgPending=" + pendingMessageCount);
        try {
          wait(500);
        } catch (InterruptedException e) {}
      }
      // 3- close the endpoint or dispatcher
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Close the endpoint or dispatcher");
      try {
        if (messageDispatcher == null) {
          endpoint.close();
        } else if (! messageDispatcher.isBound()) {
          manager.getMalContext().removeMessageDispatcher(endpoint.getURI());
          endpoint.close();
        }
      } catch (MALException e) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "", e);
      }
      // 4- close this binding
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Close the binding");
      manager.removeBinding(this);
      finalizeBinding();
      try {
        MXWrapper.unregisterMBean(jmxName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", e);
      }
      closed = true;
    }
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Binding[" + endpoint.getURI() + "] is closed.");
  }
  
  protected abstract void removeFromDispatcher(MessageDispatcher messageDispatcher) throws MALException;
  
}
