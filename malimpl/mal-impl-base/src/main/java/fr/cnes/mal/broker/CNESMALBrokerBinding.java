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
package fr.cnes.mal.broker;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALDeregisterBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.ccsds.moims.mo.mal.transport.MALPublishRegisterBody;
import org.ccsds.moims.mo.mal.transport.MALRegisterBody;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.Binding;
import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageDeliveryTask;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.MessageSender;
import fr.cnes.mal.provider.CNESMALDeregister;
import fr.cnes.mal.provider.CNESMALPublishDeregister;
import fr.cnes.mal.provider.CNESMALPublishRegister;
import fr.cnes.mal.provider.CNESMALRegister;

public class CNESMALBrokerBinding extends Binding implements MALBrokerBinding, MessageSender, CNESMALBrokerBindingMBean {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
  .getLogger(CNESMALBrokerBinding.class.getName());
  
  private Blob authenticationId;
  
  private Map defaultQoSProperties;
  
  private CNESMALBroker broker;

  private Vector<MALMessage> messageBuffer;
  
  /**
   * Ensures mono-threaded running for a given
   * publisher/subscriber.
   */
  private Vector<URI> runningTransactions;
  
  public CNESMALBrokerBinding(
      CNESMALBrokerManager brokerManager,
      CNESMALBroker broker,
      MALEndpoint endpoint,
      Blob authenticationId,
      Map defaultQoSProperties,
      String jmxName,
      MessageDispatcher messageDispatcher) throws MALException {
    super(brokerManager, null, endpoint, messageDispatcher, jmxName);
    this.broker = broker;
    this.authenticationId = authenticationId;
    this.defaultQoSProperties = defaultQoSProperties;
    messageBuffer = new Vector<MALMessage>();
    runningTransactions = new Vector<URI>();
  }
  
  protected void finalizeBinding() throws MALException {
    broker.removeBinding(getURI());
  }
  
  public Blob getAuthenticationId() {
    return authenticationId;
  }

  private void sendPublishError(
      URI uriTo,
      MALPubSubOperation op, 
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority,
      EntityKeyList unknownEntityKeyList,
      Long tid) throws MALInteractionException, MALException {
    MALMessage msg = createMessage(
        authenticationId, uriTo,
        new Time(System.currentTimeMillis()), 
        publishQos,
        publishPriority, 
        domain,
        networkZone, 
        sessionType, 
        sessionName,
        tid,
        Boolean.TRUE,
        op, 
        MALPubSubOperation.PUBLISH_STAGE,
        publishQosProps,
        MALHelper.UNKNOWN_ERROR_NUMBER,
        unknownEntityKeyList);
    sendMessage(msg);
  }

  class BrokerBindingTask extends MessageDeliveryTask<CNESMALBrokerBinding> {
    
    public BrokerBindingTask(MALMessage msg) {
      super(msg, CNESMALBrokerBinding.this);
    }
    
