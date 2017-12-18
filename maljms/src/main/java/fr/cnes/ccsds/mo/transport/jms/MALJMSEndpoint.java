package fr.cnes.ccsds.mo.transport.jms;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

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

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.dyade.aaa.common.Strings;

public class MALJMSEndpoint implements MALEndpoint {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALJMSEndpoint.class.getName());
  
  private MALJMSTransport transport;
  private String localName;
  private String queueName;
  private String consumerId;
  private Session session;
  private Session transactedSession;
  private Hashtable subscriptions;
  private MALJMSMessageConsumer consumer;
  private Hashtable publishRegisterContexts;
  private Map endPointProperties;
  private MALMessageListener listener;
  
  private HashMap<URI, MessageProducer> producerCache;
  
  private HashMap<URI, Topic> topicCache;
  
  private Queue endpointQueue;
  
  private MessageConsumer jmsConsumer;
  
  public MALJMSEndpoint(MALJMSTransport transport,
    String localName,
    String queueName,
    String consumerId,
    Session session,
    Session transactedSession,
    Queue endpointQueue,
    boolean noack,
    Map endPointProperties) throws Exception {
    this.transport = transport;
    this.localName =  localName;
    this.queueName = queueName;
    this.consumerId = consumerId;
    this.session = session;
    this.transactedSession = transactedSession;
    this.endpointQueue = endpointQueue;
    this.endPointProperties = endPointProperties;
    subscriptions = new Hashtable();
    consumer = null;
    //malEncoder = new MALByteArrayEncoder(transport.getElementStreamFactory());
    publishRegisterContexts = new Hashtable();
    producerCache = new HashMap<URI, MessageProducer>();
    topicCache = new HashMap<URI, Topic>();
  }
  
  final Session getSession() {
    return session;
  }
  
  public Queue getEndpointQueue() {
    return endpointQueue;
  }

  final MALJMSTransport getTransport() {
    return transport;
  }
  
  final Map getQoSProperties() {
    return endPointProperties;
  }

  public void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.close()");
    setMessageListener(null);
    try {
      if (localName == null) {
        // TODO: not possible in JMS
        // channel.queueDelete(queueName);
      }
      session.close();
      transactedSession.close();
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALJMSHelper.createMALException(e.toString());
    }
  }

  public URI getURI() {
    return MALJMSHelper.getQueueUri(queueName);
  }

  public void sendMessage(MALMessage msg) throws MALTransmitErrorException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendMessage(" +
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
        URI uriTo = msg.getHeader().getURITo();
        MessageProducer pTo = getProducer(uriTo);
        defaultSendMessage(msg, pTo);
      }
    } else {
      URI uriTo = msg.getHeader().getURITo();
      MessageProducer pTo = getProducer(uriTo);
        defaultSendMessage(msg, pTo);
      }
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
      throw new MALTransmitErrorException(msg.getHeader(), 
          new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, new Union(exc.toString())), 
          msg.getQoSProperties());
    }
  }
  
  private MessageProducer getProducer(URI uriTo) throws Exception {
    MessageProducer p = producerCache.get(uriTo);
    if (p == null) {
      String destName;
      Destination dest;
      if (MALJMSHelper.isTransportLevelBroker(uriTo)) {
        destName = MALJMSHelper.getTopicName(uriTo);
        //dest = transactedSession.createTopic(destName);
        dest = session.createTopic(destName);
      } else {
        destName = MALJMSHelper.getQueueName(uriTo);
        //dest = transactedSession.createQueue(destName);
        dest = session.createQueue(destName);
      }
      //p = transactedSession.createProducer(dest);
      p = session.createProducer(dest);
      producerCache.put(uriTo, p);
    }
    return p;
  }
  
  private Topic getTopic(URI uriTo) throws Exception {
    Topic t = topicCache.get(uriTo);
    if (t == null) {
      String routingKey = MALJMSHelper.getTopicName(uriTo);
      t = session.createTopic(routingKey);
      topicCache.put(uriTo, t);
    }
    return t;
  }
  
  public Message mapToJms(MALMessage msg) throws MALException {
    Map qosProperties = msg.getQoSProperties();

    MALArea messageArea = MALContextFactory.lookupArea(msg.getHeader().getServiceArea(), msg.getHeader().getAreaVersion());
    if (messageArea == null) {
      throw MALJMSHelper.createMALException("Unknown area: " + msg.getHeader().getServiceArea() + " version: " + msg.getHeader().getAreaVersion());
    }
    
    MALService messageService = messageArea.getServiceByNumber(msg.getHeader().getService());
    if (messageService == null) {
      throw MALJMSHelper.createMALException("Unknown service: " + msg.getHeader().getService());
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
    int operation = msg.getHeader().getOperation().getValue();
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
    
    try {
      //BytesMessage jmsMsg = transactedSession.createBytesMessage();
      BytesMessage jmsMsg = session.createBytesMessage();

      if (authenticationId != null && authenticationId.length != 0) {
        jmsMsg.setStringProperty(
            MALJMSHelper.AUTHENTICATION_ID_HEADER_FIELD_NAME, new String(
                authenticationId));
      }
      if (domainId != null) {
        jmsMsg.setStringProperty(MALJMSHelper.DOMAIN_HEADER_FIELD_NAME,
            MALJMSHelper.domainToString(domainId));
      }
      if (networkZone != null) {
        jmsMsg.setStringProperty(MALJMSHelper.NETWORK_ZONE_HEADER_FIELD_NAME,
            networkZone);
      }
      
      jmsMsg.setStringProperty(MALJMSHelper.FROM_HEADER_FIELD_NAME,
          queueName);
      
      jmsMsg.setIntProperty(MALJMSHelper.SESSION_HEADER_FIELD_NAME,
          new Integer(sessionType));
      jmsMsg.setStringProperty(MALJMSHelper.SESSION_NAME_HEADER_FIELD_NAME,
          sessionName);
      jmsMsg.setIntProperty(MALJMSHelper.INTERACTION_TYPE_HEADER_FIELD_NAME,
          new Integer(interactionType));
      jmsMsg.setIntProperty(MALJMSHelper.INTERACTION_STAGE_HEADER_FIELD_NAME,
          new Integer(interactionStage));
      jmsMsg
          .setLongProperty(MALJMSHelper.TRANSACTION_ID_HEADER_FIELD_NAME, transactionId);
      jmsMsg.setIntProperty(MALJMSHelper.AREA_HEADER_FIELD_NAME, new Integer(
          areaId));
      jmsMsg.setIntProperty(MALJMSHelper.SERVICE_HEADER_FIELD_NAME,
          new Integer(serviceId));
      jmsMsg.setIntProperty(MALJMSHelper.VERSION_HEADER_FIELD_NAME,
          new Integer(version));
      jmsMsg.setIntProperty(MALJMSHelper.OPERATION_HEADER_FIELD_NAME,
          new Integer(operation));
      jmsMsg.setIntProperty(MALJMSHelper.QOS_LEVEL_HEADER_FIELD_NAME,
          new Integer(qosLevel));
      jmsMsg.setLongProperty(MALJMSHelper.TIMESTAMP_HEADER_FIELD_NAME,
          timeStamp);
      int isErrorI;
      if (isError) {
        isErrorI = 1;
      } else {
        isErrorI = 0;
      }
      jmsMsg.setIntProperty(MALJMSHelper.IS_ERROR_HEADER_FIELD_NAME,
          new Integer(isErrorI));

      jmsMsg.setJMSPriority(new Integer(priority));

      long currentTime = System.currentTimeMillis();
      if (ttl != null) {
        jmsMsg.setJMSExpiration(currentTime + ttl.intValue());
      }

      if (qosLevel == QoSLevel._BESTEFFORT_INDEX) {
         // non-persistent
         jmsMsg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
      } else {
        // persistent
        jmsMsg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
      }

      MALEncodingContext msgCtx = new MALEncodingContext(msg.getHeader(),
          messageOperation, 0, endPointProperties, qosProperties);

      boolean mandatory = true;
      boolean immediate = (qosLevel != QoSLevel.QUEUED.getOrdinal());
      // Encode the body
      MALMessageBody body = msg.getBody();
      int bodySize = body.getElementCount();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MALElementOutputStream meos = transport.getElementStreamFactory()
          .createOutputStream(baos);
      for (int i = 0; i < bodySize; i++) {
        msgCtx.setBodyElementIndex(i);
        meos.writeElement((Element) body.getBodyElement(i, null), msgCtx);
      }
      meos.flush();
      byte[] encodedBody = baos.toByteArray();

      jmsMsg.writeInt(encodedBody.length);
      jmsMsg.writeBytes(encodedBody);
      
      return jmsMsg;
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw MALJMSHelper.createMALException(exc.toString());
    }
  }
  
  public void defaultSendMessage(MALMessage msg, MessageProducer mp) 
    throws MALException {
    defaultSendMessage(msg, mp, endpointQueue);
  }

  public void defaultSendMessage(MALMessage msg, MessageProducer mp, Queue replyToQ) 
    throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.defaultSendMessage(" +
          msg + ')');
    try {
      Message jmsMsg = mapToJms(msg);
      jmsMsg.setJMSReplyTo(replyToQ);
      
      if (jmsMsg.getJMSDeliveryMode() == DeliveryMode.NON_PERSISTENT) {
        mp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      } else {
        mp.setDeliveryMode(DeliveryMode.PERSISTENT);
      }
      
      mp.send(jmsMsg);
      
      //transactedSession.commit();
      
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw MALJMSHelper.createMALException(exc.toString());
    }
  }
  
  private void sendRegisterMessage(MALMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendRegisterMessage(" +
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
    } else {
      formerSubscription = subscriptionCtx.getSubscription();
      subscriptionCtx.setSubscription(subscription);
    }
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALJMSHelper.isTransportLevelBroker(uriTo);
    if (isShared) {
      try {
        Topic topic = getTopic(uriTo);
        createSubscription(msg.getHeader(), topic, subscriptionCtx);
        sendAcknowledge(msg.getHeader(),
            MALPubSubOperation._REGISTER_ACK_STAGE, qosProperties);
      } catch (Exception exc) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", exc);
        throw MALJMSHelper.createMALException(exc.toString());
      }
    } else {
      // TODO: JMS mapping uses a single queue
      // Queue replyToQ = subscriptionCtx.getSubscriptionQueue();
      
      try {
        defaultSendMessage(msg, getProducer(uriTo));
      } catch (Exception exc) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", exc);
        throw MALJMSHelper.createMALException(exc.toString());
      }
    }
  }
  
  private void sendDeregisterMessage(MALMessage msg)
  throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendDeregisterMessage(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALJMSHelper.isTransportLevelBroker(uriTo);
    Map qosProperties = msg.getQoSProperties();
    if (isShared) {
      try {
        String exchangeName = MALJMSHelper.getTopicName(uriTo);
        IdentifierList idList = (IdentifierList) msg.getBody().getBodyElement(0, null);
        for (int i = 0; i < idList.size(); i++) {
          Identifier subId = (Identifier) idList.get(i);
          SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptions.remove(subId.getValue());
          String subscriptionName = subscriptionCtx.getSubscriptionName();
          Subscription subscription = subscriptionCtx.getSubscription();
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "subscription=" + subscription);
          subscriptionCtx.deactivate();
          subscriptionCtx.delete();
        }
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "subscriptions=" + subscriptions);
        sendAcknowledge(msg.getHeader(), MALPubSubOperation._DEREGISTER_ACK_STAGE, qosProperties);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
    } else {
      try {
        IdentifierList idList = (IdentifierList) msg.getBody().getBodyElement(0, null);
        for (int i = 0; i < idList.size(); i++) {
          Identifier subId = (Identifier) idList.get(i);
          SubscriptionContext subscriptionCtx = (SubscriptionContext) subscriptions.remove(subId.getValue());
          subscriptionCtx.deactivate();
          subscriptionCtx.delete();
        }
        
        MessageProducer mp = getProducer(uriTo);
        List<Element> deregisterBody = new ArrayList<Element>();
        deregisterBody.add(idList);
        MALMessage deregisterMsg = new MALJMSMessage(msg.getHeader(),
            new GENDeregisterBody(deregisterBody), msg.getQoSProperties());
        defaultSendMessage(deregisterMsg, mp);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
    }
  }
  /*
  private void createSubscriptionQueue(int qosLevel, SubscriptionContext ctx) 
  throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.createSubscriptionQueue(" +
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
      
      Queue subscriptionQueue = session.createQueue(subscriptionQueueName);
      ctx.setSubscriptionName(subscriptionQueueName);
      ctx.setSubscriptionQueue(subscriptionQueue);
      ctx.activate(consumer.getListener());
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw MALAMQPHelper.createMALException(exc.toString());
    }
  }
  */
  private void createSubscription(MALMessageHeader header,
      Topic topic, SubscriptionContext ctx) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.createSubscription(" +
          topic + ',' + ctx + ')');
    try {
      String selector = MALJMSHelper.getMessageSelector(header, ctx.getSubscription());
      MessageConsumer jmsConsumer;
      if (header.getQoSlevel().getOrdinal() == QoSLevel._QUEUED_INDEX) {
        String subscriptionName = consumerId + '.'
            + ctx.getSubscription().getSubscriptionId();
        ctx.setSubscriptionName(subscriptionName);
        jmsConsumer = session.createDurableSubscriber(topic,
            subscriptionName, selector, false);
      } else {
        jmsConsumer = session.createConsumer(topic, selector);
      }
      ctx.setJmsConsumer(jmsConsumer);
      ctx.activate(consumer.getListener());
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALJMSHelper.createMALException(e.toString());
    }
  }
  
  private void sendPublishMessage(MALMessage msg)
    throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendPublishMessage(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    boolean isShared = MALJMSHelper.isTransportLevelBroker(uriTo);
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
      
      MessageProducer jmsProducer;
      try {
        jmsProducer = getProducer(uriTo);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
      
      try {
        for (int j = 0; j < updateHeaderList.size(); j++) {
          UpdateHeader updateHeader = (UpdateHeader) updateHeaderList.get(j);

          //if (logger.isLoggable(BasicLevel.ERROR))
          //  logger.log(BasicLevel.ERROR, "jmsMsg=" + jmsMsg);
          
          Map qosProperties = msg.getQoSProperties();
          UpdateHeaderList updateHeaderList2 = new UpdateHeaderList();
          updateHeaderList2.add(updateHeader);

          List[] updateLists2 = new List[updateLists.length];
          for (int k = 0; k < updateLists.length; k++) {
            updateLists2[k] = (List) ((Element) updateLists[k]).createElement();
            updateLists2[k].add(updateLists[k].get(j));
          }

          List<Element> publishElements = new ArrayList<Element>(
              publishBody.getElementCount());
          publishElements.add(updateHeaderList2);
          for (int k = 0; k < updateLists.length; k++) {
            publishElements.add((Element) updateLists2[k]);
          }

          MALMessage publishMsg = new MALJMSMessage(msg.getHeader(),
              new GENPublishBody(publishElements), qosProperties);
          
          Message jmsMsg = mapToJms(publishMsg);

          MALJMSHelper.setPublishJmsProperties(msg.getHeader(), updateHeader,
              jmsMsg);
          jmsMsg.setStringProperty(MALJMSHelper.TOPIC_URI, uriTo.getValue());

          try {
            jmsMsg.setJMSReplyTo(endpointQueue);
            
            if (jmsMsg.getJMSDeliveryMode() == DeliveryMode.NON_PERSISTENT) {
              jmsProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            } else {
              jmsProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            }
            
            jmsProducer.send(jmsMsg);
          } catch (Exception exc) {
            if (logger.isLoggable(BasicLevel.ERROR))
              logger.log(BasicLevel.ERROR, "", exc);
            throw MALJMSHelper.createMALException(exc.toString());
          }
        }
        
        //transactedSession.commit();
        
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
    } else {
      try {
        MessageProducer mp = getProducer(uriTo);
        defaultSendMessage(msg, mp);
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
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
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendAcknowledge(" +
          initialHeader + ',' + stage + ',' + qosProperties + ')');
    if (qos == null) qos = initialHeader.getQoSlevel();
    if (priority == null) priority = initialHeader.getPriority();
    MALMessageHeader ackHeader = new MALJMSMessageHeader();
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
    MALJMSMessage ack = new MALJMSMessage(ackHeader, 
        MALJMSHelper.createMessageBody(ackHeader, body), qosProperties);
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
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.activate()");
    try {
      String clientConsumerTag = "";
      boolean nolocal = true;
      boolean exclusive = true;
      consumer = new MALJMSMessageConsumer(listener, this);
      
      jmsConsumer = session.createConsumer(endpointQueue);
      jmsConsumer.setMessageListener(consumer);

      Enumeration enumer = subscriptions.elements();
      while (enumer.hasMoreElements()) {
        SubscriptionContext subctx = (SubscriptionContext) enumer.nextElement();
        subctx.activate(consumer.getListener());
      }
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALJMSHelper.createMALException(e.toString());
    }
  }
  
  private void deactivate() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.deactivate()");
    try {
      jmsConsumer.setMessageListener(null);
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
      throw MALJMSHelper.createMALException(e.toString());
    }
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.deactivated.");
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
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendPublishRegisterMessage(" + msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    if (MALJMSHelper.isTransportLevelBroker(uriTo)) {
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
      try {
        defaultSendMessage(msg, getProducer(uriTo));
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
    }
  }
  
  private void sendPublishDeregisterMessage(MALMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALJMSEndpoint.sendPublishDeregisterMessage(" + msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    if (MALJMSHelper.isTransportLevelBroker(uriTo)) {
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
      try {
        defaultSendMessage(msg, getProducer(uriTo));
      } catch (Exception e) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "", e);
        throw MALJMSHelper.createMALException(e.toString());
      }
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
  
  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet areaVersion,
      Boolean isErrorMessage, Map qosProperties, Object... body)
      throws MALException {
    MALJMSMessageHeader header = new MALJMSMessageHeader(
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
    MALMessageBody messageBody = MALJMSHelper.createMessageBody(header, bodyElements);
    return new MALJMSMessage(header, messageBody, qosProperties);
  }
  
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

  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      MALEncodedBody encodedBody) throws IllegalArgumentException, MALException { 
    throw new MALException("Not yet implemented");
  }

  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet areaVersion,
      Boolean isErrorMessage, Map qosProperties, MALEncodedBody encodedBody)
      throws IllegalArgumentException, MALException {
    throw new MALException("Not yet implemented");
  }
  
}
