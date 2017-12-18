package fr.cnes.ccsds.mo.transport.jms;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.objectweb.joram.client.jms.tcp.TcpConnectionFactory;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class MALJMSTransportFactory extends MALTransportFactory {

  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(MALJMSTransportFactory.class.getName());
  
  //public static final String FACTORY_CLASS_PROP = "org.ccsds.moims.mo.maljms.factory.class";
  public static final String USER_NAME_PROP = "org.ccsds.moims.mo.maljms.userName";
  public static final String PASSWORD_PROP = "org.ccsds.moims.mo.maljms.password";
  public static final String VIRTUAL_HOST_PROP = "org.ccsds.moims.mo.maljms.virtualHost";
  public static final String HEART_BEAT_PROP = "org.ccsds.moims.mo.maljms.requestedHeartBeat";
  public static final String SERVER_ADDRESS_PROP = "org.ccsds.moims.mo.maljms.serverAddress";
  public static final String SERVER_PORT_PROP = "org.ccsds.moims.mo.maljms.serverPort";
  public static final String SERVER_URL_PROP = "org.ccsds.moims.mo.maljms.serverUrl";
  
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
  
  public MALJMSTransportFactory(String protocol) {
    super(protocol);
  }
  
  @Override
  public MALTransport createTransport(MALContext malContext, Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPTransportFactory.createTransport(" + properties + ')');
    //String factoryClassName = (String) properties.get(FACTORY_CLASS_PROP);
    String userName = (String) properties.get(USER_NAME_PROP);
    if (userName == null) {
      userName = TcpConnectionFactory.getDefaultLogin();
    }
    
    String password = (String) properties.get(PASSWORD_PROP);
    if (password == null) {
      password = TcpConnectionFactory.getDefaultPassword();
    }
    
    String serverAddress = (String) properties.get(SERVER_ADDRESS_PROP);
    if (serverAddress == null) {
    	serverAddress = TcpConnectionFactory.getDefaultServerHost();
    }
    int serverPort = resolveInteger(properties, SERVER_PORT_PROP);
    if (serverPort < 0) {
    	serverPort = TcpConnectionFactory.getDefaultServerPort();
    }
    
    ConnectionFactory connectionFactory = TcpConnectionFactory.create(serverAddress, serverPort);

    try {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Create connection to: " + serverAddress + ":" + serverPort);
      Connection connection = connectionFactory.createConnection(userName, password);
      MALJMSTransport transport = new MALJMSTransport(connection);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Init");
      transport.init(getProtocol(), properties);
      return transport;
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", e);
      throw MALJMSHelper.createMALException(e.toString());
    }
  }
}