    private void replyError(MALStandardError stdError) throws MALInteractionException, MALException {
      MALMessage msg = getMessage();
      MALArea area = MALContextFactory.lookupArea(msg.getHeader().getServiceArea(), msg.getHeader().getAreaVersion());
      MALService service = area.getServiceByNumber(
          msg.getHeader().getService());
      MALOperation operation = service.getOperationByNumber(msg.getHeader().getOperation());
      MALMessageHeader header = msg.getHeader();

      switch (header.getInteractionType().getOrdinal()) {
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALBrokerBinding.this, msg, operation, authenticationId);
          register.sendError(stdError);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALBrokerBinding.this, msg, operation, authenticationId);
          deregister.sendError(stdError);
        }  else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          sendPublishError(header.getURIFrom(), 
              (MALPubSubOperation) operation, 
              header.getDomain(), 
              header.getNetworkZone(),
              header.getSession(), 
              header.getSessionName(), 
              header.getQoSlevel(), 
              msg.getQoSProperties(), 
              header.getPriority(),
              null,
              header.getTransactionId());
        } else {
          throw CNESMALContext.createException("Unexpected PUBSUB stage: " + header.getInteractionStage());
        }
        break;
      default:
        throw CNESMALContext.createException("Unexpected interaction: " + header.getInteractionType());
      }
    }
    
    @Override
    protected void onDeliveryError(MALStandardError error) throws MALInteractionException, MALException {
      replyError(error);
    }
    
    public void finalizeTask() {
      URI from = getMessage().getHeader().getURIFrom();
      if (from != null) {
        runningTransactions.remove(from);
      }
    }

    @Override
    protected void deliverMessage() throws MALInteractionException, MALException {
      MALMessage msg = getMessage();
      MALArea messageArea = MALContextFactory.lookupArea(msg.getHeader()
          .getServiceArea(), msg.getHeader().getAreaVersion());
      MALService messageService = messageArea.getServiceByNumber(msg
          .getHeader().getService());
      MALOperation messageOperation = messageService.getOperationByNumber(msg
          .getHeader().getOperation());
      if (! (messageOperation instanceof MALPubSubOperation)) {
        throw CNESMALContext.createException("Unexpected operation: "
            + messageOperation);
      }
      MALMessageHeader header = msg.getHeader();
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "header=" + header);
      switch (msg.getHeader().getInteractionType().getOrdinal()) {
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALBrokerBinding.this, msg, messageOperation,
              authenticationId);
          MALRegisterBody body = (MALRegisterBody) msg.getBody();
          try {
            broker.getHandler().handleRegister(register, body);
            register.sendAcknowledgement();
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, "", e);
            register.sendError(e.getStandardError());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALBrokerBinding.this, msg, messageOperation,
              authenticationId);
          MALDeregisterBody body = (MALDeregisterBody) msg.getBody();
          try {
            broker.getHandler().handleDeregister(deregister, body);
            deregister.sendAcknowledgement();
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, "", e);
            deregister.sendError(e.getStandardError());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          if (header.getIsErrorMessage().booleanValue()) {
            throw CNESMALContext.createException("Unexpected Publish error: "
                + header);
          } else {
            CNESMALPublish interaction = new CNESMALPublish(header,
                CNESMALBrokerBinding.this, msg, messageOperation,
                authenticationId);
            MALPublishBody body = (MALPublishBody) msg.getBody();
            try {
              broker.getHandler().handlePublish(interaction, body);
              broker.commitMessageSending();
            } catch (MALInteractionException e) {
              if (logger.isLoggable(BasicLevel.WARN))
                logger.log(BasicLevel.WARN, "", e);
            }
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
          CNESMALPublishRegister register = new CNESMALPublishRegister(header,
              CNESMALBrokerBinding.this, msg, messageOperation,
              authenticationId);
          MALPublishRegisterBody body = (MALPublishRegisterBody) msg.getBody();
          try {
            broker.getHandler().handlePublishRegister(register, body);
            register.sendAcknowledgement();
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, "", e);
            register.sendError(e.getStandardError());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
          CNESMALPublishDeregister deregister = new CNESMALPublishDeregister(
              header, CNESMALBrokerBinding.this, msg, messageOperation,
              authenticationId);
          try {
            broker.getHandler().handlePublishDeregister(deregister);
            deregister.sendAcknowledgement();
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "", e);
            // The interaction pattern PUBLISH DEREGISTER cannot fail
          }
        } else {
          throw CNESMALContext.createException("Unexpected PUBSUB stage: "
              + header.getInteractionStage());
        }
      }
    }

    public boolean runnable() {
      URI from = getMessage().getHeader().getURIFrom();
      int index = runningTransactions.indexOf(from);
      return (index == -1);
    }

    public void init() {
      URI from = getMessage().getHeader().getURIFrom();
      if (from != null) {
        runningTransactions.add(from);
      }
    }
  }

  public MALMessage sendNotify(
      MALOperation op,
      URI subscriber, 
      Long transactionId, 
      IdentifierList domainId,
      Identifier networkZone, 
      SessionType sessionType, Identifier sessionName,
      QoSLevel notifyQos, Map notifyQosProps, 
      UInteger notifyPriority,
      Identifier subscriptionId,
      UpdateHeaderList updateHeaderList,
      List... updateLists)
      throws MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (subscriber == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (domainId == null) throw new IllegalArgumentException("Null domain id");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (notifyQos == null) throw new IllegalArgumentException("Null QoS");
    if (notifyPriority == null) throw new IllegalArgumentException("Null priority");
    if (subscriptionId == null) throw new IllegalArgumentException("Null subscription id");
    if (updateHeaderList == null) throw new IllegalArgumentException("Null UpdateHeaderList");
    Object[] bodyElements = new Object[updateLists.length + 2];
    System.arraycopy(updateLists, 0, bodyElements, 2, updateLists.length);
    bodyElements[0] = subscriptionId;
    bodyElements[1] = updateHeaderList;
    MALMessage notifyMsg = createMessage(
        authenticationId,
        subscriber,
        new Time(System.currentTimeMillis()), 
        notifyQos,
        notifyPriority, 
        domainId,
        networkZone, 
        sessionType, 
        sessionName,        
        transactionId, 
        Boolean.FALSE,
        op,
        MALPubSubOperation.NOTIFY_STAGE, 
        notifyQosProps,
        bodyElements);
    messageBuffer.addElement(notifyMsg);
    return notifyMsg;
  }

  public MALMessage sendNotifyError(
      MALOperation op,
      URI subscriber,
      Long transactionId, IdentifierList domainId,
      Identifier networkZone, SessionType sessionType, Identifier sessionName,
      QoSLevel notifyQos, Map notifyQosProps, UInteger notifyPriority,
      MALStandardError error)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerBinding.sendNotifyError(" + op + ',' +
          domainId + ',' + networkZone + ',' + notifyPriority + ')');
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (subscriber == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (domainId == null) throw new IllegalArgumentException("Null domain id");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (notifyQos == null) throw new IllegalArgumentException("Null QoS");
    if (notifyPriority == null) throw new IllegalArgumentException("Null priority");
    if (error == null) throw new IllegalArgumentException("Null error");
    MALMessage notifyMsg = createMessage(
        authenticationId,
        subscriber,
        new Time(System.currentTimeMillis()), 
        notifyQos,
        notifyPriority, 
        domainId,
        networkZone, 
        sessionType, 
        sessionName, 
        transactionId,
        Boolean.TRUE,
        op,
        MALPubSubOperation.NOTIFY_STAGE,
        notifyQosProps,
        error.getErrorNumber(),
        error.getExtraInformation());
    messageBuffer.addElement(notifyMsg);
    return notifyMsg;
  }
  
  public void commitMessageSending() throws MALInteractionException, MALException {
    synchronized (messageBuffer) {
      if (messageBuffer.size() > 0) {
        MALMessage[] messages = new MALMessage[messageBuffer.size()];
        messageBuffer.copyInto(messages);
        sendMessages(messages);
        messageBuffer.clear();
      }
    }
  }

  public MALMessage sendPublishError(
      MALOperation op, URI publisher,
      Long transactionId, IdentifierList domainId,
      Identifier networkZone, SessionType sessionType, Identifier sessionName,
      QoSLevel qos, Map qosProps, UInteger priority,
      MALStandardError error) throws MALInteractionException, MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (publisher == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (domainId == null) throw new IllegalArgumentException("Null domain id");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (qos == null) throw new IllegalArgumentException("Null QoS");
    if (priority == null) throw new IllegalArgumentException("Null priority");
    if (error == null) throw new IllegalArgumentException("Null error");
    MALMessage publishErrorMsg = createMessage(
        authenticationId,
        publisher,
        new Time(System.currentTimeMillis()), 
        qos,
        priority, 
        domainId,
        networkZone,
        sessionType, 
        sessionName,
        transactionId,
        Boolean.TRUE,
        op,
        MALPubSubOperation.PUBLISH_STAGE, 
        qosProps,
        error.getErrorNumber(),
        error.getExtraInformation());
    sendMessage(publishErrorMsg);
    return publishErrorMsg;
  }

  @Override
  protected MessageDeliveryTask createMessageDeliveryTask(MALMessage msg) {
    return new BrokerBindingTask(msg);
  }

  @Override
  protected void handleTransmitError(MALMessageHeader header,
      MALStandardError standardError) throws MALException {
    // Nothing to do
  }

  @Override
  protected void removeFromDispatcher(MessageDispatcher messageDispatcher)
      throws MALException {
    messageDispatcher.setBroker(null);
  }
  
}
