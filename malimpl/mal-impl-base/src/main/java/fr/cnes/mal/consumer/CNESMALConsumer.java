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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSendOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALInteractionListener;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.CNESMALMessageHeader;
import fr.cnes.mal.InteractionManager;
import fr.cnes.mal.MessageDeliveryTask;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.Binding;
import fr.cnes.mal.SubscriptionManager;

public class CNESMALConsumer extends Binding implements MALConsumer, CNESMALConsumerMBean {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALConsumer.class.getName());
  
  public static final String NULL_OP_ERROR = "Null operation";
  
  public static final UOctet SEND_STAGE = new UOctet((short) 0);
  
  private InteractionManager interactionManager;
  
  private Identifier uriTo;
  
  private Identifier brokerUri;
  
  private Blob authenticationId;
  
  private IdentifierList domain;
  
  private Identifier networkZone;
  
  private SessionType sessionType;
  
  private Identifier sessionName;
  
  private UInteger priority;
  
  private QoSLevel qosLevel;
  
  private NamedValueList supplements;
  
  private Map qosProperties;
  
  private MessageDispatcher messageDispatcher;
  
  private SubscriptionManager subscriptionManager;
  
  /**
   * Ensures mono-threaded running for a given
   * transaction.
   */
  private Vector<Long> runningTransactions;
  
  private HashMap<Long, Integer> executorIndexes;
  
  CNESMALConsumer(
      Identifier uriTo,
      Identifier uriBroker,
      MALService service, 
      Blob authenticationId, 
      IdentifierList domain, 
      Identifier networkZone, 
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel qosLevel,
      NamedValueList supplements,
      Map qosProperties,
      UInteger priority,
      CNESMALConsumerManager consumerManager, 
      MALEndpoint endpoint,
      MessageDispatcher messageDispatcher,
      String jmxName) throws MALException {
    super(consumerManager, service, endpoint, messageDispatcher, jmxName);
    this.uriTo = uriTo;
    this.brokerUri = uriBroker;
    this.authenticationId = authenticationId;
    this.domain = domain;
    this.networkZone = networkZone;
    this.sessionType = sessionType;
    this.sessionName = sessionName;
    this.qosLevel = qosLevel;
    this.supplements = supplements;
    this.qosProperties = qosProperties;
    this.priority = priority;
    this.interactionManager = new InteractionManager();
    this.messageDispatcher = messageDispatcher;
    if (messageDispatcher == null) {
      subscriptionManager = new SubscriptionManager();
    } else {
      subscriptionManager = messageDispatcher.getSubscriptionManager();
    }
    runningTransactions = new Vector<Long>();
    executorIndexes = new HashMap<Long, Integer>();
  }
  
  private MALMessage createMessage(
      MALOperation op,
      InteractionType interactionType,
      UOctet interactionStage,
      Identifier destUri,
      Long transactionId,
      NamedValueList supplements,
      Object... body) throws MALException {
    return createMessage(
        authenticationId, destUri,
        new Time(System.currentTimeMillis()),
        transactionId,
        Boolean.FALSE,
        op, interactionStage,
        supplements, qosProperties, body);
  }
  
  private MALMessage createMessage(
      MALOperation op,
      InteractionType interactionType,
      UOctet interactionStage,
      Identifier destUri,
      Long transactionId,
      NamedValueList supplements,
      MALEncodedBody encodedBody) throws MALException {
    return createMessage(
        authenticationId, destUri,
        new Time(System.currentTimeMillis()),
        transactionId,
        Boolean.FALSE,
        op, interactionStage,
        supplements, qosProperties, encodedBody);
  }

  public MALMessage send(MALSendOperation op, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SEND, SEND_STAGE, uriTo, tid, supplements, body);
    return sendMessage(msg);
  }
  
  public MALMessage send(MALSendOperation op, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SEND, SEND_STAGE, uriTo, tid, supplements, encodedBody);
    return sendMessage(msg);
  }

  public void submit(MALSubmitOperation op, Object... body)
      throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SUBMIT, MALSubmitOperation.SUBMIT_STAGE, uriTo, tid, supplements, body);
    SubmitInteraction interact = new SubmitInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    interact.waitForResponse();
  }
  
  public void submit(MALSubmitOperation op, MALEncodedBody encodedBody)
      throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SUBMIT, MALSubmitOperation.SUBMIT_STAGE, uriTo, tid, supplements, encodedBody);
    SubmitInteraction interact = new SubmitInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    interact.waitForResponse();
  }

  public MALMessageBody request(MALRequestOperation op, Object... body)
      throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.REQUEST, MALRequestOperation.REQUEST_STAGE, uriTo, tid, supplements, body);
    RequestInteraction interact = new RequestInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }
  
  public MALMessageBody request(MALRequestOperation op, MALEncodedBody encodedBody)
      throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.REQUEST, MALRequestOperation.REQUEST_STAGE, uriTo, tid, supplements, encodedBody);
    RequestInteraction interact = new RequestInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }

  public MALMessageBody invoke(MALInvokeOperation op,  
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.INVOKE, MALInvokeOperation.INVOKE_STAGE, uriTo, tid, supplements, body);
    InvokeInteraction interact = new InvokeInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }
  
  public MALMessageBody invoke(MALInvokeOperation op,  
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.INVOKE, MALInvokeOperation.INVOKE_STAGE, uriTo, tid, supplements, encodedBody);
    InvokeInteraction interact = new InvokeInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }

  public MALMessageBody progress(MALProgressOperation op, 
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PROGRESS, MALProgressOperation.PROGRESS_STAGE, uriTo, tid, supplements, body);
    ProgressInteraction interact = new ProgressInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }
  
  public MALMessageBody progress(MALProgressOperation op, 
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PROGRESS, MALProgressOperation.PROGRESS_STAGE, uriTo, tid, supplements, encodedBody);
    ProgressInteraction interact = new ProgressInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return interact.waitForResponse();
  }

  public void register(MALPubSubOperation op, Subscription subscriptionRequest, 
      MALInteractionListener listener) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALConsumer.register(" + 
          op + ',' + subscriptionRequest + ')');
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PUBSUB, MALPubSubOperation.REGISTER_STAGE, 
        brokerUri, tid, supplements, subscriptionRequest);
    subscriptionManager.registerListener(subscriptionRequest.getSubscriptionId(), listener);
    RegisterInteraction interact = new RegisterInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    interact.waitForResponse();
  }

  public void deregister(MALPubSubOperation op, 
      IdentifierList unsubscriptionList) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    
    int length = unsubscriptionList.size();
    for (int i = 0; i < length; i++) {
      Identifier id = (Identifier) unsubscriptionList.get(i);
      Long tid = subscriptionManager.deregisterListener(id);
      if (tid != null) {
        executorIndexes.remove(tid);
      }
    }
    
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PUBSUB, MALPubSubOperation.DEREGISTER_STAGE,
        brokerUri, tid, supplements, unsubscriptionList);
    DeregisterInteraction interact = new DeregisterInteraction(op, msg.getHeader());
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    interact.waitForResponse();
  }

  public MALMessage asyncSubmit(MALSubmitOperation op, 
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SUBMIT, MALSubmitOperation.SUBMIT_STAGE, uriTo, tid, supplements, body);
    AsyncSubmitInteraction interact = new AsyncSubmitInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }
  
  public MALMessage asyncSubmit(MALSubmitOperation op, 
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.SUBMIT, MALSubmitOperation.SUBMIT_STAGE, uriTo, tid, supplements, encodedBody);
    AsyncSubmitInteraction interact = new AsyncSubmitInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  public MALMessage asyncRequest(MALRequestOperation op,
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.REQUEST, MALRequestOperation.REQUEST_STAGE, uriTo, tid, supplements, body);
    AsyncRequestInteraction interact = new AsyncRequestInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }
  
  public MALMessage asyncRequest(MALRequestOperation op,
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.REQUEST, MALRequestOperation.REQUEST_STAGE, uriTo, tid, supplements, encodedBody);
    AsyncRequestInteraction interact = new AsyncRequestInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  public MALMessage asyncInvoke(MALInvokeOperation op,
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.INVOKE, MALInvokeOperation.INVOKE_STAGE, uriTo, tid, supplements, body);
    AsyncInvokeInteraction interact = new AsyncInvokeInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }
  
  public MALMessage asyncInvoke(MALInvokeOperation op,
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.INVOKE, MALInvokeOperation.INVOKE_STAGE, uriTo, tid, supplements, encodedBody);
    AsyncInvokeInteraction interact = new AsyncInvokeInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  public MALMessage asyncProgress(MALProgressOperation op,
      MALInteractionListener listener, Object... body) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PROGRESS, MALProgressOperation.PROGRESS_STAGE, uriTo, tid, supplements, body);
    AsyncProgressInteraction interact = new AsyncProgressInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }
  
  public MALMessage asyncProgress(MALProgressOperation op,
      MALInteractionListener listener, MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PROGRESS, MALProgressOperation.PROGRESS_STAGE, uriTo, tid, supplements, encodedBody);
    AsyncProgressInteraction interact = new AsyncProgressInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  public MALMessage asyncRegister(MALPubSubOperation op, 
      Subscription subscriptionRequest, 
      MALInteractionListener listener) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PUBSUB, MALPubSubOperation.REGISTER_STAGE, brokerUri, 
        tid, supplements, subscriptionRequest);
    subscriptionManager.registerListener(subscriptionRequest.getSubscriptionId(), listener);
    AsyncRegisterInteraction interact = 
      new AsyncRegisterInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  public MALMessage asyncDeregister(MALPubSubOperation op, 
      IdentifierList unsubscriptionList, 
      MALInteractionListener listener) throws MALInteractionException, MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    
    int length = unsubscriptionList.size();
    for (int i = 0; i < length; i++) {
      Identifier id = (Identifier) unsubscriptionList.get(i);
      Long tid = subscriptionManager.deregisterListener(id);
      if (tid != null) {
        executorIndexes.remove(tid);
      }
    }
    
    Long tid = interactionManager.getTransactionId();
    MALMessage msg = createMessage(op, InteractionType.PUBSUB, MALPubSubOperation.DEREGISTER_STAGE, brokerUri, 
        tid, supplements, unsubscriptionList); 
    AsyncDeregisterInteraction interact = 
      new AsyncDeregisterInteraction(op, msg.getHeader(), listener);
    interactionManager.putInteraction(tid, interact);
    sendMessage(msg);
    return msg;
  }

  @Override
  protected void finalizeBinding() throws MALException {
    interactionManager.close();
  }

  class ConsumerTask extends MessageDeliveryTask<CNESMALConsumer> {
    
    private int executorIndex;
    
    ConsumerTask(MALMessage msg, int executorIndex) {
      super(msg, CNESMALConsumer.this);
      this.executorIndex = executorIndex;
    }
    
    @Override
    protected void deliverMessage() throws MALException {
      MALMessage msg = getMessage();
      MALService service = getService();
      MALArea messageArea;
      MALService messageService;
      if (msg.getHeader().getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX &&
          msg.getHeader().getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
        // The message area and service may be different than the consumer area and service
        if (service.getAreaNumber().getValue() != msg.getHeader().getServiceArea().getValue()) {
          messageArea = MALContextFactory.lookupArea(msg.getHeader().getServiceArea(), msg.getHeader().getServiceVersion());
          if (messageArea == null) {
            throw CNESMALContext.createException("Unexpected area: " + msg.getHeader().getServiceArea());
          }
          messageService = messageArea.getServiceByNumber(
              msg.getHeader().getService());
          if (messageService == null) {
            throw CNESMALContext.createException("Unexpected service: " + msg.getHeader().getService());
          }
        } else if (service.getServiceNumber().getValue() != msg.getHeader().getService().getValue()) {
          // cannot get the area from the service in new API
          // messageArea = service.getArea();
          messageArea = getServiceArea();
          messageService = messageArea.getServiceByNumber(
              msg.getHeader().getService());
          if (messageService == null) {
            throw CNESMALContext.createException("Unexpected service: " + msg.getHeader().getService());
          }
        } else {
          messageArea = getServiceArea();
          messageService = service;
        }
      } else {
        // Checks the area and service
        if (service.getAreaNumber().getValue() != msg.getHeader().getServiceArea().getValue()) {
          throw CNESMALContext.createException("Unexpected area: " + msg.getHeader().getServiceArea());
        }
        if (service.getServiceNumber().getValue() != msg.getHeader().getService().getValue()) {
          throw CNESMALContext.createException("Unexpected service: " + msg.getHeader().getService());
        }
        messageArea = getServiceArea();
        messageService = service;
      }
      
      MALOperation messageOperation = messageService.getOperationByNumber(msg.getHeader().getOperation());
      if (messageOperation == null) {
        throw CNESMALContext.createException("Unexpected operation number: " + msg.getHeader().getOperation());
      }
      
      switch (msg.getHeader().getInteractionType().getOrdinal()) {
      case InteractionType._PUBSUB_INDEX:
        if (msg.getHeader().getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
          if (msg.getHeader().getIsErrorMessage().booleanValue()) {
            MALErrorBody body = (MALErrorBody) msg.getBody();
            Enumeration<SubscriptionManager.SubscriptionContext> contexts = subscriptionManager.getContexts();
            while (contexts.hasMoreElements()) {
              SubscriptionManager.SubscriptionContext ctx = contexts.nextElement();
              ctx.getListener().notifyErrorReceived(msg.getHeader(), body, msg.getQoSProperties());
            }
          } else {
            MALNotifyBody body = (MALNotifyBody) msg.getBody();
            Identifier subscriptionId = body.getSubscriptionId();
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, "Notify subscription " + subscriptionId);
            MALInteractionListener listener = (MALInteractionListener) subscriptionManager.getListener(subscriptionId);
            if (listener != null) {
              listener.notifyReceived(msg.getHeader(), body, msg.getQoSProperties());
            } else {
              if (logger.isLoggable(BasicLevel.WARN))
                logger.log(BasicLevel.WARN, "Listener not found for message: " + msg);
            }
          }
        } else if (msg.getHeader().getInteractionStage().getValue() == 
          MALPubSubOperation._REGISTER_ACK_STAGE) {
          interactionManager.signalResponse(messageOperation, msg);
        } else if (msg.getHeader().getInteractionStage().getValue() == 
          MALPubSubOperation._DEREGISTER_ACK_STAGE) {
          interactionManager.signalResponse(messageOperation, msg);
        } else {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "Unexpected interaction stage: " + 
                  msg.getHeader().getInteractionStage().getValue());
        }
        break;
      default:
        boolean complete = interactionManager.signalResponse(messageOperation, msg);
        if (complete) {
          executorIndexes.remove(msg.getHeader().getTransactionId());
        }
      }
    }

    public boolean runnable() {
      Long tid = getMessage().getHeader().getTransactionId();
      int index = runningTransactions.indexOf(tid);
      return (index == -1);
    }

    // Do not synchronize on CNESMALConsumer (deadlock)
    public void init() {
      Long tid = getMessage().getHeader().getTransactionId();
      if (tid != null) {
        runningTransactions.add(tid);
      }
    }

    public void finalizeTask() {
      Long tid = getMessage().getHeader().getTransactionId();
      if (tid != null) {
        runningTransactions.remove(tid);
      }
    }

    @Override
    protected void onDeliveryError(MOErrorException error) throws MALException {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Failed to deliver message: " + getMessage() + " caused by: " + error);
    }
    
    public int getExecutorIndex() {
      return executorIndex;
    }
    
    public void setExecutorIndex(int index) throws MALException {
      MALMessage msg = getMessage();
      switch (msg.getHeader().getInteractionType().getOrdinal()) {
      case InteractionType._INVOKE_INDEX:
      case InteractionType._PROGRESS_INDEX:
        executorIndexes.put(msg.getHeader().getTransactionId(), index);
        break;
      case InteractionType._PUBSUB_INDEX:
        if (msg.getHeader().getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
          if (msg.getHeader().getIsErrorMessage().booleanValue()) {
            executorIndexes.put(msg.getHeader().getTransactionId(), index);
          } else {
            MALNotifyBody body = (MALNotifyBody) msg.getBody();
            // The subscription id decoding should happen only once with the
            // first notify received
            Identifier subscriptionId = body.getSubscriptionId();
            Long tid = msg.getHeader().getTransactionId();
            synchronized (subscriptionManager) {
              boolean registered = subscriptionManager.registerTransactionId(
                  subscriptionId, tid);
              if (registered) {
                executorIndexes.put(tid, index);
              }
            }
          }
        }
        break;
      default:
        return;
      }
    }
    
  }
  
  public long getPriority() {
    return priority.getValue();
  }
  
  public String getQoSLevelAsString() {
    return qosLevel.toString();
  }
  
  public String getAreaName() {
    return getServiceArea().getName().getValue();
  }

  public int getAreaNumber() {
    return getServiceArea().getNumber().getValue();
  }

  public String getServiceName() {
    return getService().getName().getValue();
  }

  public int getServiceNumber() {
    return getService().getServiceNumber().getValue();
  }

  /**
   * TODO SL revert to area version
   *
  public int getAreaVersion() {
    return getService().getArea().getVersion().getValue();
  }
  */

  public int getServiceVersion() {
    return getService().getServiceVersion().getValue();
  }

  public int getSubscriptionCount() {
    return subscriptionManager.size();
  }

  public String getURIToAsString() {
    if (uriTo != null) {
      return uriTo.getValue();
    } else {
      return null;
    }
  }
  
  public String getBrokerURIAsString() {
    if (brokerUri != null) {
      return brokerUri.getValue();
    } else {
      return null;
    }
  }

  public byte[] getAuthenticationIdValue() {
    try {
      return authenticationId.getValue();
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", e);
      return null;
    }
  }
  
  public Blob getAuthenticationId() {
    return authenticationId;
  }

  public String getNetworkZone() {
    return networkZone.getValue();
  }

  public String getSessionType() {
    return sessionType.toString();
  }

  public List<Long> getRunningTransactions() {
    return runningTransactions;
  }
  
  public String[] getInteractions() {
    return interactionManager.getInteractions();
  }
  
  public int getInteractionCount() {
    return interactionManager.getInteractionCount();
  }
  
  public String getDomainAsString() {
    return domain.toString();
  }

  // TODO SL should add a supplements parameter
  public void continueInteraction(
      MALOperation op,
      UOctet lastInteractionStage,
      Time initiationTimestamp,
      Long transactionId, 
      MALInteractionListener listener)
      throws java.lang.IllegalArgumentException, MALException {
    if (op == null) throw new IllegalArgumentException(NULL_OP_ERROR);
    if (lastInteractionStage == null) throw new IllegalArgumentException("Null last interaction stage");
    if (initiationTimestamp == null) throw new IllegalArgumentException("Null initiation time stamp");
    if (transactionId == null) throw new IllegalArgumentException("Null transaction id");
    if (listener == null) throw new IllegalArgumentException("Null listener");
    MALMessageHeader initHeader = new CNESMALMessageHeader(
        getDestinationId(),
        authenticationId, 
        uriTo, 
        initiationTimestamp, 
        op.getInteractionType(), 
        new UOctet((short) 1), 
        transactionId, 
        getService().getAreaNumber(), 
        getService().getServiceNumber(), 
        op.getNumber(), 
        getService().getServiceVersion(), 
        Boolean.FALSE,
        new NamedValueList());  // the supplements field should come from the stub calling this function
    interactionManager.continueInteraction(op, initHeader, lastInteractionStage, listener);
  }
  
  public void checkInteractionActivity(long currentTime, int timeout) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALConsumer-" + uriTo + ".checkInteractionActivity");
    interactionManager.checkInteractionActivity(currentTime, timeout);
  }
  
  private int getExecutorIndex(MALMessage msg) {
    Integer index = executorIndexes.get(msg.getHeader().getTransactionId());
    if (index == null) {
      return -1;
    } else {
      return index;
    }
  }

  @Override
  protected MessageDeliveryTask<CNESMALConsumer> createMessageDeliveryTask(
      MALMessage msg) {
    int executorIndex;
    switch (msg.getHeader().getInteractionType().getOrdinal()) {
    case InteractionType._INVOKE_INDEX:
    case InteractionType._PROGRESS_INDEX:
      executorIndex = getExecutorIndex(msg);
      break;
    case InteractionType._PUBSUB_INDEX:
      if (msg.getHeader().getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
        executorIndex = getExecutorIndex(msg);
      } else {
        executorIndex = -1;
      }
      break;
    default:
      executorIndex = -1;
    }
    return new ConsumerTask(msg, executorIndex);
  }

  @Override
  protected void removeFromDispatcher(MessageDispatcher messageDispatcher) throws MALException {
    messageDispatcher.removeConsumer(this);
  }

  @Override
  protected void handleTransmitError(MALMessageHeader header,
      MOErrorException standardError) throws MALException {
    // Nothing to do
  }

  public Blob setAuthenticationId(Blob newAuthenticationId) {
    Blob previous = authenticationId;
    authenticationId = newAuthenticationId;
    return previous;
  }
  
}
