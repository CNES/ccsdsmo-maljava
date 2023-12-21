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
package fr.cnes.mal.provider;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALPublisher;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
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
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALDeregisterBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.ccsds.moims.mo.mal.transport.MALPublishRegisterBody;
import org.ccsds.moims.mo.mal.transport.MALRegisterBody;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.CNESMALMessageHeader;
import fr.cnes.mal.Interaction;
import fr.cnes.mal.InteractionManager;
import fr.cnes.mal.MessageDeliveryTask;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.MessageSender;
import fr.cnes.mal.Binding;
import fr.cnes.mal.SyncInteraction;
import fr.cnes.mal.broker.BrokerAdapter;
import fr.cnes.mal.broker.internal.Broker;
import fr.cnes.mal.broker.internal.BrokerNotification;
import fr.cnes.mal.broker.internal.BrokerPublication;
import fr.cnes.mal.broker.internal.BrokerSubscriptionUpdate;
import fr.cnes.mal.broker.internal.UnknownEntityException;
import fr.cnes.mal.broker.internal.UnknownPublisherException;
import fr.cnes.mal.broker.internal.UpdateCheckReport;

public class CNESMALProvider extends Binding implements MALProvider, CNESMALProviderMBean, MessageSender {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALProvider.class.getName());

  private MALInteractionHandler handler;
  
  private int publishedMessageCount;
  
  private long publishDuration;
  
  private long notifyDuration;
  
  private int publishedUpdateListSize;
  
  private Blob authenticationId;
  
  private Boolean isPublisher;
  
  private Identifier sharedBrokerUri;
  
  private Vector<CNESMALPublisher> publishers;
  
  private Map defaultQoSProperties;
  
  private NamedValueList supplements;
  
  private InteractionManager maps;
  
  private BrokerAdapter brokerAdapter;
  
  CNESMALProvider(
      CNESMALProviderManager providerManager,
      MALEndpoint endpoint,
      MALService service,
      MALInteractionHandler handler,
      Blob authenticationId,
      Map defaultQoSProperties,
      NamedValueList supplements,
      Boolean isPublisher,
      Identifier sharedBrokerUri,
      String jmxName,
      MessageDispatcher messageDispatcher) throws MALException {
    super(providerManager, service, endpoint, messageDispatcher, jmxName);
    this.handler = handler;
    this.authenticationId = authenticationId;
    this.defaultQoSProperties = defaultQoSProperties;
    this.supplements = supplements;
    this.isPublisher = isPublisher;
    this.sharedBrokerUri = sharedBrokerUri;
    maps = new InteractionManager();
    publishedMessageCount = 0;
    if (isPublisher.booleanValue()) {
      publishers = new Vector<CNESMALPublisher>();
      if (sharedBrokerUri == null) {
        brokerAdapter = new BrokerAdapter(new Broker(jmxName), 
          authenticationId);
      }
    }
  }
  
  public final boolean isPublisher() {
    return isPublisher.booleanValue();
  }
  
  public final boolean ownsPrivateBroker() {
    return (brokerAdapter != null);
  }

  public URI getBrokerURI() {
    if (sharedBrokerUri == null) {
      if (isPublisher.booleanValue()) {
        return getURI();
      } else {
        return null;
      }
    } else {
      return new URI(sharedBrokerUri.getValue());
    }
  }

  public Identifier getBrokerDestinationId() {
    if (sharedBrokerUri == null) {
      if (isPublisher.booleanValue()) {
        return getDestinationId();
      } else {
        return null;
      }
    } else {
      return sharedBrokerUri;
    }
  }

  class ProviderTask extends MessageDeliveryTask<CNESMALProvider> {

    public ProviderTask(MALMessage msg) {
      super(msg, CNESMALProvider.this);
    }

    private void replyError(MOErrorException error) throws MALInteractionException, MALException {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "ProviderTask.replyError(" + error + ')');
      MALService service = getService();
      MALMessage msg = getMessage();
      MALOperation operation = service.getOperationByNumber(msg.getHeader()
          .getOperation());
      MALMessageHeader header = msg.getHeader();

      switch (header.getInteractionType().getOrdinal()) {
      case InteractionType._SEND_INDEX:
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Failed SEND interaction: " + 
              ((Union) error.getExtraInformation()).getStringValue());
        break;
      case InteractionType._SUBMIT_INDEX:
        CNESMALSubmit submit = new CNESMALSubmit(header, CNESMALProvider.this, msg,
            operation, authenticationId, supplements);
        submit.sendError(error);
        break;
      case InteractionType._REQUEST_INDEX:
        CNESMALRequest request = new CNESMALRequest(header, CNESMALProvider.this,
            msg, operation, authenticationId, supplements);
        request.sendError(error);
        break;
      case InteractionType._INVOKE_INDEX:
        CNESMALInvoke invoke = new CNESMALInvoke(header, CNESMALProvider.this, msg,
            operation, authenticationId, supplements);
        invoke.sendError(error);
        break;
      case InteractionType._PROGRESS_INDEX:
        CNESMALProgress progress = new CNESMALProgress(header, CNESMALProvider.this,
            msg, operation, authenticationId, supplements);
        progress.sendError(error);
        break;
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId, supplements);
          register.sendError(error);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALProvider.this, msg, operation, authenticationId, supplements);
          deregister.sendError(error);
        }  else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          sendPublishError(header.getFrom(), 
              (MALPubSubOperation) operation, 
              header.getSupplements(),
              msg.getQoSProperties(), 
              header.getTransactionId(),
              MALHelper.UNKNOWN_ERROR_NUMBER,
              null);
        } else {
          throw CNESMALContext.createException("Unexpected PUBSUB stage: " + header.getInteractionStage());
        }
        break;
      default:
        throw CNESMALContext.createException("Unexpected interaction: " + header.getInteractionType());
      }
    }
    
    public MALPublishInteractionListener getPublishListener(MALMessageHeader header) {
      synchronized (publishers) {
        for (int i = 0; i < publishers.size(); i++) {
          CNESMALPublisher publisher = publishers.elementAt(i);
          if (header.getTransactionId() != null &&
            header.getTransactionId().equals(publisher.getTid())) {
            MALPublishInteractionListener listener = publisher.getListener();
            return listener;
          }
        }
      }
      return null;
    }
    
    @Override
    protected void onDeliveryError(MOErrorException error) throws MALInteractionException, MALException {
      replyError(error);
    }

    @Override
    protected void deliverMessage() throws MALInteractionException,
        MALException {
      MALService service = getService();
      MALMessage msg = getMessage();
      MALOperation operation = service.getOperationByNumber(msg.getHeader()
          .getOperation());
      MALMessageHeader header = msg.getHeader();
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "header=" + header);
      switch (msg.getHeader().getInteractionType().getOrdinal()) {
      case InteractionType._SEND_INDEX:
        CNESMALInteraction interaction = new CNESMALInteraction(header,
            CNESMALProvider.this, msg, operation, authenticationId, supplements);
        handler.handleSend(interaction, msg.getBody());
        break;
      case InteractionType._SUBMIT_INDEX:
        CNESMALSubmit submit = new CNESMALSubmit(header, CNESMALProvider.this,
            msg, operation, authenticationId, supplements);
        try {
          handler.handleSubmit(submit, msg.getBody());
        } catch (MALInteractionException exc) {
          submit.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._REQUEST_INDEX:
        CNESMALRequest request = new CNESMALRequest(header,
            CNESMALProvider.this, msg, operation, authenticationId, supplements);
        try {
          handler.handleRequest(request, msg.getBody());
        } catch (MALInteractionException exc) {
          request.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._INVOKE_INDEX:
        CNESMALInvoke invoke = new CNESMALInvoke(header, CNESMALProvider.this,
            msg, operation, authenticationId, supplements);
        try {
          handler.handleInvoke(invoke, msg.getBody());
        } catch (MALInteractionException exc) {
          invoke.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._PROGRESS_INDEX:
        CNESMALProgress progress = new CNESMALProgress(header,
            CNESMALProvider.this, msg, operation, authenticationId, supplements);
        try {
          handler.handleProgress(progress, msg.getBody());
        } catch (MALInteractionException exc) {
          progress.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId, supplements);
          MALRegisterBody registerBody = (MALRegisterBody) msg.getBody();
          Subscription subscription = registerBody.getSubscription();
          brokerAdapter.handleRegister(header, subscription,
              msg.getQoSProperties());
          register.sendAcknowledgement();
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALProvider.this, msg, operation, authenticationId, supplements);
          MALDeregisterBody deregisterBody = (MALDeregisterBody) msg.getBody();
          IdentifierList subIdList = deregisterBody.getSubscriptionIds();
          brokerAdapter.handleDeregister(header, subIdList);
          deregister.sendAcknowledgement();
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE) {
          maps.signalResponse(operation, msg);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE) {
          maps.signalResponse(operation, msg);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_STAGE) {
          if (header.getIsErrorMessage().booleanValue()) {
            MALPublishInteractionListener listener = getPublishListener(header);
            if (listener != null) {
              MALErrorBody errorBody = (MALErrorBody) msg.getBody();
              listener.publishErrorReceived(header, errorBody,
                  msg.getQoSProperties());
            } else {
              if (logger.isLoggable(BasicLevel.WARN))
                logger.log(BasicLevel.WARN, "Publish listener not found: "
                    + header);
              if (logger.isLoggable(BasicLevel.DEBUG))
                logger.log(BasicLevel.DEBUG, "Publishers: " + publishers);
            }
          } else {
            MALPublishBody publishBody = (MALPublishBody) msg.getBody();
            handlePublish((MALPubSubOperation) operation, header, header.getSupplements(),
                msg.getQoSProperties(), publishBody.getUpdateHeader(),
                publishBody.getUpdateObjects());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
          CNESMALPublishRegister register = new CNESMALPublishRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId, supplements);
          MALPublishRegisterBody publishRegisterBody = (MALPublishRegisterBody) msg
              .getBody();
          IdentifierList subKeys = publishRegisterBody.getSubscriptionKeyNames();
          AttributeTypeList keyTypes = publishRegisterBody.getSubscriptionKeyTypes();
          try {
            if (brokerAdapter != null) {
              brokerAdapter.handlePublishRegister(header, subKeys, keyTypes);
              register.sendAcknowledgement();
            } else {
              register.sendError(new MOErrorException(
                  MALHelper.INTERNAL_ERROR_NUMBER, new Union("Not a broker")));
            }
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "", e);
            register.sendError(e.getStandardError());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
          CNESMALPublishDeregister deregister = new CNESMALPublishDeregister(
              header, CNESMALProvider.this, msg, operation, authenticationId, supplements);
          try {
            brokerAdapter.handlePublishDeregister(header);
            deregister.sendAcknowledgement();
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "", e);
            deregister.sendError(e.getStandardError());
          }
        } else {
          throw CNESMALContext.createException("Unexpected PUBSUB stage: "
              + header.getInteractionStage());
        }
        break;
      default:
        throw CNESMALContext.createException("Unexpected message type: "
            + msg.getHeader().getInteractionType());
      }
    }

    public boolean runnable() {
      return true;
    }

    public void init() {
      // Nothing to do
    }

    public void finalizeTask() {
      // Nothing to do
    }
  }

  public MALService getService() {
    return super.getService();
  }
  
  /**
   * Unable to find the service area
   * remove the function
   * 
  public String getAreaName() {
    return getService().getArea().getName().getValue();
  }
  */

  public int getAreaNumber() {
    // return getService().getArea().getNumber().getValue();
    return getService().getAreaNumber().getValue();
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

  public String getBrokerURIAsString() {
    URI uri = getBrokerURI();
    if (uri != null) return uri.getValue();
    else return null;
  }
  
  public int getPublishedMessageCount() {
    return publishedMessageCount;
  }
  
  public long getPublishDuration() {
    return publishDuration;
  }
  
  public long getNotifyDuration() {
    return notifyDuration;
  }
  
  public int getPublishedUpdateListSize() {
    return publishedUpdateListSize;
  }
  
  MALMessageHeader createPubSubHeader(
      UOctet stage,
      MALOperation op, 
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority,
      Long transactionId,
      Boolean isError,
      NamedValueList supplements) {
    return new CNESMALMessageHeader(
        getDestinationId(), authenticationId, getDestinationId(), 
        new Time(System.currentTimeMillis()), InteractionType.PUBSUB, 
        stage, transactionId, op.getServiceKey().getAreaNumber(), 
        op.getServiceKey().getServiceNumber(), op.getNumber(), 
        getService().getServiceVersion(), isError, supplements);
  }

  private void sendPublishError(
      Identifier uriTo,
      MALPubSubOperation op, 
      NamedValueList supplements,
      Map publishQosProps,
      Long tid,
      UInteger errorNumber,
      Object extraInformation) throws MALInteractionException, MALException  {
    MALMessage msg = createMessage(
        authenticationId, uriTo,
        new Time(System.currentTimeMillis()),
        tid,
        Boolean.TRUE,
        op, MALPubSubOperation.PUBLISH_STAGE, 
        supplements, publishQosProps,
        errorNumber,
        extraInformation);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "sendPublishError: " + msg);
    if (msg.getHeader().getTo().equals(getURI())) {
      onMessage(null, msg);
    } else {
      sendMessage(msg);
    }
  }
  
  MALMessage publish(
      MALPubSubOperation op,
      NamedValueList supplements,
      Map publishQosProps,
      Long tid,
      UpdateHeader updateHeader,
      Object... updateObjects) throws MALInteractionException, MALException {
    checkClosed();
    return publish(getDestinationId(), op, supplements, publishQosProps, tid,
        updateHeader, updateObjects);
  }

  private MALMessage publish(
      Identifier uriFrom,
      MALPubSubOperation op, 
      NamedValueList supplements,
      Map publishQosProps,
      Long tid,
      UpdateHeader updateHeader,
      Object... updateObjects) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.publish(" +
          uriFrom + ',' +
          op + ',' + 
          updateHeader + ',' + 
          updateObjects + ',' + 
          publishQosProps + ',' + tid + ')');
    publishedMessageCount++;
    publishedUpdateListSize = 1;
    
    if (tid == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Null transaction id");
      MOErrorException error = new MOErrorException(MALHelper.INCORRECT_STATE_ERROR_NUMBER, null);
      throw new MALInteractionException(error);
    }
    
    MALMessage msg;
    if (brokerAdapter != null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Local publish");
      // TODO SL use supplements ?
      msg = localPublish(uriFrom, op, new NamedValueList(), publishQosProps, tid,
          updateHeader, updateObjects);
    } else {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Remote publish");
      boolean timeInstrumented = getManager().getMalContext().isTimeInstrumented();
      long publishStart = 0;
      if (timeInstrumented) {
        publishStart = System.nanoTime();
      }
      if (publishQosProps == null) {
        publishQosProps = defaultQoSProperties;
      }
      Object[] bodyElements = new Object[updateObjects.length + 1];
      System.arraycopy(updateObjects, 0, bodyElements, 1, updateObjects.length);
      bodyElements[0] = updateHeader;
      msg = createMessage(
          authenticationId, getBrokerDestinationId(),
          new Time(System.currentTimeMillis()),
          tid,
          Boolean.FALSE,
          op,
          MALPubSubOperation.PUBLISH_STAGE, 
          // TODO SL use supplements ?
          // supplements,
          new NamedValueList(),
          publishQosProps,
          bodyElements);
      sendMessage(msg);
      
      if (timeInstrumented) {
        publishDuration = System.nanoTime() - publishStart;
      }
    }
    return msg;
  }
  
  private MALMessage localPublish(
      Identifier uriFrom,
      MALPubSubOperation op, 
      NamedValueList supplements,
      Map publishQosProps,
      Long tid,
      UpdateHeader updateHeader,
      Object... updateObjects) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.localPublish()");
    
    Object[] publishBodyElements = new Object[updateObjects.length + 1];
    System.arraycopy(updateObjects, 0, publishBodyElements, 1, updateObjects.length);
    publishBodyElements[0] = updateHeader;
    
    MALMessage msg = createMessage(
        authenticationId, getBrokerDestinationId(),
        new Time(System.currentTimeMillis()),
        tid,
        Boolean.FALSE,
        op,
        MALPubSubOperation.PUBLISH_STAGE, 
        supplements, publishQosProps,
        publishBodyElements);
    
    handlePublish(op, msg.getHeader(), supplements, publishQosProps, updateHeader, updateObjects);
    
    return msg;
  }
  
  private void handlePublish(
      MALPubSubOperation op, 
      MALMessageHeader header,
      NamedValueList supplements,
      Map publishQosProps,
      UpdateHeader updateHeader,
      Object... updateObjects) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.handlePublish(" +
          header.getServiceArea() + ':' + header.getService() + ':' + header.getOperation() +
          ", tid=" + header.getTransactionId() + ")");
    /*
    Identifier sessionName = header.getSessionName();
    String sessionNameS = null;
    if (sessionName != null) {
      sessionNameS = sessionName.getValue();
    }*/

    BrokerPublication publication = new BrokerPublication(
      header.getFrom(),
      updateHeader,
      updateObjects,
      header.getServiceArea(),
      header.getService(),
      header.getOperation(),
      header.getServiceVersion());

    BrokerNotification[] notifications;
    try {
      notifications = brokerAdapter.publish(publication);
    } catch (UnknownPublisherException upe) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", upe);
      MOErrorException error = new MOErrorException(MALHelper.INTERNAL_ERROR_NUMBER, null);
      sendPublishError(
          header.getFrom(),
          op,
          // TODO SL bug in testbed ?
          // supplements,
          new NamedValueList(),
          publishQosProps,
          header.getTransactionId(), 
          MALHelper.INTERNAL_ERROR_NUMBER, new Union(upe.toString()));
      return;
    } catch (UnknownEntityException uee) {
      // TODO SL pas sur que cela existe encore
      UpdateCheckReport report = uee.getReport();
      sendPublishError(
          header.getFrom(),
          op, 
          // TODO SL bug in testbed ?
          // supplements,
          new NamedValueList(),
          publishQosProps,
          header.getTransactionId(),
          MALHelper.UNKNOWN_ERROR_NUMBER, null);
      return;
    }
    
    if (notifications == null || notifications.length== 0) {
      return;
    }
    
    int messageNb = 0;
    for (int i = 0; i < notifications.length; i++) {
      messageNb += notifications[i].getSubscriptionUpdateList().size();
    }
    
    MALMessage[] notifyMessages = new MALMessage[messageNb];
    int messageIndex = 0;
    for (int i = 0; i < notifications.length; i++) {
      List<BrokerSubscriptionUpdate> subscriptionUpdates = 
          notifications[i].getSubscriptionUpdateList();
      for (int j = 0; j < subscriptionUpdates.size(); j++) {
        BrokerSubscriptionUpdate subscriptionUpdate = subscriptionUpdates.get(j);
        Object[] notifiedUpdateObjects = subscriptionUpdate.getUpdateObjects();
        Object[] bodyElements = new Object[notifiedUpdateObjects.length + 2];
        System.arraycopy(notifiedUpdateObjects, 0, bodyElements, 2, notifiedUpdateObjects.length);
        bodyElements[0] = subscriptionUpdate.getSubscriptionId();
        bodyElements[1] = subscriptionUpdate.getUpdateHeader();
        notifyMessages[messageIndex++] = createMessage(
          authenticationId,
          notifications[i].getSubscriberUri(),
          new Time(System.currentTimeMillis()), 
          notifications[i].getTransactionId(),
          Boolean.FALSE,
          op, MALPubSubOperation.NOTIFY_STAGE, 
          // TODO SL bug in testbed ?
          // supplements,
          new NamedValueList(),
          notifications[i].getQosProperties(),
          bodyElements);
      }
    }
    sendMessages(notifyMessages);
  }
  
  public Blob getAuthenticationId() {
    return authenticationId;
  }

  public Blob setAuthenticationId(Blob newAuthenticationId) {
    // added method to comply to ESA Java API
    Blob previous = authenticationId;
    authenticationId = newAuthenticationId;
    return previous;
  }

  public MALMessage publishRegister(
      MALPubSubOperation op, IdentifierList subKeys, AttributeTypeList keyTypes,
      NamedValueList supplements, Map publishQosProps,
      UInteger publishPriority, 
      boolean async,
      MALPublishInteractionListener listener) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.publishRegister(" + op + ',' +
          supplements + ',' + publishQosProps + ',' + publishPriority + ')');
    checkClosed();
    Long tid = maps.getTransactionId();
    MALMessage msg = createMessage(
        authenticationId,
        getBrokerDestinationId(),
        new Time(System.currentTimeMillis()), 
        tid,
        Boolean.FALSE,
        op, MALPubSubOperation.PUBLISH_REGISTER_STAGE,
        supplements,
        publishQosProps,
        subKeys,
        keyTypes);
    
    Interaction interaction;
    if (async) {
      interaction = new AsyncPublishRegisterInteraction(op, msg.getHeader(), listener);
    } else {
      interaction = new PublishRegisterInteraction(op, msg.getHeader());
    }
    
    maps.putInteraction(tid, interaction);

    if (brokerAdapter != null) {
      brokerAdapter.handlePublishRegister(msg.getHeader(), subKeys, keyTypes);
      MALMessage publishRegisterAckMsg = createMessage(
          authenticationId,
          msg.getHeader().getFrom(),
          new Time(System.currentTimeMillis()), 
          tid,
          Boolean.FALSE,
          op, MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE, 
          supplements,
          publishQosProps);
      sendMessage(publishRegisterAckMsg);
    } else {
      if (publishQosProps == null) {
        publishQosProps = defaultQoSProperties;
      }
      sendMessage(msg);
    }
    
    if (! async) {
      ((SyncInteraction) interaction).waitForResponse();
    }
    return msg;
  }
  
  public MALMessage publishDeregister(
      MALPubSubOperation op,
      NamedValueList supplements, Map publishQosProps,
      UInteger publishPriority,
      boolean async,
      MALPublishInteractionListener listener) throws MALInteractionException, MALException {
    checkClosed();
    Long tid = maps.getTransactionId();
    MALMessage msg = createMessage(
        authenticationId,
        getBrokerDestinationId(),
        new Time(System.currentTimeMillis()), 
        tid,
        Boolean.FALSE,
        op, MALPubSubOperation.PUBLISH_DEREGISTER_STAGE, 
        supplements,
        publishQosProps);
    
    Interaction interaction;
    if (async) {
      interaction = new AsyncPublishDeregisterInteraction(op, msg.getHeader(), listener);
    } else {
      interaction = new PublishDeregisterInteraction(op, msg.getHeader());
    }
    
    maps.putInteraction(tid, interaction);
    
    if (brokerAdapter != null) {
      brokerAdapter.handlePublishDeregister(msg.getHeader());
      MALMessage publishRegisterAckMsg = createMessage(
          authenticationId,
          msg.getHeader().getFrom(),
          new Time(System.currentTimeMillis()), 
          tid,
          Boolean.FALSE,
          op, MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE, 
          supplements,
          publishQosProps);
      onMessage(null, publishRegisterAckMsg);
    } else {
      if (publishQosProps == null) {
        publishQosProps = defaultQoSProperties;
      }
      sendMessage(msg);
    }
    
    if (! async) {
      ((SyncInteraction) interaction).waitForResponse();
    }
    return msg;
  }
  
  public Blob getBrokerAuthenticationId() {
    return authenticationId;
  }

  public Blob setBrokerAuthenticationId(Blob newAuthenticationId) {
    // added method to comply to ESA Java API
    Blob previous = authenticationId;
    authenticationId = newAuthenticationId;
    return previous;
  }

  public MALPublisher createPublisher(MALPubSubOperation op,
      IdentifierList domain, SessionType sessionType,
      Identifier sessionName, QoSLevel publishQos, Map publishQosProps, 
      NamedValueList supplements) throws MALException {
    return createPublisher(op, domain, new Identifier(""), sessionType, sessionName, publishQos, publishQosProps, new UInteger(1), supplements);
  }
  
  public MALPublisher createPublisher(MALPubSubOperation op,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel publishQos, Map publishQosProps, 
      UInteger publishPriority, NamedValueList supplements) throws MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (domain == null) throw new IllegalArgumentException("Null domain");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (publishQos == null) throw new IllegalArgumentException("Null QoS");
    if (publishPriority == null) throw new IllegalArgumentException("Null priority");
    // Currently ignore the supplements for the provider, use empty list in the testbed

    CNESMALPublisher newPublisher = null;
    boolean found = false;
    synchronized (publishers) {
      for (int i = 0; i < publishers.size(); i++) {
        CNESMALPublisher publisher = publishers.elementAt(i);
        if (publisher.getDomain().equals(domain) &&
          publisher.getNetworkZone().equals(networkZone) &&
          publisher.getSessionType().equals(sessionType) &&
          publisher.getSessionName().equals(sessionName)) {
          newPublisher = publisher;
          found = true;
        }
      }
      if (!found) {
        newPublisher = new CNESMALPublisher(this, op, domain, networkZone, sessionType, sessionName, publishQos, publishQosProps,
                                            publishPriority);
        publishers.addElement(newPublisher);
      }
    }
    return newPublisher;
  }
  
  void closePublisher(CNESMALPublisher publisher) {
    publishers.removeElement(publisher);
  }
  
  protected void handleTransmitError(MALMessageHeader header,
      MOErrorException standardError) throws MALException {
    if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX &&
        header.getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
      if (standardError.getErrorNumber().getValue() == MALHelper._DESTINATION_UNKNOWN_ERROR_NUMBER) {
        // A message could not be delivered to a subscriber
        // because it is unknown
        // Deregister the subscriber
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Deregister unknown subscriber: " + header);
        try {
          brokerAdapter.deregister(header);
        } catch (MALException e) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "", e);
        }
      }
    }
  }

  @Override
  protected MessageDeliveryTask createMessageDeliveryTask(
      MALMessage msg) {
    return new ProviderTask(msg);
  }

  @Override
  protected void finalizeBinding() throws MALException {
    if (brokerAdapter != null) {
      brokerAdapter.unregisterMBeans();
    }
  }

  @Override
  protected void removeFromDispatcher(MessageDispatcher messageDispatcher)
      throws MALException {
    messageDispatcher.removeProvider(this);
  }

  public String getAreaName() {
    return getServiceArea().getName().getValue();
  }

}
