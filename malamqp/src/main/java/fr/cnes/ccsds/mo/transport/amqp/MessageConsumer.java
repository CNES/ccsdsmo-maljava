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

import java.io.IOException;
import java.util.Hashtable;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class MessageConsumer implements Consumer {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MessageConsumer.class.getName());
  
  private MALMessageListener listener;
  
  private Channel channel;
  
  private MALAMQPEndPoint endpoint;
  
  private String consumerTag;

  public MessageConsumer(MALMessageListener listener,
      MALAMQPEndPoint endpoint) {
    this.listener = listener;
    this.endpoint = endpoint;
    consumerTag = null;
  }

  /**
   * @return the consumerTag
   */
  public String getConsumerTag() {
    return consumerTag;
  }
  
  /**
   * @param consumerTag the consumerTag to set
   */
  public void setConsumerTag(String consumerTag) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageConsumer.setConsumerTag(" + consumerTag + ')');
    this.consumerTag = consumerTag;
  }
  
  public void handleCancelOk(String arg0) {
    // TODO Auto-generated method stub
    
  }

  public void handleConsumeOk(String arg0) {
    // TODO Auto-generated method stub
    
  }

  public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, 
      byte[] encodedBody) throws IOException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageConsumer.handleDelivery(" + consumerTag + ',' +
          envelope + ',' + properties + ')');
    Integer areaNumber = (Integer) properties.headers.get(MALAMQPHelper.AREA_HEADER_FIELD_NAME);
    Integer serviceNumber = (Integer) properties.headers.get(MALAMQPHelper.SERVICE_HEADER_FIELD_NAME);
    Integer serviceVersion = (Integer) properties.headers.get(MALAMQPHelper.VERSION_HEADER_FIELD_NAME);
    Integer operationNumber = (Integer) properties.headers.get(MALAMQPHelper.OPERATION_HEADER_FIELD_NAME);
    
    MALArea area = MALContextFactory.lookupArea(new UShort(areaNumber.intValue()), new UOctet((short) serviceVersion.intValue()));
    if (area == null) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "Unknown area number: " + areaNumber);
      return;
    }
    
    MALService service = area.getServiceByNumber(new UShort(serviceNumber.intValue()));
    if (service == null) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "Unknown service number: " + serviceNumber);
      return;
    }
    
    // Translate the AMQP properties into MAL message header fields
    MALMessageHeader header = MALAMQPHelper.getMALHeader(
        properties, envelope.getExchange(),
        envelope.getRoutingKey(), service);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MAL header = " + header);

    // Deduce the QoS properties
    Hashtable qosProperties = MALAMQPHelper.getMALQoSProperties(properties);

    MALOperation op = service.getOperationByNumber(new UShort(operationNumber.intValue()));
    MALEncodingContext msgCtx = new MALEncodingContext(header, op, 0,
        endpoint.getQoSProperties(), qosProperties);
    
    MALMessageBody body = MALAMQPHelper.createMessageBody(encodedBody,
        msgCtx, endpoint.getTransport().getElementStreamFactory());
    
    MALMessage msg = new MALAMQPMessage(header, body,
        qosProperties, endpoint, envelope.getDeliveryTag());
    listener.onMessage(endpoint, msg);
  }

  public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageConsumer.handleShutdownSignal(" + consumerTag + ',' + sig  + ')');
    listener.onInternalError(endpoint, sig); 
  }
  
  public MALMessageListener getListener() {
    return listener;
  }
}
