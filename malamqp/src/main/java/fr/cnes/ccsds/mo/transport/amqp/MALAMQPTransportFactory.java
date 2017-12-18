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

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;

public class MALAMQPTransportFactory extends MALTransportFactory {

  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALAMQPTransportFactory.class.getName());
  
  public static final String USER_NAME_PROP = "org.ccsds.moims.smc.amqp.userName";
  public static final String PASSWORD_PROP = "org.ccsds.moims.smc.amqp.password";
  public static final String VIRTUAL_HOST_PROP = "org.ccsds.moims.smc.amqp.virtualHost";
  public static final String HEART_BEAT_PROP = "org.ccsds.moims.smc.amqp.requestedHeartBeat";
  public static final String SERVER_ADDRESS_PROP = "org.ccsds.moims.smc.amqp.serverAddress";
  public static final String SERVER_PORT_PROP = "org.ccsds.moims.smc.amqp.serverPort";
  
  private static int resolveInteger(Map properties, String propName) {
    String valueS = (String) properties.get(propName);
    int value;
    if (valueS != null) {
      value = Integer.parseInt(valueS);
    } else {
      value = -1;
    }
    return value;
  }
  
  public MALAMQPTransportFactory(String protocol) {
    super(protocol);
  }
  
  @Override
  public MALTransport createTransport(MALContext malContext, Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransportFactory.createTransport(" + properties + ')');
    String userName = (String) properties.get(USER_NAME_PROP);
    String password = (String) properties.get(PASSWORD_PROP);
    String virtualHost = (String) properties.get(VIRTUAL_HOST_PROP);
    int requestedHeartBeat = resolveInteger(properties, HEART_BEAT_PROP);
    String serverAddress = (String) properties.get(SERVER_ADDRESS_PROP);
    int serverPort = resolveInteger(properties, SERVER_PORT_PROP);
    
    ConnectionParameters params = new ConnectionParameters();
    params.setUsername(userName);
    params.setPassword(password);
    params.setVirtualHost(virtualHost);
    params.setRequestedHeartbeat(requestedHeartBeat);
    ConnectionFactory factory = new ConnectionFactory(params);
    try {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Create connection to: " + serverAddress + ':' + serverPort);
      Connection connection = factory.newConnection(serverAddress, serverPort);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Create channel");
      Channel channel = connection.createChannel();
      MALAMQPTransport transport = new MALAMQPTransport(connection, channel);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Init");
      transport.init(getProtocol(), properties);
      return transport;
    } catch (IOException e) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }
}
