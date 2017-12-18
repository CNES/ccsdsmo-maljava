package fr.cnes.ccsds.mo.transport.jms;

import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

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

public class MALJMSMessageConsumer implements MessageListener {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MALJMSMessageConsumer.class.getName());

  private MALMessageListener listener;

  private MALJMSEndpoint endpoint;

  private String consumerTag;

  public MALJMSMessageConsumer(MALMessageListener listener,
      MALJMSEndpoint endpoint) {
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
   * @param consumerTag
   *          the consumerTag to set
   */
  public void setConsumerTag(String consumerTag) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageConsumer.setConsumerTag("
          + consumerTag + ')');
    this.consumerTag = consumerTag;
  }

  // TODO: no equivalent in JMS
  /*
   * public void handleShutdownSignal(String consumerTag,
   * ShutdownSignalException sig) { if (logger.isLoggable(BasicLevel.DEBUG))
   * logger.log(BasicLevel.DEBUG, "MessageConsumer.handleShutdownSignal(" +
   * consumerTag + ',' + sig + ')'); listener.onInternalError(endpoint, sig); }
   */

  public MALMessageListener getListener() {
    return listener;
  }

  public void onMessage(Message msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageConsumer.onMessage()");
    try {
      Integer areaNumber = msg
          .getIntProperty(MALJMSHelper.AREA_HEADER_FIELD_NAME);
      Integer serviceNumber = msg
          .getIntProperty(MALJMSHelper.SERVICE_HEADER_FIELD_NAME);
      Integer serviceVersion = msg
          .getIntProperty(MALJMSHelper.VERSION_HEADER_FIELD_NAME);
      Integer operationNumber = msg
          .getIntProperty(MALJMSHelper.OPERATION_HEADER_FIELD_NAME);

      MALArea area = MALContextFactory.lookupArea(
          new UShort(areaNumber.intValue()),
          new UOctet((short) serviceVersion.intValue()));
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
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "MAL header = " + header);

      // Deduce the QoS properties
      Hashtable qosProperties = MALJMSHelper.getMALQoSProperties(msg);

      MALOperation op = service.getOperationByNumber(new UShort(operationNumber
          .intValue()));
      MALEncodingContext msgCtx = new MALEncodingContext(header, op, 0,
          endpoint.getQoSProperties(), qosProperties);

      BytesMessage bytesMsg = (BytesMessage) msg;
      int bodyLength = bytesMsg.readInt();
      byte[] encodedBody = new byte[bodyLength];
      bytesMsg.readBytes(encodedBody);

      MALMessageBody body = MALJMSHelper.createMessageBody(encodedBody,
          msgCtx, endpoint.getTransport().getElementStreamFactory());

      MALMessage malMsg = new MALJMSMessage(header, body, qosProperties,
          endpoint, msg);
      listener.onMessage(endpoint, malMsg);

    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
    }
  }

}
