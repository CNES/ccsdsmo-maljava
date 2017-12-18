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
import java.util.HashMap;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class MALAMQPTransport implements MALTransport {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALAMQPTransport.class.getName());
  
  //public final static String ENCODING_FORMAT = 
  //  "fr.cnes.malamqp.encoding.format";
  
  public final static String SHARED_BROKER_CREATION = 
    "fr.cnes.malamqp.shared.broker.creation";
  
  public static final Blob DEFAULT_AUTHENTICATION_ID = new Blob(new byte[0]);
  
  private MALElementStreamFactory elementStreamFactory;
  
  private Connection connection;
  
  private Channel channel;
  
  private boolean sharedBrokerCreation;

  public MALAMQPTransport(Connection connection,
    Channel channel) {
    this.connection = connection;
    this.channel = channel;
  }
  
  public final Channel getChannel() {
    return channel;
  }
  
  public final MALElementStreamFactory getElementStreamFactory() {
    return elementStreamFactory;
  }
  
  public void init(String protocol, Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.init(" + protocol + ','+ properties + ')');
    //String encodingFormat = (String) properties.get(ENCODING_FORMAT);
    //if (encodingFormat == null) encodingFormat = "joram";
    elementStreamFactory = MALElementStreamFactory.newFactory(protocol, properties);
    String sharedBrokerCreationS = (String) properties.get(SHARED_BROKER_CREATION);
    sharedBrokerCreation = Boolean.parseBoolean(sharedBrokerCreationS);
  }
  
  @Override
  public MALEndpoint createEndpoint(String localName, Map qosProperties)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.createEndPoint(" +
          localName + ',' + qosProperties + ')');
    try {
      Channel epChannel = connection.createChannel();
      boolean durable = (localName != null);
      boolean exclusive = !durable;
      boolean passive = false;
      boolean nowait = false;
      Map args = new HashMap();
      String queueName;
      if (localName != null) {
        queueName = localName;
      } else {
        queueName = "";
      }
      DeclareOk res = epChannel.queueDeclare(queueName, passive, 
          durable, exclusive, nowait, args);
      queueName = res.getQueue();

      boolean noack = true;
      String consumerId = null;
      if (qosProperties != null) {
        Boolean noackB = (Boolean) qosProperties.get("noAck");
        if (noackB != null) {
          noack = noackB.booleanValue();
        }
        consumerId = (String) qosProperties.get("consumerId");
      }

      return new MALAMQPEndPoint(this, localName, queueName, 
          consumerId, epChannel, noack, qosProperties);
    } catch (IOException exc) {
      throw MALAMQPHelper.createMALException(exc.toString());
    }
  }

  public void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.close()");
    try {
      channel.close(AMQP.REPLY_SUCCESS, "Closing.");
      connection.close();
    } catch (IOException exc) {
      throw MALAMQPHelper.createMALException(exc.toString());
    } catch (AlreadyClosedException exc) {
      logger.log(BasicLevel.DEBUG, "Transport already closed");
    }
  }

  public void deleteEndpoint(String localName) throws MALException {
    if (localName == null) throw MALAMQPHelper.createMALException("null local name");
    try {
      channel.queueDelete(localName);
    } catch (IOException exc) {
      throw MALAMQPHelper.createMALException(exc.toString());
    }
  }

  public boolean isSupportedInteractionType(InteractionType arg0) {
    return true;
  }

  public boolean isSupportedQoSLevel(QoSLevel arg0) {
    return true;
  }
  
  @Override
  public MALBrokerBinding createBroker(String localName, Blob authenticationId,
      QoSLevel[] expectedQos, UInteger priorityLevelNumber, Map qosProperties)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.createBroker(" + 
          localName + ')');
    //if (! sharedBrokerCreation) return null;
    try {
      com.rabbitmq.client.AMQP.Exchange.DeclareOk res = 
        channel.exchangeDeclare(localName, "topic", true);
    } catch (IOException exc) {
      throw MALAMQPHelper.createMALException(exc.toString());
    }
    return new MALAMQPBroker(MALAMQPHelper.getTopicUri(localName), 
        DEFAULT_AUTHENTICATION_ID);
  }
  
  @Override
  public MALBrokerBinding createBroker(MALEndpoint endPoint,
      Blob authenticationId, QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, Map qosProperties) throws MALException {
    throw new MALException("Not implemented");
  }
  
  public void deleteBroker(String localName) throws MALException {
    // TODO
  }
  
  public final Blob getAuthenticationId() {
    return DEFAULT_AUTHENTICATION_ID;
  }

  @Override
  public MALEndpoint getEndpoint(String localName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MALEndpoint getEndpoint(URI uri) {
    // TODO Auto-generated method stub
    return null;
  }
}
