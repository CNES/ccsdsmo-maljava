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
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALPublisher;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
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
  
  private URI sharedBrokerUri;
  
  private Vector<CNESMALPublisher> publishers;
  
  private Map defaultQoSProperties;
  
  private InteractionManager maps;
  
  private BrokerAdapter brokerAdapter;
  
  CNESMALProvider(
      CNESMALProviderManager providerManager,
      MALEndpoint endpoint,
      MALService service,
      MALInteractionHandler handler,
      Blob authenticationId,
      Map defaultQoSProperties,
      Boolean isPublisher,
      URI sharedBrokerUri,
      String jmxName,
      MessageDispatcher messageDispatcher) throws MALException {
    super(providerManager, service, endpoint, messageDispatcher, jmxName);
    this.handler = handler;
    this.authenticationId = authenticationId;
    this.defaultQoSProperties = defaultQoSProperties;
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
      return sharedBrokerUri;
    }
  }

  class ProviderTask extends MessageDeliveryTask<CNESMALProvider> {

    public ProviderTask(MALMessage msg) {
      super(msg, CNESMALProvider.this);
    }

    private void replyError(MALStandardError error) throws MALInteractionException, MALException {
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
            operation, authenticationId);
        submit.sendError(error);
        break;
      case InteractionType._REQUEST_INDEX:
        CNESMALRequest request = new CNESMALRequest(header, CNESMALProvider.this,
            msg, operation, authenticationId);
        request.sendError(error);
        break;
      case InteractionType._INVOKE_INDEX:
        CNESMALInvoke invoke = new CNESMALInvoke(header, CNESMALProvider.this, msg,
            operation, authenticationId);
        invoke.sendError(error);
        break;
      case InteractionType._PROGRESS_INDEX:
        CNESMALProgress progress = new CNESMALProgress(header, CNESMALProvider.this,
            msg, operation, authenticationId);
        progress.sendError(error);
        break;
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId);
          register.sendError(error);
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALProvider.this, msg, operation, authenticationId);
          deregister.sendError(error);
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
    protected void onDeliveryError(MALStandardError error) throws MALInteractionException, MALException {
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
            CNESMALProvider.this, msg, operation, authenticationId);
        handler.handleSend(interaction, msg.getBody());
        break;
      case InteractionType._SUBMIT_INDEX:
        CNESMALSubmit submit = new CNESMALSubmit(header, CNESMALProvider.this,
            msg, operation, authenticationId);
        try {
          handler.handleSubmit(submit, msg.getBody());
        } catch (MALInteractionException exc) {
          submit.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._REQUEST_INDEX:
        CNESMALRequest request = new CNESMALRequest(header,
            CNESMALProvider.this, msg, operation, authenticationId);
        try {
          handler.handleRequest(request, msg.getBody());
        } catch (MALInteractionException exc) {
          request.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._INVOKE_INDEX:
        CNESMALInvoke invoke = new CNESMALInvoke(header, CNESMALProvider.this,
            msg, operation, authenticationId);
        try {
          handler.handleInvoke(invoke, msg.getBody());
        } catch (MALInteractionException exc) {
          invoke.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._PROGRESS_INDEX:
        CNESMALProgress progress = new CNESMALProgress(header,
            CNESMALProvider.this, msg, operation, authenticationId);
        try {
          handler.handleProgress(progress, msg.getBody());
        } catch (MALInteractionException exc) {
          progress.sendError(exc.getStandardError());
        }
        break;
      case InteractionType._PUBSUB_INDEX:
        if (header.getInteractionStage().getValue() == MALPubSubOperation._REGISTER_STAGE) {
          CNESMALRegister register = new CNESMALRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId);
          MALRegisterBody registerBody = (MALRegisterBody) msg.getBody();
          Subscription subscription = registerBody.getSubscription();
          brokerAdapter.handleRegister(header, subscription,
              msg.getQoSProperties());
          register.sendAcknowledgement();
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
          CNESMALDeregister deregister = new CNESMALDeregister(header,
              CNESMALProvider.this, msg, operation, authenticationId);
          MALDeregisterBody deregisterBody = (MALDeregisterBody) msg.getBody();
          IdentifierList subIdList = deregisterBody.getIdentifierList();
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
            handlePublish((MALPubSubOperation) operation, header,
                msg.getQoSProperties(), publishBody.getUpdateHeaderList(),
                publishBody.getUpdateLists());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
          CNESMALPublishRegister register = new CNESMALPublishRegister(header,
              CNESMALProvider.this, msg, operation, authenticationId);
          MALPublishRegisterBody publishRegisterBody = (MALPublishRegisterBody) msg
              .getBody();
          EntityKeyList entityKeys = publishRegisterBody.getEntityKeyList();
          try {
            if (brokerAdapter != null) {
              brokerAdapter.handlePublishRegister(header, entityKeys);
              register.sendAcknowledgement();
            } else {
              register.sendError(new MALStandardError(
                  MALHelper.INTERNAL_ERROR_NUMBER, new Union("Not a broker")));
            }
          } catch (MALInteractionException e) {
            if (logger.isLoggable(BasicLevel.WARN))
              logger.log(BasicLevel.WARN, "", e);
            register.sendError(e.getStandardError());
          }
        } else if (header.getInteractionStage().getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
          CNESMALPublishDeregister deregister = new CNESMALPublishDeregister(
              header, CNESMALProvider.this, msg, operation, authenticationId);
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
  
  public String getAreaName() {
    return getService().getArea().getName().getValue();
  }

  public int getAreaNumber() {
    return getService().getArea().getNumber().getValue();
  }

  public String getServiceName() {
    return getService().getName().getValue();
  }

  public int getServiceNumber() {
    return getService().getNumber().getValue();
  }

  public int getAreaVersion() {
    return getService().getArea().getVersion().getValue();
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
      Boolean isError) {
    return new CNESMALMessageHeader(
        getURI(), authenticationId, getBrokerURI(), 
        new Time(System.currentTimeMillis()), publishQos, publishPriority, 
        domain, networkZone, sessionType, sessionName, InteractionType.PUBSUB, 
        stage, transactionId, op.getService().getArea().getNumber(), 
        op.getService().getNumber(), op.getNumber(), 
        getService().getArea().getVersion(), isError);
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
      Long tid,
      UInteger errorNumber,
      Object extraInformation) throws MALInteractionException, MALException  {
    MALMessage msg = createMessage(
        authenticationId, uriTo,
        new Time(System.currentTimeMillis()), publishQos,
        publishPriority, domain,
        networkZone, sessionType, sessionName,
        tid,
        Boolean.TRUE,
        op, MALPubSubOperation.PUBLISH_STAGE, 
        publishQosProps,
        errorNumber,
        extraInformation);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "sendPublishError: " + msg);
    if (msg.getHeader().getURITo().equals(getURI())) {
      onMessage(null, msg);
    } else {
      sendMessage(msg);
    }
  }
  
  MALMessage publish(
      MALPubSubOperation op,
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority,
      Long tid,
      UpdateHeaderList updateHeaderList,
      List... updateLists) throws MALInteractionException, MALException {
    checkClosed();
    return publish(getURI(), op, domain, 
        networkZone, sessionType, sessionName,
        publishQos, publishQosProps, publishPriority, tid,
        updateHeaderList, updateLists);
  }

  private MALMessage publish(
      URI uriFrom,
      MALPubSubOperation op, 
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority,
      Long tid,
      UpdateHeaderList updateHeaderList,
      List... updateLists) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.publish(" +
          uriFrom + ',' +
          op + ',' + 
          updateHeaderList + ',' + 
          updateLists + ',' + 
          domain + ',' + 
          networkZone + ',' + 
          sessionType + ',' + 
          sessionName + ',' + 
          publishQos + ',' + 
          publishPriority + ',' + 
          publishQosProps + ',' + tid + ')');
    publishedMessageCount++;
    publishedUpdateListSize = updateHeaderList.size();
    
    if (tid == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Null transaction id");
      MALStandardError error = new MALStandardError(MALHelper.INCORRECT_STATE_ERROR_NUMBER, null);
      throw new MALInteractionException(error);
    }
    
    MALMessage msg;
    if (brokerAdapter != null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Local publish");
      msg = localPublish(uriFrom, op, domain, networkZone, sessionType,
          sessionName, publishQos, publishQosProps, publishPriority, tid,
          updateHeaderList, updateLists);
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
      Object[] bodyElements = new Object[updateLists.length + 1];
      System.arraycopy(updateLists, 0, bodyElements, 1, updateLists.length);
      bodyElements[0] = updateHeaderList;
      msg = createMessage(
          authenticationId, getBrokerURI(),
          new Time(System.currentTimeMillis()), publishQos,
          publishPriority, domain,
          networkZone, sessionType, sessionName,
          tid,
          Boolean.FALSE,
          op,
          MALPubSubOperation.PUBLISH_STAGE, 
          publishQosProps,
          bodyElements);
      sendMessage(msg);
      
      if (timeInstrumented) {
        publishDuration = System.nanoTime() - publishStart;
      }
    }
    return msg;
  }
  
  private MALMessage localPublish(URI uriFrom,
      MALPubSubOperation op, 
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority,
      Long tid,
      UpdateHeaderList updateHeaderList,
      List... updateLists) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.localPublish()");
    
    Object[] publishBodyElements = new Object[updateLists.length + 1];
    System.arraycopy(updateLists, 0, publishBodyElements, 1, updateLists.length);
    publishBodyElements[0] = updateHeaderList;
    
    MALMessage msg = createMessage(
        authenticationId, getBrokerURI(),
        new Time(System.currentTimeMillis()), publishQos,
        publishPriority, domain,
        networkZone, sessionType, sessionName,
        tid,
        Boolean.FALSE,
        op,
        MALPubSubOperation.PUBLISH_STAGE, 
        publishQosProps,
        publishBodyElements);
    
    handlePublish(op, msg.getHeader(), publishQosProps, updateHeaderList, updateLists);
    
    return msg;
  }
  
  private void handlePublish(
      MALPubSubOperation op, 
      MALMessageHeader header,
      Map publishQosProps,
      UpdateHeaderList updateHeaderList,
      List... updateLists) throws MALInteractionException, MALException {
    /*
    Identifier sessionName = header.getSessionName();
    String sessionNameS = null;
    if (sessionName != null) {
      sessionNameS = sessionName.getValue();
    }*/

    BrokerPublication publication = new BrokerPublication(
      header.getURIFrom(),
      header.getDomain(),
      header.getNetworkZone(),
      header.getSession(),
      header.getSessionName(),
      updateHeaderList,
      updateLists,
      header.getServiceArea(),
      header.getService(),
      header.getOperation(),
      header.getAreaVersion());

    BrokerNotification[] notifications;
    try {
      notifications = brokerAdapter.publish(publication);
    } catch (UnknownPublisherException upe) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", upe);
      MALStandardError error = new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, null);
      sendPublishError(
          header.getURIFrom(),
          op,
          header.getDomain(), 
          header.getNetworkZone(), 
          header.getSession(), 
          header.getSessionName(), 
          header.getQoSlevel(),
          publishQosProps,
          header.getPriority(),
          header.getTransactionId(), 
          MALHelper.INTERNAL_ERROR_NUMBER, new Union(upe.toString()));
      return;
    } catch (UnknownEntityException uee) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", uee);
      UpdateCheckReport report = uee.getReport();
      EntityKeyList unknownEntityKeyList = new EntityKeyList();
      UpdateHeaderList failedUpdateHeaders = report.getFailedUpdateHeaders();
      for (int i = 0; i < failedUpdateHeaders.size(); i++) {
        EntityKey key = failedUpdateHeaders.get(i).getKey();
        unknownEntityKeyList.add(key);
      }
      sendPublishError(
          header.getURIFrom(),
          op, 
          header.getDomain(), 
          header.getNetworkZone(), 
          header.getSession(), 
          header.getSessionName(), 
          report.getQos(),
          publishQosProps,
          report.getPriority(),
          report.getTransactionId(),
          MALHelper.UNKNOWN_ERROR_NUMBER, unknownEntityKeyList);
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
        List[] notifiedUpdateLists = subscriptionUpdate.getUpdateLists();
        Object[] bodyElements = new Object[notifiedUpdateLists.length + 2];
        System.arraycopy(notifiedUpdateLists, 0, bodyElements, 2, notifiedUpdateLists.length);
        bodyElements[0] = subscriptionUpdate.getSubscriptionId();
        bodyElements[1] = subscriptionUpdate.getUpdateHeaders();
        notifyMessages[messageIndex++] = createMessage(
          authenticationId,
          notifications[i].getSubscriberUri(),
          new Time(System.currentTimeMillis()), 
          notifications[i].getQosLevel(),
          notifications[i].getPriority(), 
          notifications[i].getDomain(),
          notifications[i].getNetworkZone(), 
          notifications[i].getSessionType(), 
          notifications[i].getSessionName(),
          notifications[i].getTransactionId(),
          Boolean.FALSE,
          op, MALPubSubOperation.NOTIFY_STAGE, 
          notifications[i].getQosProperties(),
          bodyElements);
      }
    }
    sendMessages(notifyMessages);
  }
  
  Blob getAuthenticationId() {
    return authenticationId;
  }

  public MALMessage publishRegister(
      MALPubSubOperation op, EntityKeyList entityKeys,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel publishQos, Map publishQosProps,
      UInteger publishPriority, 
      boolean async,
      MALPublishInteractionListener listener) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProvider.publishRegister(" + op + ',' +
          domain + ',' + networkZone + ',' + sessionType + ',' + sessionName + ',' +
          publishQos + ',' + publishQosProps + ',' + publishPriority + ')');
    checkClosed();
    Long tid = maps.getTransactionId();
    MALMessage msg = createMessage(
        authenticationId,
        getBrokerURI(),
        new Time(System.currentTimeMillis()), 
        publishQos,
        publishPriority, 
        domain,
        networkZone, 
        sessionType, 
        sessionName,
        tid,
        Boolean.FALSE,
        op, MALPubSubOperation.PUBLISH_REGISTER_STAGE,
        publishQosProps,
        entityKeys);
    
    Interaction interaction;
    if (async) {
      interaction = new AsyncPublishRegisterInteraction(op, msg.getHeader(), listener);
    } else {
      interaction = new PublishRegisterInteraction(op, msg.getHeader());
    }
    
    maps.putInteraction(tid, interaction);

    if (brokerAdapter != null) {
      brokerAdapter.handlePublishRegister(msg.getHeader(), entityKeys);
      MALMessage publishRegisterAckMsg = createMessage(
          authenticationId,
          msg.getHeader().getURIFrom(),
          new Time(System.currentTimeMillis()), 
          publishQos,
          publishPriority, 
          domain,
          networkZone, 
          sessionType, 
          sessionName,
          tid,
          Boolean.FALSE,
          op, MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE, 
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
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel publishQos, Map publishQosProps,
      UInteger publishPriority,
      boolean async,
      MALPublishInteractionListener listener) throws MALInteractionException, MALException {
    checkClosed();
    Long tid = maps.getTransactionId();
    MALMessage msg = createMessage(
        authenticationId,
        getBrokerURI(),
        new Time(System.currentTimeMillis()), 
        publishQos,
        publishPriority, 
        domain,
        networkZone, 
        sessionType, 
        sessionName,
        tid,
        Boolean.FALSE,
        op, MALPubSubOperation.PUBLISH_DEREGISTER_STAGE, 
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
          msg.getHeader().getURIFrom(),
          new Time(System.currentTimeMillis()), 
          publishQos,
          publishPriority, 
          domain,
          networkZone, 
          sessionType, 
          sessionName,
          tid,
          Boolean.FALSE,
          op, MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE, 
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

  public MALPublisher createPublisher(MALPubSubOperation op,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel publishQos, Map publishQosProps, 
      UInteger publishPriority) throws MALException {
    checkClosed();
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (domain == null) throw new IllegalArgumentException("Null domain");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (publishQos == null) throw new IllegalArgumentException("Null QoS");
    if (publishPriority == null) throw new IllegalArgumentException("Null priority");
    CNESMALPublisher newPublisher = new CNESMALPublisher(
       this, op, domain, networkZone, sessionType, sessionName, publishQos, publishQosProps,
       publishPriority);
    synchronized (publishers) {
      for (int i = 0; i < publishers.size(); i++) {
        CNESMALPublisher publisher = publishers.elementAt(i);
        if (publisher.getDomain().equals(domain) &&
          publisher.getNetworkZone().equals(networkZone) &&
          publisher.getSessionType().equals(sessionType) &&
          publisher.getSessionName().equals(sessionName)) {
          newPublisher.setTid(publisher.getTid());
        }
      }
      publishers.addElement(newPublisher);
    }
    return newPublisher;
  }
  
  void closePublisher(CNESMALPublisher publisher) {
    publishers.removeElement(publisher);
  }
  
  protected void handleTransmitError(MALMessageHeader header,
      MALStandardError standardError) throws MALException {
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
  
}
