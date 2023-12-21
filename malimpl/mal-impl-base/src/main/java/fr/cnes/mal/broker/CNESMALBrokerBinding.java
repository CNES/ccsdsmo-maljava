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
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
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
  private Vector<Identifier> runningTransactions;
  
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
    runningTransactions = new Vector<Identifier>();
  }
  
  protected void finalizeBinding() throws MALException {
    broker.removeBinding(getDestinationId());
  }
  
  public Blob getAuthenticationId() {
    return authenticationId;
  }

  private void sendPublishError(
      Identifier uriTo,
      MALPubSubOperation op, 
      NamedValueList supplements,
      Map publishQosProps,
      // TODO SL removed parameter
      // assume it is only used locally with a null value
      // EntityKeyList unknownEntityKeyList,
      Long tid) throws MALInteractionException, MALException {
    MALMessage msg = createMessage(
        authenticationId, uriTo,
        new Time(System.currentTimeMillis()), 
        tid,
        Boolean.TRUE,
        op, 
        MALPubSubOperation.PUBLISH_STAGE,
        supplements, publishQosProps,
        MALHelper.UNKNOWN_ERROR_NUMBER,
        null);
    sendMessage(msg);
  }

  class BrokerBindingTask extends MessageDeliveryTask<CNESMALBrokerBinding> {
    
    public BrokerBindingTask(MALMessage msg) {
      super(msg, CNESMALBrokerBinding.this);
    }
    
    private void replyError(MOErrorException stdError) throws MALInteractionException, MALException {
      MALMessage msg = getMessage();
      // TODO SL move back service to area version
      MALArea area = MALContextFactory.lookupArea(msg.getHeader().getServiceArea(), msg.getHeader().getServiceVersion());
      MALService service = area.getServiceByNumber(
          msg.getHeader().getService());
      MALOperation operation = service.getOperationByNumber(msg.getHeader().getOperation());
      MALMessageHeader header = msg.getHeader();

      switch (header.getInteractionType().getOrdinal()) {
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALBrokerBinding.this, msg, operation, authenticationId, header.getSupplements());
          register.sendError(stdError);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALBrokerBinding.this, msg, operation, authenticationId, header.getSupplements());
          deregister.sendError(stdError);
        }  else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          sendPublishError(header.getFrom(), 
              (MALPubSubOperation) operation, 
              header.getSupplements(), msg.getQoSProperties(), 
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
    protected void onDeliveryError(MOErrorException error) throws MALInteractionException, MALException {
      replyError(error);
    }
    
    public void finalizeTask() {
      Identifier from = getMessage().getHeader().getFrom();
      if (from != null) {
        runningTransactions.remove(from);
      }
    }

    @Override
    protected void deliverMessage() throws MALInteractionException, MALException {
      MALMessage msg = getMessage();
      MALArea messageArea = MALContextFactory.lookupArea(msg.getHeader()
          .getServiceArea(), msg.getHeader().getServiceVersion());
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
              authenticationId, header.getSupplements());
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
              authenticationId, header.getSupplements());
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
                authenticationId, header.getSupplements());
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
              authenticationId, header.getSupplements());
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
              authenticationId, header.getSupplements());
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
      Identifier from = getMessage().getHeader().getFrom();
      int index = runningTransactions.indexOf(from);
      return (index == -1);
    }

    public void init() {
      Identifier from = getMessage().getHeader().getFrom();
      if (from != null) {
        runningTransactions.add(from);
      }
    }
  }

  // implementation of the MALBrokerBinding interface
  // probably deprecated
  public MALMessage sendNotify(
      MALOperation op, URI subscriber,
      Long transactionId, IdentifierList domainId,
      Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority,
      Identifier subscriptionId,
      UpdateHeader updateHeader,
      Object... updateObjects)
          throws IllegalArgumentException, MALInteractionException, MALException {
    return sendNotify(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        subscriber, transactionId, domainId,
        notifyQosProps, subscriptionId, updateHeader, updateObjects);
  }

  public MALMessage sendNotify(UShort area, UShort service, UShort operation,
                               UOctet version, URI subscriber,
                               Long transactionId, IdentifierList domainId,
                               Map notifyQosProps, Identifier subscriptionId,
                               NamedValueList supplements,
                               UpdateHeader updateHeader,
                               Object... updateObjects) throws IllegalArgumentException,
                                                        MALInteractionException,
                                                        MALException {
    return sendNotify(area, service, operation, version,
        new Identifier(subscriber.getValue()), transactionId, domainId,
        notifyQosProps, subscriptionId, supplements, updateHeader, updateObjects);
  }

  // implementation of the MALBrokerBinding interface
  public MALMessage sendNotify(
      UShort area, UShort service, UShort operation, UOctet version,
      Identifier subscriber, Long transactionId, IdentifierList domainId,
      Map notifyQosProps, Identifier subscriptionId,
      NamedValueList supplements,
      UpdateHeader updateHeader,
      Object... updateObjects)
          throws IllegalArgumentException, MALInteractionException, MALException {
    return sendNotify(area, service, operation, version, subscriber, transactionId,
        supplements, notifyQosProps, subscriptionId, updateHeader, updateObjects);
  }

  public MALMessage sendNotify(MALOperation op, URI subscriber,
                               Long transactionId, IdentifierList domainId,
                               Identifier networkZone, SessionType sessionType,
                               Identifier sessionName, QoSLevel notifyQos,
                               Map notifyQosProps, UInteger notifyPriority,
                               Identifier subscriptionId,
                               NamedValueList supplements,
                               UpdateHeader updateHeader,
                               Object... updateObjects) throws IllegalArgumentException,
                                                        MALInteractionException,
                                                        MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    return sendNotify(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        new Identifier(subscriber.getValue()), transactionId, supplements, notifyQosProps,
        subscriptionId, updateHeader, updateObjects);
  }

  // TODO SL reorganize implementation
  public MALMessage sendNotify(
      MALOperation op,
      Identifier subscriber, 
      Long transactionId,
      NamedValueList supplements, Map notifyQosProps, 
      Identifier subscriptionId,
      UpdateHeader updateHeader,
      Object... updateObjects)
          throws IllegalArgumentException, MALInteractionException, MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    return sendNotify(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        subscriber, transactionId, supplements, notifyQosProps,
        subscriptionId, updateHeader, updateObjects);
  }
  
  public MALMessage sendNotify(
      UShort area, UShort service, UShort operation, UOctet version,
      Identifier subscriber, Long transactionId,
      NamedValueList supplements, Map notifyQosProps,
      Identifier subscriptionId,
      UpdateHeader updateHeader,
      Object... updateObjects)
          throws IllegalArgumentException, MALInteractionException, MALException {
    if (subscriber == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (subscriptionId == null) throw new IllegalArgumentException("Null subscription id");
    if (updateHeader == null) throw new IllegalArgumentException("Null UpdateHeader");
    Object[] bodyElements = new Object[updateObjects.length + 2];
    System.arraycopy(updateObjects, 0, bodyElements, 2, updateObjects.length);
    bodyElements[0] = subscriptionId;
    bodyElements[1] = updateHeader;
    MALMessage notifyMsg = createMessage(
        authenticationId,
        subscriber,
        new Time(System.currentTimeMillis()), 
        transactionId, 
        Boolean.FALSE,
        area, service, operation, version,
        InteractionType.PUBSUB, MALPubSubOperation.NOTIFY_STAGE, 
        supplements,
        notifyQosProps,
        bodyElements);
    messageBuffer.addElement(notifyMsg);
    return notifyMsg;
  }

  // implementation of the MALBrokerBinding interface
  public MALMessage sendNotifyError(
      UShort area, UShort service,
      UShort operation, UOctet version,
      Identifier subscriber, Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority,
      MOErrorException error)
          throws IllegalArgumentException, MALInteractionException, MALException {
    // TODO SL no supplements field?
    return sendNotifyError(area, service, operation, version, subscriber, transactionId,
        new NamedValueList(), notifyQosProps, error);
  }

  public MALMessage sendNotifyError(MALOperation op, URI subscriber,
                                    Long transactionId, IdentifierList domainId,
                                    Identifier networkZone,
                                    SessionType sessionType,
                                    Identifier sessionName, QoSLevel notifyQos,
                                    Map notifyQosProps, UInteger notifyPriority,
                                    MOErrorException error,
                                    NamedValueList supplements) throws IllegalArgumentException,
                                                                MALInteractionException,
                                                                MALException {
    return sendNotifyError(op, new Identifier(subscriber.getValue()), transactionId,
        supplements, notifyQosProps, error);
  }

  // TODO SL reorganize implementation
  public MALMessage sendNotifyError(
      MALOperation op,
      Identifier subscriber,
      Long transactionId,
      NamedValueList supplements, Map notifyQosProps,
      MOErrorException error)
          throws IllegalArgumentException, MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerBinding.sendNotifyError(" + op + ',' + ')');
    if (op == null) throw new IllegalArgumentException("Null operation");
    return sendNotifyError(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        subscriber, transactionId, supplements, notifyQosProps,
        error);
  }

  public MALMessage sendNotifyError(UShort area, UShort service,
                                    UShort operation, UOctet version,
                                    URI subscriber, Long transactionId,
                                    IdentifierList domainId,
                                    Identifier networkZone,
                                    SessionType sessionType,
                                    Identifier sessionName, QoSLevel notifyQos,
                                    Map notifyQosProps, UInteger notifyPriority,
                                    MOErrorException error,
                                    NamedValueList supplements) throws IllegalArgumentException,
                                                                MALInteractionException,
                                                                MALException {
    return sendNotifyError(area, service, operation, version,
        new Identifier(subscriber.getValue()), transactionId,
        supplements, notifyQosProps, error);
  }

  public MALMessage sendNotifyError(
      UShort area, UShort service,
      UShort operation, UOctet version,
      Identifier subscriber, Long transactionId,
      NamedValueList supplements, Map notifyQosProps,
      MOErrorException error)
          throws IllegalArgumentException, MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerBinding.sendNotifyError(" +
          area + ':' + service + ':' + operation + ',' + ')');
    if (subscriber == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (error == null) throw new IllegalArgumentException("Null error");
    MALMessage notifyMsg = createMessage(
        authenticationId,
        subscriber,
        new Time(System.currentTimeMillis()), 
        transactionId,
        Boolean.TRUE,
        area, service, operation, version,
        InteractionType.PUBSUB, MALPubSubOperation.NOTIFY_STAGE,
        supplements, notifyQosProps,
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

  // implementation of the MALBrokerBinding interface
  public MALMessage sendPublishError(
      MALOperation op, URI publisher,
      Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName, QoSLevel qos,
      Map qosProps, UInteger priority,
      MOErrorException error,
      NamedValueList supplements)
          throws IllegalArgumentException, MALInteractionException, MALException {
    return sendPublishError(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        publisher, transactionId, domainId, networkZone, sessionType, sessionName, qos,
        qosProps, priority, error, supplements);
  }

  public MALMessage sendPublishError(UShort area, UShort service,
                                     UShort operation, UOctet version,
                                     URI publisher, Long transactionId,
                                     IdentifierList domainId,
                                     Identifier networkZone,
                                     SessionType sessionType,
                                     Identifier sessionName, QoSLevel qos,
                                     Map qosProps, UInteger priority,
                                     MOErrorException error,
                                     NamedValueList supplements) throws IllegalArgumentException,
                                                                 MALInteractionException,
                                                                 MALException {
    return sendPublishError(area, service, operation, version,
        new Identifier(publisher.getValue()), transactionId,
        domainId, networkZone, sessionType, sessionName, qos,
        qosProps, priority, error, supplements);
  }

  // implementation of the MALBrokerBinding interface
  public MALMessage sendPublishError(
      UShort area, UShort service,
      UShort operation, UOctet version,
      Identifier publisher, Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName, QoSLevel qos,
      Map qosProps, UInteger priority,
      MOErrorException error,
      NamedValueList supplements)
          throws IllegalArgumentException, MALInteractionException, MALException {
    return sendPublishError(area, service, operation, version, publisher, transactionId,
        supplements, qosProps, error);
  }

  // TODO SL reorganize implementation
  public MALMessage sendPublishError(
      MALOperation op, Identifier publisher,
      Long transactionId, 
      NamedValueList supplements, Map qosProps,
      MOErrorException error) throws MALInteractionException, MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    return sendPublishError(
        op.getServiceKey().getAreaNumber(), op.getServiceKey().getServiceNumber(),
        op.getNumber(), op.getServiceKey().getAreaVersion(),
        publisher, transactionId, supplements, qosProps,
        error);
  }
  
  public MALMessage sendPublishError(
      UShort area, UShort service,
      UShort operation, UOctet version,
      Identifier publisher, Long transactionId,
      NamedValueList supplements, Map qosProps,
      MOErrorException error)
          throws IllegalArgumentException, MALInteractionException, MALException {
    if (publisher == null) throw new IllegalArgumentException("Null subscriber URI");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (error == null) throw new IllegalArgumentException("Null error");
    MALMessage publishErrorMsg = createMessage(
        authenticationId,
        publisher,
        new Time(System.currentTimeMillis()), 
        transactionId,
        Boolean.TRUE,
        area, service, operation, version,
        InteractionType.PUBSUB, MALPubSubOperation.PUBLISH_STAGE, 
        supplements, qosProps,
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
      MOErrorException standardError) throws MALException {
    // Nothing to do
  }

  @Override
  protected void removeFromDispatcher(MessageDispatcher messageDispatcher)
      throws MALException {
    messageDispatcher.setBroker(null);
  }

  public Blob setAuthenticationId(Blob newAuthenticationId) {
    Blob previous = authenticationId;
    authenticationId = newAuthenticationId;
    return previous;
  }

  // The functions in MALBrokerBinding should use Identifier instead of URI
  
  public MALMessage sendNotifyError(MALOperation op, URI subscriber,
                                    Long transactionId, IdentifierList domainId,
                                    Identifier networkZone,
                                    SessionType sessionType,
                                    Identifier sessionName, QoSLevel notifyQos,
                                    Map notifyQosProps, UInteger notifyPriority,
                                    MOErrorException error) throws IllegalArgumentException,
                                                            MALInteractionException,
                                                            MALException {
    return sendNotifyError(op, new URI(subscriber.getValue()),
                 transactionId, domainId,
                 networkZone,
                 sessionType,
                 sessionName, notifyQos,
                 notifyQosProps, notifyPriority,
                 error);
  }

  public MALMessage sendNotify(UShort area, UShort service, UShort operation,
                               UOctet version, URI subscriber,
                               Long transactionId, IdentifierList domainId,
                               Map notifyQosProps, Identifier subscriptionId,
                               UpdateHeader updateHeader,
                               Object... updateObjects) throws IllegalArgumentException,
                                                   MALInteractionException,
                                                   MALException {
    return sendNotify(area, service, operation,
                      version, new URI(subscriber.getValue()),
                      transactionId, domainId,
                      notifyQosProps, subscriptionId,
                      updateHeader,
                      updateObjects);
  }

  public MALMessage sendNotifyError(UShort area, UShort service,
                                    UShort operation, UOctet version,
                                    URI subscriber, Long transactionId,
                                    IdentifierList domainId,
                                    Identifier networkZone,
                                    SessionType sessionType,
                                    Identifier sessionName, QoSLevel notifyQos,
                                    Map notifyQosProps, UInteger notifyPriority,
                                    MOErrorException error) throws IllegalArgumentException,
                                                            MALInteractionException,
                                                            MALException {
    return sendNotifyError(area, service,
                           operation, version,
                           new URI(subscriber.getValue()), transactionId,
                           domainId,
                           networkZone,
                           sessionType,
                           sessionName, notifyQos,
                           notifyQosProps, notifyPriority,
                           error);
  }

}
