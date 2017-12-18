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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENNotifyBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENRegisterBody;

public class SubscriptionConsumer implements Consumer {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(SubscriptionConsumer.class.getName());
  
  private SubscriptionContext subscriptionCtx;
  private MALMessageListener listener;
  private MALAMQPEndPoint endpoint;

  SubscriptionConsumer(MALMessageListener listener,
    SubscriptionContext subscriptionCtx,
    MALAMQPEndPoint endpoint) {
    this.listener = listener;
    this.subscriptionCtx = subscriptionCtx;
    this.endpoint = endpoint;
  }
  
  public void handleDelivery(java.lang.String consumerTag, Envelope envelope,
      BasicProperties properties, byte[] encodedBody) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleDelivery(" + 
          consumerTag + ',' +
          envelope.getExchange() + ',' + 
          envelope.getRoutingKey() + ',' + 
          properties + ')');
    Integer areaNumber = (Integer) properties.headers.get(MALAMQPHelper.AREA_HEADER_FIELD_NAME);
    Integer serviceNumber = (Integer) properties.headers.get(MALAMQPHelper.SERVICE_HEADER_FIELD_NAME);
    Integer version = (Integer) properties.headers.get(MALAMQPHelper.VERSION_HEADER_FIELD_NAME);
    Integer operationNumber = (Integer) properties.headers.get(MALAMQPHelper.OPERATION_HEADER_FIELD_NAME);
    
    MALArea area = MALContextFactory.lookupArea(new UShort(areaNumber.intValue()), new UOctet((short) version.intValue()));
    if (area == null) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "Unknown area number: " + areaNumber);
      return;
    }
    
    MALService service = area.getServiceByNumber(
        new UShort(serviceNumber.intValue()));
    if (service == null) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "Unknown service number: " + serviceNumber);
      return;
    }
    
    // Translate the AMQP properties into MAL message header fields
    MALMessageHeader header = MALAMQPHelper.getMALHeader(properties, 
        envelope.getExchange(), envelope.getRoutingKey(), service);
    // The network zone is the one from the publisher
    //header.setNetworkZone(subscriptionCtx.getNetworkZone());
    header.setQoSlevel(subscriptionCtx.getQos());
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MAL header = " + header);
    
    // Deduce the QoS properties
    Hashtable qosProperties = MALAMQPHelper.getMALQoSProperties(properties);
    
    MALOperation op = service.getOperationByNumber(new UShort(operationNumber.intValue()));
    MALEncodingContext msgCtx = new MALEncodingContext(header, op, 0,
        endpoint.getQoSProperties(), qosProperties);
    
    InteractionType interactionType = header.getInteractionType();
    if (interactionType.getOrdinal() != InteractionType._PUBSUB_INDEX) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Unexpected interaction type: " + interactionType);
      return;
    }
    
    try {
      short stage = header.getInteractionStage().getValue();
      if (stage == MALPubSubOperation._PUBLISH_STAGE) {
        GENPublishBody publishBody = new GENPublishBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALAMQPMessage msg = new MALAMQPMessage(header, publishBody,
            qosProperties, endpoint, envelope.getDeliveryTag());
        handlePublish(msg);
      } else if (stage == MALPubSubOperation._NOTIFY_STAGE) {
        GENNotifyBody notifyBody = new GENNotifyBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALAMQPMessage msg = new MALAMQPMessage(header, notifyBody,
            qosProperties, endpoint, envelope.getDeliveryTag());
        handleNotify(msg);
      } else if (stage == MALPubSubOperation._REGISTER_ACK_STAGE) {
        GENRegisterBody registerBody = new GENRegisterBody(encodedBody,
            msgCtx, endpoint.getTransport().getElementStreamFactory());
        MALAMQPMessage msg = new MALAMQPMessage(header, registerBody,
            qosProperties, endpoint, envelope.getDeliveryTag());
        handleRegisterAck(msg);
      } else if (stage == MALPubSubOperation._DEREGISTER_ACK_STAGE) {
       GENDeregisterBody deregisterBody = new GENDeregisterBody(
            encodedBody, msgCtx, endpoint.getTransport()
                .getElementStreamFactory());
        MALAMQPMessage msg = new MALAMQPMessage(header, deregisterBody,
            qosProperties, endpoint, envelope.getDeliveryTag());
        handleDeregisterAck(msg);
      } else {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Unexpected interaction stage: " + stage);
      }
    } catch (MALException exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
    }
  }
  
  private void handlePublish(MALAMQPMessage msg) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handlePublish(" +
          msg + ')');
    URI uriTo = msg.getHeader().getURITo();
    if (MALAMQPHelper.isTransportLevelBroker(uriTo)) {
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
      
      MALAMQPMessage notifyMsg = new MALAMQPMessage(msg.getHeader(), notifyBody, 
          msg.getQoSProperties(), endpoint, msg.getDeliveryTag());
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
  
  public void handleShutdownSignal(String arg0, ShutdownSignalException arg1) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionConsumer.handleShutdownSignal(" + arg0 + 
          ',' + arg1 + ')');
  }

}
