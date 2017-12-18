package fr.cnes.ccsds.mo.transport.jms;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENNotifyBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENRegisterBody;

public class SubscriptionConsumer implements MessageListener {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(SubscriptionConsumer.class.getName());
  
  private SubscriptionContext subscriptionCtx;
  private MALMessageListener listener;
  private MALJMSEndpoint endpoint;

  SubscriptionConsumer(MALMessageListener listener,
    SubscriptionContext subscriptionCtx,
    MALJMSEndpoint endpoint) {
    this.listener = listener;
    this.subscriptionCtx = subscriptionCtx;
    this.endpoint = endpoint;
  }
  
  private void handlePublish(MALJMSMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handlePublish(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    if (MALJMSHelper.isTransportLevelBroker(uriTo)) {
      MALPublishBody publishBody = (MALPublishBody) msg.getBody();
      UpdateHeaderList updateHeaderList = publishBody.getUpdateHeaderList();
      List[] updateLists = publishBody.getUpdateLists();
      
      List notifyBodyElements = new ArrayList();
      notifyBodyElements.add(subscriptionCtx.getSubscription().getSubscriptionId());
      notifyBodyElements.add(updateHeaderList);
      for (List updateList : updateLists) {
        notifyBodyElements.add(updateList);
      }

      MALNotifyBody notifyBody = new GENNotifyBody(notifyBodyElements);
      
      MALJMSMessage notifyMsg = new MALJMSMessage(msg.getHeader(), notifyBody, 
          msg.getQoSProperties(), endpoint, msg.getJmsMsg());
      msg.getHeader().setInteractionStage(MALPubSubOperation.NOTIFY_STAGE);
      msg.getHeader().setQoSlevel(subscriptionCtx.getQos());
      msg.getHeader().setTransactionId(subscriptionCtx.getTransactionId());
      msg.getHeader().setURIFrom(msg.getHeader().getURITo());
      msg.getHeader().setAuthenticationId(endpoint.getTransport().getAuthenticationId());
      handleMessageFromBroker(notifyMsg);
    } else {
      handleMessageFromBroker(msg);
    }
  }
  
  private void handleMessageFromBroker(MALMessage msg) {
    // Change the 'URIto' assigned with the subscription queue URI
    // and assign it with the endpoint URI
    msg.getHeader().setURITo(endpoint.getURI());
    listener.onMessage(endpoint, msg);
  }
  
  private void handleNotify(MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleNotify(" +
          msg + ')');
    handleMessageFromBroker(msg);
  }
  
  private void handleRegisterAck(MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleRegisterAck(" +
          msg + ')');
    handleMessageFromBroker(msg);
  }
  
  private void handleDeregisterAck(MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleDeregisterAck("
          + msg + ')');
    handleMessageFromBroker(msg);
    
    // This code fails with RabbitMQ
    /*
    new Thread() {
      public void run() {
        try {
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "-> create channel");
          Channel channel = endpoint.getConnection().createChannel();
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "-> cancel: "
                + subscriptionCtx.getConsumerTag());
          subscriptionCtx.deactivate();
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "-> delete: "
                + subscriptionCtx.getSubscriptionQueueName());
          channel.queueDelete(subscriptionCtx.getSubscriptionQueueName());
          channel.close();
        } catch (Exception exc) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "", exc);
        }
      }
    }.start();
    */
  }
  
  public void handleCancelOk(String arg0) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleCancelOk(" + arg0 + ')');
  }

  
  public void handleConsumeOk(String arg0) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleConsumeOk(" + arg0 + ')');
  }

  public void onMessage(Message msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.onMessage(" + msg + ')');
    try {
      Integer areaNumber = msg
          .getIntProperty(MALJMSHelper.AREA_HEADER_FIELD_NAME);
      Integer serviceNumber = msg
          .getIntProperty(MALJMSHelper.SERVICE_HEADER_FIELD_NAME);
      Integer version = msg
          .getIntProperty(MALJMSHelper.VERSION_HEADER_FIELD_NAME);
      Integer operationNumber = msg
          .getIntProperty(MALJMSHelper.OPERATION_HEADER_FIELD_NAME);

      MALArea area = MALContextFactory.lookupArea(
          new UShort(areaNumber.intValue()),
          new UOctet((short) version.intValue()));
      if (area == null) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "Unknown area number: " + areaNumber);
        return;
      }

      MALService service = area.getServiceByNumber(new UShort(serviceNumber
          .intValue()));
      if (service == null) {
        if (logger.isLoggable(BasicLevel.ERROR))
          logger.log(BasicLevel.ERROR, "Unknown service number: "
              + serviceNumber);
        return;
      }

      // Translate the AMQP properties into MAL message header fields
      MALMessageHeader header = MALJMSHelper.getMALHeader(msg, service, endpoint.getURI());
      // The network zone is the one from the publisher
      // header.setNetworkZone(subscriptionCtx.getNetworkZone());
      header.setQoSlevel(subscriptionCtx.getQos());
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "MAL header = " + header);
    
      // Deduce the QoS properties
      Hashtable qosProperties = MALJMSHelper.getMALQoSProperties(msg);

      MALOperation op = service.getOperationByNumber(new UShort(operationNumber
          .intValue()));
      MALEncodingContext msgCtx = new MALEncodingContext(header, op, 0,
          endpoint.getQoSProperties(), qosProperties);

      InteractionType interactionType = header.getInteractionType();
      if (interactionType.getOrdinal() != InteractionType._PUBSUB_INDEX) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Unexpected interaction type: "
              + interactionType);
        return;
      }

      BytesMessage bytesMsg = (BytesMessage) msg;
      int bodyLength = bytesMsg.readInt();
      byte[] encodedBody = new byte[bodyLength];
      bytesMsg.readBytes(encodedBody);
    
      short stage = header.getInteractionStage().getValue();
      if (stage == MALPubSubOperation._PUBLISH_STAGE) {
        GENPublishBody publishBody = new GENPublishBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALJMSMessage malMsg = new MALJMSMessage(header, publishBody,
            qosProperties, endpoint, msg);
        handlePublish(malMsg);
      } else if (stage == MALPubSubOperation._NOTIFY_STAGE) {
        GENNotifyBody notifyBody = new GENNotifyBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALJMSMessage malMsg = new MALJMSMessage(header, notifyBody,
            qosProperties, endpoint, msg);
        handleNotify(malMsg);
      } else if (stage == MALPubSubOperation._REGISTER_ACK_STAGE) {
        GENRegisterBody registerBody = new GENRegisterBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALJMSMessage malMsg = new MALJMSMessage(header, registerBody,
            qosProperties, endpoint, msg);
        handleRegisterAck(malMsg);
      } else if (stage == MALPubSubOperation._DEREGISTER_ACK_STAGE) {
        GENDeregisterBody deregisterBody = new GENDeregisterBody(
            encodedBody, msgCtx, endpoint.getTransport()
                .getElementStreamFactory());
        MALJMSMessage malMsg = new MALJMSMessage(header, deregisterBody,
            qosProperties, endpoint, msg);
        handleDeregisterAck(malMsg);
      } else {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Unexpected interaction stage: " + stage);
      }
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
    }
  }

}
