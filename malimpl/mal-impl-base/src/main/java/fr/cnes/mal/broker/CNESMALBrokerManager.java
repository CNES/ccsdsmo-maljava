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
package fr.cnes.mal.broker;

import java.util.Map;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.broker.MALBroker;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.broker.MALBrokerHandler;
import org.ccsds.moims.mo.mal.broker.MALBrokerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.BindingManager;
import fr.dyade.aaa.util.management.MXWrapper;

public class CNESMALBrokerManager extends BindingManager<CNESMALBrokerBinding>
    implements MALBrokerManager, CNESMALBrokerManagerMBean {
 
  public final static Logger logger = fr.dyade.aaa.common.Debug
  .getLogger(CNESMALBrokerManager.class.getName());
  
  private Vector<CNESMALBroker> brokers;
  
  public CNESMALBrokerManager(int threadPoolSize, CNESMALContext mal, String name, String jmxName) {
    super(mal, threadPoolSize, name, jmxName);
    brokers = new Vector<CNESMALBroker>();
  }

  private String getBrokerMBeanName(int index) {
    return getJmxName() + ",broker=Broker-" + index;
  }
  
  private String getBrokerBindingMBeanName(int brokerIndex, String uri) {
    String escapedUri = uri.replace(':', '-');
    escapedUri = escapedUri.replace('=', '-');
    return getBrokerMBeanName(brokerIndex) + ",brokerBinding=BrokerBinding-" + escapedUri;
  }
  
  @Override
  protected void doClose() throws MALException {
    Vector<CNESMALBroker> brokersClone = (Vector<CNESMALBroker>) brokers.clone();
    for (int i = 0; i < brokersClone.size(); i++) {
      CNESMALBroker broker = brokersClone.get(i);
      broker.close();
    }
    brokers.clear();
  }

  public MALBroker createBroker() throws MALException {
    return doCreateBroker(null);
  }
  
  public MALBroker createBroker(MALBrokerHandler handler) throws MALException {
    if (handler == null) throw new IllegalArgumentException("Null MALBrokerHandler");
    return doCreateBroker(handler);
  }
  
  private CNESMALBroker doCreateBroker(MALBrokerHandler handler) {
    String mBeanName = getBrokerMBeanName(brokers.size());
    CNESMALBroker broker = new CNESMALBroker(this, handler, mBeanName);
    brokers.add(broker);
    return broker;
  }

  public synchronized MALBrokerBinding createBrokerBinding(MALBroker broker,
      String localName, String protocol, 
      Blob authenticationId, QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, Map qosProperties,
      NamedValueList supplements)
      throws MALException {
    checkClosed();
    if (localName == null) throw new IllegalArgumentException("Null local name");
    if (protocol == null) throw new IllegalArgumentException("Null protocol");
    if (authenticationId == null) throw new IllegalArgumentException("Null authenticationId");
    if (expectedQos == null) throw new IllegalArgumentException("Null expectedQos");
    if (priorityLevelNumber == null) throw new IllegalArgumentException("Null priorityLevelNumber");
    // Currently ignore the supplements for the provider, ping back the supplements from the consumer
    // for the testbed, the broker must be created with an empty list
    if (supplements == null) supplements = new NamedValueList();
    
    MALTransport transport = getMalContext().getTransport(protocol);
    if (broker == null) {
      return transport.createBroker(localName, authenticationId, expectedQos, 
          priorityLevelNumber, qosProperties);
    } else {
      if (broker instanceof CNESMALBroker) {
        if (brokers.indexOf(broker) == -1) throw new MALException("Unknown broker");
        CNESMALBroker cnesBroker = (CNESMALBroker) broker;
        CNESMALBrokerBinding brokerBinding = createBrokerBinding(
          localName,
          cnesBroker,
          transport,
          authenticationId,
          expectedQos, 
          priorityLevelNumber, 
          qosProperties);
        return brokerBinding;
      } else {
        throw CNESMALContext.createException("Incorrect type: " + broker);
      }
    }
  }
  
  private CNESMALBrokerBinding createBrokerBinding(String localName, 
      CNESMALBroker broker,
      MALTransport transport,
      Blob authenticationId,
      QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber, 
      Map defaultQoSProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerManager.createBrokerBinding(" +
          localName + ',' + defaultQoSProperties + ')');
    MALEndpoint ep = transport.createEndpoint(localName, defaultQoSProperties, new NamedValueList());
    CNESMALBrokerBinding brokerBinding = doCreateBrokerBinding(
        broker, ep, authenticationId, expectedQos, priorityLevelNumber, 
        defaultQoSProperties, null);
    ep.setMessageListener(brokerBinding);
    ep.startMessageDelivery();
    return brokerBinding;
  }

  public synchronized MALBrokerBinding createBrokerBinding(MALBroker broker,
      MALEndpoint endPoint, Blob authenticationId, QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, Map qosProperties,
      NamedValueList supplements) throws MALException {
    checkClosed();
    if (supplements == null) supplements = new NamedValueList();
    if (broker == null) {
      MALTransport transport = getMalContext().getTransport(endPoint.getURI());
      return transport.createBroker(endPoint, authenticationId, expectedQos, 
          priorityLevelNumber, qosProperties);
    } else {
      if (broker instanceof CNESMALBroker) {
        CNESMALBroker cnesBroker = (CNESMALBroker) broker;
        MessageDispatcher messageDispatcher = getMalContext().getMessageDispatcher(endPoint);
        CNESMALBrokerBinding brokerBinding = doCreateBrokerBinding(cnesBroker,
          endPoint, authenticationId, expectedQos, priorityLevelNumber, qosProperties, messageDispatcher);
        messageDispatcher.setBroker(brokerBinding);
        return brokerBinding;
      } else {
        throw CNESMALContext.createException("Incorrect type: " + broker);
      }
    }
  }
  
  private CNESMALBrokerBinding doCreateBrokerBinding(
      CNESMALBroker broker,
      MALEndpoint endPoint,
      Blob authenticationId,
      QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber, 
      Map defaultQoSProperties,
      MessageDispatcher messageDispatcher) throws MALException {
    String mBeanName = getBrokerBindingMBeanName(brokers.indexOf(broker), endPoint.getURI().getValue());
    CNESMALBrokerBinding brokerBinding = new CNESMALBrokerBinding(
        this, broker, endPoint, 
        authenticationId,
        defaultQoSProperties,
        mBeanName, messageDispatcher);
    try {
      MXWrapper.registerMBean(brokerBinding, mBeanName);
    } catch (Exception e) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", e);
    }
    broker.addBinding(brokerBinding);
    addBinding(brokerBinding);
    return brokerBinding;
  }
  
  void closeBroker(CNESMALBroker broker) {
    brokers.removeElement(broker);
  }

  public int getBrokerBindingCount() {
    return getBindingCount();
  }
  
  public int getBrokerCount() {
    return brokers.size();
  }
  
  @Override
  protected void finalizeManager() throws MALException {
    getMalContext().closeBrokerManager(this);
  }

}
