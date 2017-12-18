/*******************************************************************************
 * Copyright or © or Copr. CNES
 *
 * This software is a computer program whose purpose is to provide a 
 * framework for the CCSDS Mission Operations services.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package fr.cnes.ccsds.mo.transport.amqp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
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
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransmitMultipleErrorException;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.BindOk;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.AMQP.Queue.UnbindOk;
import com.rabbitmq.client.Channel;

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.dyade.aaa.common.Strings;

public class MALAMQPEndPoint implements MALEndpoint {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALAMQPEndPoint.class.getName());
  
  private MALAMQPTransport transport;
  private String localName;
  private String queueName;
  private String consumerId;
  private Channel channel;
  private boolean noack;
  private Hashtable subscriptions;
  private MessageConsumer consumer;
  private Hashtable publishRegisterContexts;
  private Map endPointProperties;
  private MALMessageListener listener;

  public MALAMQPEndPoint(MALAMQPTransport transport,
    String localName,
    String queueName,
    String consumerId,
    Channel channel,
    boolean noack,
    Map endPointProperties) throws MALException {
    this.transport = transport;
    this.localName =  localName;
    this.queueName = queueName;
    this.consumerId = consumerId;
    this.channel = channel;
    this.noack = noack;
    this.endPointProperties = endPointProperties;
    subscriptions = new Hashtable();
    consumer = null;
    //malEncoder = new MALByteArrayEncoder(transport.getElementStreamFactory());
    publishRegisterContexts = new Hashtable();
  }
  
  final Channel getChannel() {
    return channel;
  }
  
  final MALAMQPTransport getTransport() {
    return transport;
  }
  
  final Map getQoSProperties() {
    return endPointProperties;
  }
  
  final boolean getNoAck() {
    return noack;
  }

  public void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.close()");
    setMessageListener(null);
    try {
      if (localName == null) {
        channel.queueDelete(queueName);
      }
      channel.close();
    } catch (IOException e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }

  public URI getURI() {
    return MALAMQPHelper.getQueueUri(queueName);
  }

  public void sendMessage(MALMessage msg) throws MALTransmitErrorException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.sendMessage(" +
          msg + ')');
    try {
    int interactionType = msg.getHeader().getInteractionType().getOrdinal();
    if (interactionType == InteractionType._PUBSUB_INDEX) {
      short interactionStage = msg.getHeader().getInteractionStage().getValue();
      if (interactionStage == MALPubSubOperation._REGISTER_STAGE) {
        sendRegisterMessage(msg);
      } else if (interactionStage == MALPubSubOperation._PUBLISH_STAGE) {
        sendPublishMessage(msg);
      } else if (interactionStage == MALPubSubOperation._DEREGISTER_STAGE) {
        sendDeregisterMessage(msg);
      } else if (interactionStage == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
        sendPublishRegisterMessage(msg);
      } else if (interactionStage == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
        sendPublishDeregisterMessage(msg);
      } else {
        String exchangeName = "";
        URI uriTo = msg.getHeader().getURITo();
        String routingKey = MALAMQPHelper.getQueueName(uriTo);
        URI uriFrom = msg.getHeader().getURIFrom();
        String replyTo = MALAMQPHelper.getQueueName(uriFrom);
        defaultSendMessage(msg,
            exchangeName, routingKey, replyTo);
      }
    } else {
      String exchangeName = "";
      URI uriTo = msg.getHeader().getURITo();
      String routingKey = MALAMQPHelper.getQueueName(uriTo);
      URI uriFrom = msg.getHeader().getURIFrom();
      String replyTo = MALAMQPHelper.getQueueName(uriFrom);
      defaultSendMessage(msg,
        exchangeName, routingKey, replyTo);
      }
    } catch (MALException exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      throw new MALTransmitErrorException(msg.getHeader(), 
          new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, new Union(exc.toString())), 
          msg.getQoSProperties());
    }
  }

  public void defaultSendMessage(MALMessage msg,
      String exchangeName, String routingKey, String replyToQueueName) 
    throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.defaultSendMessage(" +
          msg + ',' +
          exchangeName + ',' + routingKey + ',' + replyToQueueName + ')');
    Map qosProperties = msg.getQoSProperties();

    MALArea messageArea = MALContextFactory.lookupArea(msg.getHeader().getServiceArea(), msg.getHeader().getAreaVersion());
    if (messageArea == null) {
      throw MALAMQPHelper.createMALException("Unknown area: " + msg.getHeader().getServiceArea() + " version: " + msg.getHeader().getAreaVersion());
    }
    
    MALService messageService = messageArea.getServiceByNumber(msg.getHeader().getService());
    if (messageService == null) {
      throw MALAMQPHelper.createMALException("Unknown service: " + msg.getHeader().getService());
    }
    
    MALOperation messageOperation = messageService.getOperationByNumber(msg.getHeader().getOperation());
    
    // Resolve all the message header fields
    int areaId = messageService.getArea().getNumber().getValue();
    byte[] authenticationId = msg.getHeader().getAuthenticationId().getValue();
    IdentifierList domainId = msg.getHeader().getDomain();
    short interactionStage;
    UOctet interactionStageB = msg.getHeader().getInteractionStage();
    if (interactionStageB == null) {
      interactionStage = 0;
    } else {
      interactionStage = interactionStageB.getValue();
    }
    int interactionType = msg.getHeader().getInteractionType().getOrdinal();
    String networkZone = msg.getHeader().getNetworkZone().getValue();
    int operation = messageService.getOperationByNumber(msg.getHeader().getOperation()).getNumber().getValue();
    int priority = (int) msg.getHeader().getPriority().getValue();
    int qosLevel = msg.getHeader().getQoSlevel().getOrdinal();
    int serviceId = messageService.getNumber().getValue();
    int sessionType = msg.getHeader().getSession().getOrdinal();
    Identifier sessionNameI = msg.getHeader().getSessionName();
    String sessionName;
    if (sessionNameI != null) {
      sessionName = sessionNameI.getValue();
    } else {
      sessionName = "";
    }
    long timeStamp = msg.getHeader().getTimestamp().getValue();
    Long transactionId = msg.getHeader().getTransactionId();
    short version = msg.getHeader().getAreaVersion().getValue();
    boolean isError = msg.getHeader().getIsErrorMessage().booleanValue();
    
    Integer ttl;
    if (qosProperties != null) {
      // Resolve the QoS property « time-to-live »
      ttl = (Integer) qosProperties.get("timeToLive");
    } else {
      ttl = null;
    }
    BasicProperties props = new BasicProperties();
    props.headers = new HashMap();
    if (authenticationId != null && authenticationId.length != 0) {
      props.headers.put(MALAMQPHelper.AUTHENTICATION_ID_HEADER_FIELD_NAME, new String(authenticationId));
    }
    if (domainId != null ) {
      props.headers.put(MALAMQPHelper.DOMAIN_HEADER_FIELD_NAME, MALAMQPHelper.domainToString(domainId));
    }
    if (networkZone != null) {
      props.headers.put(MALAMQPHelper.NETWORK_ZONE_HEADER_FIELD_NAME, networkZone);
    }
    props.headers.put(MALAMQPHelper.SESSION_HEADER_FIELD_NAME, new Integer(sessionType));
    props.headers.put(MALAMQPHelper.SESSION_NAME_HEADER_FIELD_NAME, sessionName);
    props.headers.put(MALAMQPHelper.INTERACTION_TYPE_HEADER_FIELD_NAME, new Integer(interactionType));
    props.headers.put(MALAMQPHelper.INTERACTION_STAGE_HEADER_FIELD_NAME, new Integer(interactionStage));
    Integer tid;
    if (transactionId == null) {
      tid = new Integer(-1);
    } else {
      // TODO: define a full AMQP mapping of the Long transactionId: Integer is not enough
      tid = new Integer(transactionId.intValue());
    }
    props.headers.put(MALAMQPHelper.TRANSACTION_ID_HEADER_FIELD_NAME, tid);
    props.headers.put(MALAMQPHelper.AREA_HEADER_FIELD_NAME, new Integer(areaId));
    props.headers.put(MALAMQPHelper.SERVICE_HEADER_FIELD_NAME, new Integer(serviceId));
    props.headers.put(MALAMQPHelper.VERSION_HEADER_FIELD_NAME, new Integer(version));
    props.headers.put(MALAMQPHelper.OPERATION_HEADER_FIELD_NAME, new Integer(operation));
    props.headers.put(MALAMQPHelper.QOS_LEVEL_HEADER_FIELD_NAME, new Integer(qosLevel));
    props.headers.put(MALAMQPHelper.TIMESTAMP_HEADER_FIELD_NAME, "" + timeStamp);
    int isErrorI;
    if (isError) {
      isErrorI = 1;
    } else {
      isErrorI = 0;
    }
    props.headers.put(MALAMQPHelper.IS_ERROR_HEADER_FIELD_NAME, new Integer(isErrorI));
    props.priority = new Integer(priority);
    long currentTime = System.currentTimeMillis();
    if (ttl != null) {
      props.expiration = String.valueOf(currentTime + ttl.intValue());
    }
    props.timestamp = new Date(timeStamp);

    if (qosLevel == QoSLevel.QUEUED.getOrdinal() ||
      qosLevel == QoSLevel.ASSURED.getOrdinal() ||
      qosLevel == QoSLevel.TIMELY.getOrdinal()) {
        // persistent
        props.deliveryMode = new Integer(2);
      } else {
      // non-persistent
      props.deliveryMode = new Integer(1);
    }

    props.replyTo = replyToQueueName;
    
    MALEncodingContext msgCtx = 
      new MALEncodingContext(msg.getHeader(), messageOperation, 0, endPointProperties, qosProperties);
        
    boolean mandatory = true;
    boolean immediate  = (qosLevel != QoSLevel.QUEUED.getOrdinal());
    // Encode the body
    MALMessageBody body = msg.getBody();
    int bodySize = body.getElementCount();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    MALElementOutputStream meos = transport.getElementStreamFactory().createOutputStream(baos);
    for( int i = 0; i < bodySize; i++) {
      msgCtx.setBodyElementIndex(i);
      meos.writeElement((Element) body.getBodyElement(i, null), msgCtx);
    }
    meos.flush();
    byte[] encodedBody = baos.toByteArray();
    try {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "channel.basicPublish(" +
            exchangeName + ',' + routingKey + ',' + props + ')');
      channel.basicPublish(exchangeName, routingKey,
        mandatory, immediate,
        props, encodedBody);
    } catch (IOException exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw MALAMQPHelper.createMALException(exc.toString());
    }
  }
  
  private void sendRegisterMessage(MALMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPOint.sendRegisterMessage(" +
          msg + ')');
    Map qosProperties = msg.getQoSProperties();
    Subscription subscription = (Subscription) msg.getBody().getBodyElement(0, null);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "subscription=" + subscription);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "subscriptions=" + subscriptions);
    SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptions.get(
        subscription.getSubscriptionId().getValue());
    Subscription formerSubscription = null;
    if (subscriptionCtx == null) {
      Long transactionId = msg.getHeader().getTransactionId();
      int qosLevel = msg.getHeader().getQoSlevel().getOrdinal();
      subscriptionCtx = new SubscriptionContext(transactionId, 
          msg.getHeader().getNetworkZone(),
          msg.getHeader().getQoSlevel(), subscription, this);
      subscriptions.put(subscription.getSubscriptionId().getValue(), subscriptionCtx);
      createSubscriptionQueue(qosLevel, subscriptionCtx);
    } else {
      formerSubscription = subscriptionCtx.getSubscription();
      subscriptionCtx.setSubscription(subscription);
    }
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALAMQPHelper.isTransportLevelBroker(uriTo);
    if (isShared) {
      String exchangeName = MALAMQPHelper.getTopicName(uriTo);
      if (formerSubscription != null) {
        deleteSubscriptionBinding(
            msg.getHeader(),
            exchangeName, 
            subscriptionCtx.getSubscriptionQueueName(), 
            formerSubscription);
      }
      createSubscriptionBinding(msg.getHeader(), exchangeName, subscriptionCtx);
      sendAcknowledge(msg.getHeader(), MALPubSubOperation._REGISTER_ACK_STAGE,
          qosProperties);
    } else {
      String exchangeName = "";
      String routingKey = MALAMQPHelper.getQueueName(uriTo);
      String replyTo = subscriptionCtx.getSubscriptionQueueName();
      defaultSendMessage(msg, exchangeName, routingKey, replyTo);
    }
  }
  
  private void sendDeregisterMessage(MALMessage msg)
  throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.sendDeregisterMessage(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALAMQPHelper.isTransportLevelBroker(uriTo);
    Map qosProperties = msg.getQoSProperties();
    if (isShared) {
      try {
        String exchangeName = MALAMQPHelper.getTopicName(uriTo);
        IdentifierList idList = (IdentifierList) msg.getBody().getBodyElement(0, null);
        for (int i = 0; i < idList.size(); i++) {
          Identifier subId = (Identifier) idList.get(i);
          SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptions.remove(subId.getValue());
          String subscriptionQueueName = subscriptionCtx.getSubscriptionQueueName();
          Subscription subscription = subscriptionCtx.getSubscription();
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "subscription=" + subscription);
          deleteSubscriptionBinding(msg.getHeader(),
              exchangeName, subscriptionCtx.getSubscriptionQueueName(), 
              subscription);
          subscriptionCtx.deactivate();
          // Delete the subscription queue
          channel.queueDelete(subscriptionQueueName);
        }
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "subscriptions=" + subscriptions);
        sendAcknowledge(msg.getHeader(), MALPubSubOperation._DEREGISTER_ACK_STAGE, qosProperties);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALAMQPHelper.createMALException(e.toString());
      }
    } else {
      try {
        IdentifierList idList = (IdentifierList) msg.getBody().getBodyElement(0, null);
        for (int i = 0; i < idList.size(); i++) {
          Identifier subId = (Identifier) idList.get(i);
          SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptions.remove(subId.getValue());
          String subscriptionQueueName = subscriptionCtx
              .getSubscriptionQueueName();
          
          subscriptionCtx.deactivate();
         
          // Delete the subscription queue
          channel.queueDelete(subscriptionQueueName);
        }
        
        String exchangeName = "";
        String routingKey = MALAMQPHelper.getQueueName(uriTo);
        URI uriFrom = msg.getHeader().getURIFrom();
        String replyTo = MALAMQPHelper.getQueueName(uriFrom);
        List<Element> deregisterBody = new ArrayList<Element>();
        deregisterBody.add(idList);
        MALMessage deregisterMsg = new MALAMQPMessage(msg.getHeader(),
            new GENDeregisterBody(deregisterBody), msg.getQoSProperties());
        defaultSendMessage(deregisterMsg, exchangeName, routingKey, replyTo);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALAMQPHelper.createMALException(e.toString());
      }
    }
  }
  
  private void createSubscriptionQueue(int qosLevel, SubscriptionContext ctx) 
  throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.createSubscriptionQueue(" +
          qosLevel + ',' + ctx + ')');
    try {
      boolean exclusiveQueue;
      boolean durable;
      String subscriptionQueueName;
      if (qosLevel == QoSLevel._QUEUED_INDEX) {
        subscriptionQueueName = consumerId + '.' + ctx.getSubscription().getSubscriptionId();
        durable = true;
        exclusiveQueue = false;
      } else {
        subscriptionQueueName = "";
        durable = false;
        exclusiveQueue = true;
      }
      boolean passive = false;
      boolean nowait = false;
      Map args = new HashMap();
      DeclareOk res = channel.queueDeclare(
          subscriptionQueueName, 
          passive, durable, exclusiveQueue, nowait, args);
      subscriptionQueueName = res.getQueue();
      ctx.setSubscriptionQueueName(subscriptionQueueName);
      ctx.activate(consumer.getListener());
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw MALAMQPHelper.createMALException(exc.toString());
    }
  }
  
  private void createSubscriptionBinding(MALMessageHeader header,
      String exchangeName, SubscriptionContext ctx) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPOint.createSubscriptionBinding(" +
          exchangeName + ',' + ctx + ')');
    try {
      String[] routingKeys = MALAMQPHelper.getSubscribeRoutingKeys(header, ctx.getSubscription());
      Map args = new HashMap();
      Vector alreadyBound = new Vector();
      for (int i = 0; i < routingKeys.length; i++) {
        if (alreadyBound.indexOf(routingKeys[i]) > -1) continue;
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "channel.queueBind(" + 
              ctx.getSubscriptionQueueName() + ',' +
              exchangeName + ',' + routingKeys[i] + ')');
        BindOk res = channel.queueBind(ctx.getSubscriptionQueueName(), 
            exchangeName, routingKeys[i], args);
        alreadyBound.addElement(routingKeys[i]);
      }
    } catch (IOException e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }
  
  private void deleteSubscriptionBinding(MALMessageHeader header,
      String exchangeName,
      String subscriptionQueueName,
      Subscription formerSubscription) 
    throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "deleteSubscriptionBinding(" +
          header + ',' + exchangeName + ',' + subscriptionQueueName + ',' + formerSubscription + ')');
    try {
      String[] routingKeys = MALAMQPHelper.getSubscribeRoutingKeys(header, formerSubscription);
      Map args = new HashMap();
      Vector alreadyUnbound = new Vector();
      for (int i = 0; i < routingKeys.length; i++) {
        if (alreadyUnbound.indexOf(routingKeys[i]) > -1) continue;
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "routingKey=" + routingKeys[i]);
        UnbindOk res = channel.queueUnbind(subscriptionQueueName,
            exchangeName, routingKeys[i], args);
        alreadyUnbound.addElement(routingKeys[i]);
      }
    } catch (IOException e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }
  
  private void sendPublishMessage(MALMessage msg)
    throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPOint.sendPublishMessage(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALAMQPHelper.isTransportLevelBroker(uriTo);
    if (isShared) {
      PublishRegisterKey key = new PublishRegisterKey(
        msg.getHeader().getURITo(),
        msg.getHeader().getDomain(),
        msg.getHeader().getNetworkZone(),
        msg.getHeader().getSession(),
        msg.getHeader().getSessionName());
      PublishRegisterContext ctx = (PublishRegisterContext) publishRegisterContexts
        .get(key);
      EntityKeyList unknownEntityKeyList = null;
      MALPublishBody publishBody = (MALPublishBody) msg.getBody();
      UpdateHeaderList updateHeaderList = publishBody.getUpdateHeaderList();
      List[] updateLists = publishBody.getUpdateLists();
      int i = 0;
      while (i < updateHeaderList.size()) {
        UpdateHeader updateHeader = (UpdateHeader) updateHeaderList.get(i);
        if (! ctx.match(updateHeader.getKey())) {
          if (unknownEntityKeyList == null) {
            unknownEntityKeyList = new EntityKeyList();
          }
          unknownEntityKeyList.add(updateHeader.getKey());
          updateHeaderList.remove(i);
          for (int j = 0; j < updateLists.length; j++) {
            updateLists[j].remove(i);
          }
        } else {
          i++;
        }
      }
      if (unknownEntityKeyList != null && unknownEntityKeyList.size() > 0) {
        List<Element> errorElements = new ArrayList<Element>(2);
        errorElements.add(MALHelper.UNKNOWN_ERROR_NUMBER);
        errorElements.add(unknownEntityKeyList);
        sendAcknowledge(msg.getHeader(), MALPubSubOperation._PUBLISH_STAGE, 
          ctx.getQos(), ctx.getPriority(),
          msg.getQoSProperties(), Boolean.TRUE, errorElements);
      }
      String exchangeName = MALAMQPHelper.getTopicName(uriTo);
      for (int j = 0; j < updateHeaderList.size(); j++) {
        UpdateHeader updateHeader = (UpdateHeader) updateHeaderList.get(j);
        String routingKey = MALAMQPHelper.getPublishRoutingKey(msg.getHeader(), updateHeader);
        Map qosProperties = msg.getQoSProperties();
        UpdateHeaderList updateHeaderList2 = new UpdateHeaderList();
        updateHeaderList2.add(updateHeader);
        
        List[] updateLists2 = new List[updateLists.length];
        for (int k = 0; k < updateLists.length; k++) {
          updateLists2[k] = (List) ((Element) updateLists[k]).createElement();
          updateLists2[k].add(updateLists[k].get(j));
        }
        
        List<Element> publishElements = new ArrayList<Element>(publishBody.getElementCount());
        publishElements.add(updateHeaderList2);
        for (int k = 0; k < updateLists.length; k++) {
          publishElements.add((Element) updateLists2[k]);
        }
        
        MALMessage publishMsg = new MALAMQPMessage(msg.getHeader(), 
            new GENPublishBody(publishElements), qosProperties);
        String replyTo = queueName;
        defaultSendMessage(publishMsg, exchangeName, routingKey, replyTo);
      }
    } else {
      String exchangeName = "";
      String routingKey = MALAMQPHelper.getQueueName(uriTo);
      String replyTo = queueName;
      defaultSendMessage(msg, exchangeName, routingKey, replyTo);
    }
  }
  
  private void sendAcknowledge(MALMessageHeader initialHeader, short stage,
      Map qosProperties) {
    sendAcknowledge(initialHeader, stage, null, null, qosProperties, Boolean.FALSE, null);
  }
  
  private void sendAcknowledge(MALMessageHeader initialHeader, short stage,
      Map qosProperties, QoSLevel qos, UInteger priority) {
    sendAcknowledge(initialHeader, stage, qos, priority, qosProperties, Boolean.FALSE, null);
  }
  
  private void sendAcknowledge(MALMessageHeader initialHeader, short stage,
      QoSLevel qos, UInteger priority,
      Map qosProperties, Boolean isError, List<Element> body) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPOint.sendAcknowledge(" +
          initialHeader + ',' + stage + ',' + qosProperties + ')');
    if (qos == null) qos = initialHeader.getQoSlevel();
    if (priority == null) priority = initialHeader.getPriority();
    MALMessageHeader ackHeader = new MALAMQPMessageHeader();
    ackHeader.setServiceArea(initialHeader.getServiceArea());
    ackHeader.setDomain(initialHeader.getDomain());
    ackHeader.setInteractionType(initialHeader.getInteractionType());
    ackHeader.setIsErrorMessage(isError);
    ackHeader.setNetworkZone(initialHeader.getNetworkZone());
    ackHeader.setOperation(initialHeader.getOperation());
    ackHeader.setPriority(priority);
    ackHeader.setQoSlevel(qos);
    ackHeader.setService(initialHeader.getService());
    ackHeader.setSession(initialHeader.getSession());
    ackHeader.setSessionName(initialHeader.getSessionName());
    ackHeader.setAreaVersion(initialHeader.getAreaVersion());
    ackHeader.setInteractionStage(new UOctet(stage));
    ackHeader.setTimestamp(new Time(System.currentTimeMillis()));
    ackHeader.setTransactionId(initialHeader.getTransactionId());
    ackHeader.setURIFrom(initialHeader.getURITo());
    ackHeader.setURITo(initialHeader.getURIFrom());
    ackHeader.setAuthenticationId(getTransport().getAuthenticationId());
    MALAMQPMessage ack = new MALAMQPMessage(ackHeader, 
        MALAMQPHelper.createMessageBody(ackHeader, body), qosProperties);
    consumer.getListener().onMessage(this, ack);
  }
  
  public void setMessageListener(MALMessageListener l) throws MALException {
    if (l == null) {
      if (listener != null) {
        listener = null;
        stopMessageDelivery();
      }
    } else {
      listener = l;
    }
  }
  
  private void activate(MALMessageListener listener) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.activate()");
    try {
      String clientConsumerTag = "";
      boolean nolocal = true;
      boolean exclusive = true;
      consumer = new MessageConsumer(listener, this);
      String consumerTag = channel.basicConsume(queueName, noack, 
          clientConsumerTag, nolocal, exclusive, consumer);
      consumer.setConsumerTag(consumerTag);
      channel.setReturnListener(new InternalErrorListener(listener, this));

      Enumeration enumer = subscriptions.elements();
      while (enumer.hasMoreElements()) {
        SubscriptionContext subctx = (SubscriptionContext) enumer.nextElement();
        subctx.activate(consumer.getListener());
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }
  
  private void deactivate() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.deactivate()");
    try {
      String consumerTag = consumer.getConsumerTag();
      channel.basicCancel(consumerTag);
      channel.setReturnListener(null);
      consumer = null;
      // Cancel the subscription Queue message delivery
      Enumeration enumer = subscriptions.elements();
      while (enumer.hasMoreElements()) {
        SubscriptionContext subctx = (SubscriptionContext) enumer.nextElement();
        //channel.basicCancel(subctx.getConsumerTag());
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "deactivate subscription");
        subctx.deactivate();
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.deactivated.");
  }

  public java.lang.String getLocalName() {
    return localName;
  }

  public void sendMessages(MALMessage[] msgList) throws MALTransmitMultipleErrorException {
    Vector transmitExceptionList = null; 
    for (int i = 0; i < msgList.length; i++) {
      try {
        sendMessage(msgList[i]);
      } catch (MALTransmitErrorException exc) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", exc);
        if (transmitExceptionList == null) {
          transmitExceptionList = new Vector();
        }
        transmitExceptionList.addElement(exc);
      }
    }
    if (transmitExceptionList != null) {
      MALTransmitErrorException[] transmitExceptions = 
        new MALTransmitErrorException[transmitExceptionList.size()];
      transmitExceptionList.copyInto(transmitExceptions);
      throw new MALTransmitMultipleErrorException(transmitExceptions);
    }
  }

  private void sendPublishRegisterMessage(MALMessage msg) throws MALException {
    URI uriTo = msg.getHeader().getURITo();
    if (MALAMQPHelper.isTransportLevelBroker(uriTo)) {
      PublishRegisterKey key = new PublishRegisterKey(
        msg.getHeader().getURITo(),
        msg.getHeader().getDomain(),
        msg.getHeader().getNetworkZone(),
        msg.getHeader().getSession(),
        msg.getHeader().getSessionName());
      PublishRegisterContext ctx = 
        (PublishRegisterContext) publishRegisterContexts.get(key);
      EntityKeyList entityKeyList = (EntityKeyList) msg.getBody().getBodyElement(0, new EntityKeyList());
      if (ctx == null) {
        ctx = new PublishRegisterContext(
          key, entityKeyList,
          msg.getHeader().getQoSlevel(),
          msg.getHeader().getPriority());
        publishRegisterContexts.put(key, ctx);
      } else {
        ctx.setPatterns(entityKeyList);
      }
      QoSLevel ackQos = ctx.getQos();
      UInteger ackPriority = ctx.getPriority();
      sendAcknowledge(msg.getHeader(),
          MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE, msg.getQoSProperties(),
          ackQos, ackPriority);
    } else {
      String exchangeName = "";
      String routingKey = MALAMQPHelper.getQueueName(uriTo);
      String replyTo = queueName;
      defaultSendMessage(msg, exchangeName, routingKey, replyTo);
    }
  }
  
  private void sendPublishDeregisterMessage(MALMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.sendPublishDeregisterMessage(" + msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    if (MALAMQPHelper.isTransportLevelBroker(uriTo)) {
      PublishRegisterKey key = new PublishRegisterKey(
        msg.getHeader().getURITo(),
        msg.getHeader().getDomain(),
        msg.getHeader().getNetworkZone(),
        msg.getHeader().getSession(),
        msg.getHeader().getSessionName());
      PublishRegisterContext ctx = 
          (PublishRegisterContext) publishRegisterContexts.remove(key);
      QoSLevel ackQos;
      UInteger ackPriority;
      if (ctx != null) {
        ackQos = ctx.getQos();
        ackPriority = ctx.getPriority();
      } else {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "PublishRegisterContext not found: " + ctx);
        ackQos = msg.getHeader().getQoSlevel();
        ackPriority = msg.getHeader().getPriority();
      }
      sendAcknowledge(msg.getHeader(),
        MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE, msg.getQoSProperties(),
        ackQos, ackPriority);
    } else {
      String exchangeName = "";
      String routingKey = MALAMQPHelper.getQueueName(uriTo);
      String replyTo = queueName;
      defaultSendMessage(msg, exchangeName, routingKey, replyTo);
    }
  }
  
  static class PublishRegisterContext {
    private PublishRegisterKey key;
    private EntityKeyList patterns;
    private QoSLevel qos;
    private UInteger priority;
    
    public PublishRegisterContext(PublishRegisterKey key,
        EntityKeyList patterns,
        QoSLevel qos, UInteger priority) {
      super();
      this.key = key;
      this.patterns = patterns;
      this.qos = qos;
      this.priority = priority;
    }

    public PublishRegisterKey getKey() {
      return key;
    }

    public EntityKeyList getPatterns() {
      return patterns;
    }
    
    public void setPatterns(EntityKeyList patterns) {
      this.patterns = patterns;
    }

    public QoSLevel getQos() {
      return qos;
    }

    public UInteger getPriority() {
      return priority;
    }

    public boolean match(EntityKey key) {
      for (int i = 0; i < patterns.size(); i++) {
        if (match(patterns.get(i), key)) return true;
      }
      return false;
    }
    
    public static boolean match(EntityKey pattern, EntityKey key) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "PublishRegisterContext.match(" + Strings.toString(pattern) + 
            "," + key + ')');
      if (! match(pattern.getFirstSubKey(), key.getFirstSubKey())) return false;
      if (! match(pattern.getSecondSubKey(), key.getSecondSubKey())) return false;
      if (! match(pattern.getThirdSubKey(), key.getThirdSubKey())) return false;
      if (! match(pattern.getFourthSubKey(), key.getFourthSubKey())) return false;
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "matched");
      return true;
    }
    
    public static boolean match(Identifier patternSubKey, Identifier subKey) {
      if (patternSubKey == null) {
        if (subKey != null) return false;
      } else if (! patternSubKey.getValue().equals("*")) {
        if (! patternSubKey.equals(subKey)) return false;
      }
      return true;
    }
    
    public static boolean match(Long patternSubKey, Long subKey) {
      if (patternSubKey == null) {
        if (subKey != null) return false;
      } else if (patternSubKey.intValue() != 0) {
        if (! patternSubKey.equals(subKey)) return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "PublishRegisterContext [key=" + key + ", patterns=" + patterns
          + ", qos=" + qos + ", priority=" + priority + "]";
    }
  }
  
  static class PublishRegisterKey {
    private URI broker;
    private IdentifierList domain;
    private Identifier networkZone;
    private SessionType session;
    private Identifier sessionName;
    
    public PublishRegisterKey(URI broker, IdentifierList domain,
        Identifier networkZone, SessionType session, Identifier sessionName) {
      super();
      this.broker = broker;
      this.domain = domain;
      this.networkZone = networkZone;
      this.session = session;
      this.sessionName = sessionName;
    }

    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((broker == null) ? 0 : broker.hashCode());
      result = prime * result + ((domain == null) ? 0 : domain.hashCode());
      result = prime * result
          + ((networkZone == null) ? 0 : networkZone.hashCode());
      result = prime * result + ((session == null) ? 0 : session.hashCode());
      result = prime * result
          + ((sessionName == null) ? 0 : sessionName.hashCode());
      return result;
    }

    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      PublishRegisterKey other = (PublishRegisterKey) obj;
      if (broker == null) {
        if (other.broker != null)
          return false;
      } else if (!broker.equals(other.broker))
        return false;
      if (domain == null) {
        if (other.domain != null)
          return false;
      } else if (!domain.equals(other.domain))
        return false;
      if (networkZone == null) {
        if (other.networkZone != null)
          return false;
      } else if (!networkZone.equals(other.networkZone))
        return false;
      if (session == null) {
        if (other.session != null)
          return false;
      } else if (!session.equals(other.session))
        return false;
      if (sessionName == null) {
        if (other.sessionName != null)
          return false;
      } else if (!sessionName.equals(other.sessionName))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "PublishRegisterKey [broker=" + broker + ", domain=" + domain
          + ", networkZone=" + networkZone + ", session=" + session
          + ", sessionName=" + sessionName + "]";
    }
  }

  public void startMessageDelivery() throws MALException {
    activate(listener);
  }
  
  public void stopMessageDelivery() throws MALException {
    deactivate();
  }

  @Override
  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet areaVersion,
      Boolean isErrorMessage, Map qosProperties, Object... body)
      throws MALException {
    MALAMQPMessageHeader header = new MALAMQPMessageHeader(
        getURI(), authenticationId, uRITo, timestamp, qoSlevel, priority, 
        domain, networkZone, session, sessionName, 
        interactionType, interactionStage, transactionId,
        serviceArea, service, operation, areaVersion,
        isErrorMessage);
    List bodyElements = new ArrayList();
    if (body != null) {
      for (Object bodyElement : body) {
        bodyElements.add(bodyElement);
      }
    }
    MALMessageBody messageBody = MALAMQPHelper.createMessageBody(header, bodyElements);
    return new MALAMQPMessage(header, messageBody, qosProperties);
  }
  
  @Override
  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      Object... body) throws MALException {
    return createMessage(authenticationId, uRITo,
      timestamp, qoSlevel, priority,
      domain, networkZone, session,
      sessionName, op.getInteractionType(),
      interactionStage, transactionId, op.getService().getArea().getNumber(),
      op.getService().getNumber(), op.getNumber(), op.getService().getArea().getVersion(),
      isErrorMessage, qosProperties, body);
  }

  @Override
  public MALMessage createMessage(Blob arg0, URI arg1, Time arg2,
      QoSLevel arg3, UInteger arg4, IdentifierList arg5, Identifier arg6,
      SessionType arg7, Identifier arg8, Long arg9, Boolean arg10,
      MALOperation arg11, UOctet arg12, Map arg13, MALEncodedBody arg14)
      throws IllegalArgumentException, MALException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MALMessage createMessage(Blob arg0, URI arg1, Time arg2,
      QoSLevel arg3, UInteger arg4, IdentifierList arg5, Identifier arg6,
      SessionType arg7, Identifier arg8, InteractionType arg9, UOctet arg10,
      Long arg11, UShort arg12, UShort arg13, UShort arg14, UOctet arg15,
      Boolean arg16, Map arg17, MALEncodedBody arg18)
      throws IllegalArgumentException, MALException {
    // TODO Auto-generated method stub
    return null;
  }
}
