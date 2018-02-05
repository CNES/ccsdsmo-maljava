package fr.cnes.ccsds.mo.transport.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

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

public class MALJMSTransport implements MALTransport {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALJMSTransport.class.getName());
  
  //public final static String ENCODING_FORMAT = 
  //  "fr.cnes.malamqp.encoding.format";
  
  public final static String SHARED_BROKER_CREATION = 
    "fr.cnes.malamqp.shared.broker.creation";
  
  public final static String MAL_NAME = "fr.cnes.mal.name";
  
  public static final Blob DEFAULT_AUTHENTICATION_ID = new Blob(new byte[0]);
  
  private MALElementStreamFactory elementStreamFactory;
  
  private Connection connection;
  
  private Session session;
  
  private boolean sharedBrokerCreation;

  public MALJMSTransport(Connection connection) {
    this.connection = connection;
  }
  
  public final Session getSession() {
    return session;
  }
  
  public final MALElementStreamFactory getElementStreamFactory() {
    return elementStreamFactory;
  }
  
  public void init(String protocol, Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.init(" + protocol + ','+ properties + ')');
    //String encodingFormat = (String) properties.get(ENCODING_FORMAT);
    //if (encodingFormat == null) encodingFormat = "joram";
    
    // Need to deactivate the publish encoding optimization
    properties.put("fr.cnes.encoding.binary.encoded.update", Boolean.FALSE);
    elementStreamFactory = MALElementStreamFactory.newFactory(protocol, properties);
    String sharedBrokerCreationS = (String) properties.get(SHARED_BROKER_CREATION);
    sharedBrokerCreation = Boolean.parseBoolean(sharedBrokerCreationS);
    
    String malName = (String) properties.get(MAL_NAME);
    if (malName == null) malName = "MAL";
    
    try {
      connection.setClientID(malName);
      connection.start();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    } catch (JMSException exc) {
      throw new MALException("setClientID", exc);
    }
  }

  public MALEndpoint createEndpoint(String localName, Map qosProperties)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPEndPoint.createEndPoint(" +
          localName + ',' + qosProperties + ')');
    try {
      Session epSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      Session transactedSession = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
      boolean durable = (localName != null);
      boolean exclusive = !durable;
      boolean passive = false;
      boolean nowait = false;
      Map args = new HashMap();
      String queueName = null;
      if (localName != null) {
        queueName = localName;
      }
      
      Queue queue;
      if (queueName == null) {
        queue = epSession.createTemporaryQueue();
        queueName = queue.getQueueName();
      } else {
        queue = epSession.createQueue(queueName);
      }
      
      boolean noack = true;
      String consumerId = null;
      if (qosProperties != null) {
        Boolean noackB = (Boolean) qosProperties.get("noAck");
        if (noackB != null) {
          noack = noackB.booleanValue();
        }
        consumerId = (String) qosProperties.get("consumerId");
      }

      return new MALJMSEndpoint(this, localName, queueName, 
          consumerId, epSession, transactedSession, queue, noack, qosProperties);
    } catch (Exception exc) {
      throw MALJMSHelper.createMALException(exc.toString());
    }
  }

  public void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.close()");
    try {
      session.close();
      connection.close();
    } catch (Exception exc) {
      throw MALJMSHelper.createMALException(exc.toString());
    }
  }

  public void deleteEndpoint(String localName) throws MALException {
    if (localName == null) throw MALJMSHelper.createMALException("null local name");
    try {
      // TODO: not possible in JMS
      //session.queueDelete(localName);
    } catch (Exception exc) {
      throw MALJMSHelper.createMALException(exc.toString());
    }
  }

  public boolean isSupportedInteractionType(InteractionType arg0) {
    return true;
  }

  public boolean isSupportedQoSLevel(QoSLevel arg0) {
    return true;
  }

  public MALBrokerBinding createBroker(String localName, Blob authenticationId,
      QoSLevel[] expectedQos, UInteger priorityLevelNumber, Map qosProperties)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransport.createBroker(" + 
          localName + ')');
    try {
      session.createTopic(localName);
    } catch (Exception exc) {
      throw MALJMSHelper.createMALException(exc.toString());
    }
    return new MALJMSBroker(MALJMSHelper.getTopicUri(localName), 
        DEFAULT_AUTHENTICATION_ID);
  }
  
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

  public MALEndpoint getEndpoint(String localName) {
    // TODO Auto-generated method stub
    return null;
  }

  public MALEndpoint getEndpoint(URI uri) {
    // TODO Auto-generated method stub
    return null;
  }

}
