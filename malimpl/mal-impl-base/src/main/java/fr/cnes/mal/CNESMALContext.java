/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 CNES
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
  *******************************************************************************/
package fr.cnes.mal;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.accesscontrol.MALAccessControl;
import org.ccsds.moims.mo.mal.accesscontrol.MALAccessControlFactory;
import org.ccsds.moims.mo.mal.broker.MALBrokerManager;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.broker.CNESMALBrokerManager;
import fr.cnes.mal.consumer.CNESMALConsumerManager;
import fr.cnes.mal.provider.CNESMALProviderManager;
import fr.dyade.aaa.util.management.MXWrapper;

public class CNESMALContext implements MALContext, CNESMALContextMBean {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALContext.class.getName());
  
  public static final int DEFAULT_INTERACTION_INACTIVITY_TIME_OUT = 60000;
  
  public static final String THREAD_POOL_SIZE = "fr.cnes.mal.thread.pool.size";
  
  public static final String TIME_INSTRUMENTED = "fr.cnes.mal.time.instrumented";
  
  public static final String INTERACTION_TIMEOUT = "fr.cnes.mal.interaction.timeout";
  
  public static final String SEQUENCE_COUNT = "sequenceCount";
  
  public static final String NAME = "fr.cnes.mal.name";
  
  public static final String POLL_EXEC = "fr.cnes.mal.poll.exec";
  
  private Vector<CNESMALConsumerManager> consumerManagers;
  
  private Vector<CNESMALProviderManager> providerManagers;
  
  private Vector<CNESMALBrokerManager> brokerManagers;
  
  private volatile boolean closed;
  
  private int defaultThreadPoolSize;
  
  private int interactionTimeout;
  
  private Hashtable<String, MALTransport> transports;
  
  private Map properties;
  
  private MALAccessControl accessControl;
  
  private boolean timeInstrumented;
  
  private String name;
  
  private Hashtable<URI, MessageDispatcher> messageDispatchers;
  
  private Timer timer;
  
  private InteractionWatchDog interactionWatchDog;
  
  private boolean pollExec;
  
  public CNESMALContext() {
    consumerManagers = new Vector<CNESMALConsumerManager>();
    providerManagers = new Vector<CNESMALProviderManager>();
    brokerManagers = new Vector<CNESMALBrokerManager>();
    transports = new Hashtable<String, MALTransport>();
    messageDispatchers = new Hashtable<URI, MessageDispatcher>();
    defaultThreadPoolSize = 2;
    interactionTimeout = DEFAULT_INTERACTION_INACTIVITY_TIME_OUT;
    timer = new Timer();
    interactionWatchDog = new InteractionWatchDog();
  }
  
  public Timer getTimer() {
    return timer;
  }

  public boolean isTimeInstrumented() {
    return timeInstrumented;
  }
  
  public String getName() {
    return name;
  }
  
  public int getInteractionTimeout() {
    return interactionTimeout;
  }

  public boolean isPollExec() {
    return pollExec;
  }

  public void init(Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALContext.init");
    this.properties = properties;
    String defaultThreadPoolSizeS = 
      (String) properties.get(THREAD_POOL_SIZE);
    if (defaultThreadPoolSizeS != null) {
      defaultThreadPoolSize = Integer.parseInt(defaultThreadPoolSizeS);
    }
    
    String timeInstrumentedS = 
      (String) properties.get(TIME_INSTRUMENTED);
    if (timeInstrumentedS != null) {
      timeInstrumented = Boolean.parseBoolean(timeInstrumentedS);
    }
    
    String pollExecS = (String) properties.get(POLL_EXEC);
    if (pollExecS != null) {
      pollExec = Boolean.parseBoolean(pollExecS);
    } else {
      pollExec = true;
    }
    
    String interactionTimeoutS = (String) properties.get(INTERACTION_TIMEOUT);
    if (interactionTimeoutS != null) {
      interactionTimeout = Integer.parseInt(interactionTimeoutS);
    }

    name = (String) properties.get(NAME);
    if (name == null) name = "MAL";
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "default thread pool size: " + defaultThreadPoolSize);
    
    MALAccessControlFactory accessControlFactory = 
      MALAccessControlFactory.newFactory();
    accessControl = accessControlFactory.createAccessControl(properties);
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "securityManager class: " + accessControl.getClass().getName());
    
    if (interactionTimeout > 0) {
      timer.schedule(interactionWatchDog, interactionTimeout, interactionTimeout);
    }
    
    try {
      MXWrapper.registerMBean(this, getMBeanName());
    } catch (Exception exc) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
    }
  }
  
  private String getMBeanName() {
    return "MAL:mal=" + name;
  }
  
  private String getManagerMBeanName(String managerType, int id) {
    return getMBeanName() + ',' + "manager=" + managerType + "Manager#" + id;
  }
  
  public MALTransport getTransport(String protocol) throws IllegalArgumentException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMAL.getTransport(" + protocol + ')');
    checkClosedError();
    if (protocol == null) throw new IllegalArgumentException("Null protocol");
    MALTransport transport;
    synchronized (transports) {
      transport = transports.get(protocol);
      if (transport == null) {
        MALTransportFactory transportFactory = MALTransportFactory.newFactory(protocol);
        transport = transportFactory.createTransport(this, properties);
        transports.put(protocol, transport);
      }
    }
    return transport;
  }
  
  public MALAccessControl getAccessControl() throws MALException {
    return accessControl;
  }

  public synchronized MALConsumerManager createConsumerManager() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALContext.createConsumerManager");
    checkClosedError();
    String jmxName = getManagerMBeanName("Consumer", consumerManagers.size());
    String name = "MALConsumerManager#" + consumerManagers.size();
    CNESMALConsumerManager consumerMgr = 
      new CNESMALConsumerManager(defaultThreadPoolSize, this, name, jmxName);
    try {
      MXWrapper.registerMBean(consumerMgr, jmxName);
    } catch (Exception exc) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
    }
    consumerManagers.addElement(consumerMgr);
    return consumerMgr;
  }
  
  public void closeConsumerManager(CNESMALConsumerManager consumerManager) {
    consumerManagers.removeElement(consumerManager);
  }

  public synchronized MALProviderManager createProviderManager() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALContext.createProviderManager");
    checkClosedError();
    String jmxName = getManagerMBeanName("Provider", providerManagers.size());
    String name = "MALProviderManager#" + providerManagers.size();
    CNESMALProviderManager providerMgr = 
      new CNESMALProviderManager(defaultThreadPoolSize, this, name, jmxName);
    try {
      MXWrapper.registerMBean(providerMgr, jmxName);
    } catch (Exception exc) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
    }
    providerManagers.addElement(providerMgr);
    return providerMgr;
  }
  
  public void closeProviderManager(CNESMALProviderManager providerManager) {
    providerManagers.removeElement(providerManager);
  }
  
  public synchronized MALBrokerManager createBrokerManager() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALContext.createBrokerManager");
    checkClosedError();
    String jmxName = getManagerMBeanName("Broker", brokerManagers.size());
    String name = "MALBrokerManager#" + brokerManagers.size();
    CNESMALBrokerManager brokerMgr = 
      new CNESMALBrokerManager(defaultThreadPoolSize, this, name, jmxName);
    try {
      MXWrapper.registerMBean(brokerMgr, jmxName);
    } catch (Exception exc) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
    }
    brokerManagers.addElement(brokerMgr);
    return brokerMgr;
  }
  
  public void closeBrokerManager(CNESMALBrokerManager brokerManager) {
    brokerManagers.removeElement(brokerManager);
  }

  public synchronized void close() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMAL.close()");
    if (! closed) {
      timer.cancel();
      Vector<CNESMALConsumerManager> consumerManagersClone =
        (Vector<CNESMALConsumerManager>) consumerManagers.clone();
      for (CNESMALConsumerManager mgr : consumerManagersClone) {
        mgr.close();
      }
      consumerManagers.clear();
      
      Vector<CNESMALProviderManager> providerManagersClone =
          (Vector<CNESMALProviderManager>) providerManagers.clone();
      for (CNESMALProviderManager mgr : providerManagersClone) {
        mgr.close();
      }
      providerManagers.clear();
      
      Vector<CNESMALBrokerManager> brokerManagersClone =
          (Vector<CNESMALBrokerManager>) brokerManagers.clone();
      for (CNESMALBrokerManager mgr : brokerManagersClone) {
        mgr.close();
      }
      brokerManagers.clear();
      
      Enumeration elements = transports.elements();
      while (elements.hasMoreElements()) {
        MALTransport transport = (MALTransport) elements.nextElement();
        transport.close();
      }
      
      try {
        MXWrapper.unregisterMBean(getMBeanName());
      } catch (Exception exc) {
        logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
      }
      
      closed = true;
    }
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMAL " + name + " is closed");
  }
  
  private void checkClosedError() throws MALException {
    if (closed) throw createException("Closed");
  }
  
  public static MALException createException(String msg) {
    return new MALException(msg);
  }
  
  public static MALInteractionException createException(UInteger errorCode, String msg) {
    return new MALInteractionException(new MOErrorException(errorCode, new Union(msg)));
  }

  public synchronized MALTransport getTransport(URI uri) throws IllegalArgumentException, MALException {
    checkClosedError();
    if (uri == null) throw new IllegalArgumentException("Null URI");
    StringTokenizer st = new StringTokenizer(uri.getValue(), ":/");
    String protocol = st.nextToken();
    return getTransport(protocol);
  }
  
  public MessageDispatcher getMessageDispatcher(MALEndpoint endpoint) throws MALException {
    synchronized (messageDispatchers) {
      MessageDispatcher messageDispatcher = messageDispatchers.get(endpoint.getURI());
      if (messageDispatcher == null) {
        messageDispatcher = new MessageDispatcher(endpoint);
        messageDispatchers.put(endpoint.getURI(), messageDispatcher);
      }
      return messageDispatcher;
    }
  }
  
  public void removeMessageDispatcher(URI uri) {
    messageDispatchers.remove(uri);
  }
  
  public synchronized void checkInteractionActivity() {
    long currentTime = System.currentTimeMillis();
    for (CNESMALConsumerManager mgr : consumerManagers) {
      mgr.checkInteractionActivity(currentTime, interactionTimeout);
    }
  }
  
  class InteractionWatchDog extends TimerTask {
    @Override
    public void run() {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "InteractionWatchDog.run");
      checkInteractionActivity();
    }
  }
}
