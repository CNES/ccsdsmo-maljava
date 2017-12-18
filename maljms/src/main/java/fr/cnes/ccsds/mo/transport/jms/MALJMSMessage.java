package fr.cnes.ccsds.mo.transport.jms;

import java.io.IOException;
import java.util.Map;

import javax.jms.Message;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

public class MALJMSMessage implements MALMessage {
  
  private MALMessageHeader header;
  
  private MALMessageBody body;
  
  private Message jmsMsg;
  
  private MALJMSEndpoint endpoint;
  
  private Map qosProperties;
  
  public MALJMSMessage(MALMessageHeader header, MALMessageBody body, Map qosProperties) {
    this.header = header;
    this.body = body;
    this.qosProperties = qosProperties;
  }
  
  public MALJMSMessage(MALMessageHeader header, MALMessageBody body, Map qosProperties,
     MALJMSEndpoint endpoint, Message jmsMsg) {
    this(header, body, qosProperties);
    this.endpoint = endpoint;
    this.jmsMsg = jmsMsg;
  }

  public MALMessageBody getBody() {
   return body;
  }

  public MALMessageHeader getHeader() {
    return header;
  }
  
  public void setBody(MALMessageBody body) {
    this.body = body;
  }
  
  public String toString() {
    return '(' + super.toString() +
    ",header=" + header +
    ",jmsMsg=" + jmsMsg + 
    ",qosProperties=" + qosProperties + ')';
  }

  public void free() throws MALException {
    if (endpoint != null) {
      try {
        jmsMsg.acknowledge();
      } catch (Exception exc) {
        throw MALJMSHelper.createMALException(exc.toString());
      }
    }
  }

  public Map getQoSProperties() {
    return qosProperties;
  }

  public Message getJmsMsg() {
    return jmsMsg;
  }
  
}
