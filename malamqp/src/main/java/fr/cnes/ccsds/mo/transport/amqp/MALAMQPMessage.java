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
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

public class MALAMQPMessage implements MALMessage {
  
  private MALMessageHeader header;
  
  private MALMessageBody body;
  
  private long deliveryTag;
  
  private MALAMQPEndPoint endpoint;
  
  private Map qosProperties;
  
  public MALAMQPMessage(MALMessageHeader header, MALMessageBody body, Map qosProperties) {
    this.header = header;
    this.body = body;
    this.qosProperties = qosProperties;
  }
  
  public MALAMQPMessage(MALMessageHeader header, MALMessageBody body, Map qosProperties,
     MALAMQPEndPoint endpoint, long deliveryTag) {
    this(header, body, qosProperties);
    this.endpoint = endpoint;
    this.deliveryTag = deliveryTag;
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
    ",deliveryTag=" + deliveryTag + 
    ",qosProperties=" + qosProperties + ')';
  }

  public void free() throws MALException {
    if (endpoint != null  && endpoint.getNoAck() == false) {
      try {
        endpoint.getChannel().basicAck(deliveryTag, false);
      } catch (IOException exc) {
        throw MALAMQPHelper.createMALException(exc.toString());
      }
    }
  }

  public Map getQoSProperties() {
    return qosProperties;
  }

  public long getDeliveryTag() {
    return deliveryTag;
  }
}
