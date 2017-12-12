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
package fr.cnes.mal.consumer;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.BindingManager;
import fr.dyade.aaa.util.management.MXWrapper;

public class CNESMALConsumerManager extends BindingManager<CNESMALConsumer> implements MALConsumerManager,
    CNESMALConsumerManagerMBean {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CNESMALConsumerManager.class.getName());

  public CNESMALConsumerManager(int threadPoolSize, CNESMALContext mal, String name, String jmxName) {
    super(mal, threadPoolSize, name, jmxName);
  }

  public synchronized MALConsumer createConsumer(String localName, URI uriTo,
      URI brokerUri, MALService service, Blob authenticationId,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel qosLevel, Map qosProps, UInteger priority)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALConsumerManager.createConsumer("
          + localName + ',' + uriTo + ',' + brokerUri + ',' + service + ',' + domain + ','
          + networkZone + ',' + sessionType + ',' + sessionName + ','
          + qosLevel + ',' + qosProps + ',' + priority + ')');
    checkClosed();
    if (service == null) throw new IllegalArgumentException("Null service");
    if (authenticationId == null) throw new IllegalArgumentException("Null authenticationId");
    if (domain == null) throw new IllegalArgumentException("Null domain");
    if (networkZone == null) throw new IllegalArgumentException("Null networkZone");
    if (sessionType == null) throw new IllegalArgumentException("Null sessionType");
    if (sessionName == null) throw new IllegalArgumentException("Null sessionName");
    if (qosLevel == null) throw new IllegalArgumentException("Null qosLevel");
    if (priority == null) throw new IllegalArgumentException("Null priority");
    if (uriTo == null && brokerUri == null) throw new IllegalArgumentException("Null provider and broker URIs");
    
    String proto;
    if (uriTo != null) {
      proto = getProtocol(uriTo.getValue());
    } else {
      proto = getProtocol(brokerUri.getValue());
    }
    MALTransport transport = getMalContext().getTransport(proto);
    checkTransport(transport, qosLevel, service);
    
    MALEndpoint endPoint = transport.createEndpoint(localName, qosProps);
    CNESMALConsumer consumer =  doCreateConsumer(endPoint, uriTo, brokerUri, service, authenticationId,
        domain, networkZone, sessionType, sessionName,
        qosLevel, qosProps, priority, null);
    endPoint.setMessageListener(consumer);
    endPoint.startMessageDelivery();
    return consumer;
  }
  
  public synchronized MALConsumer createConsumer(MALEndpoint endPoint, URI uriTo,
      URI brokerUri, MALService service, Blob authenticationId,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel qosLevel, Map qosProps,
      UInteger priority) throws MALException {
    if (service == null) throw new IllegalArgumentException("Null service");
    if (authenticationId == null) throw new IllegalArgumentException("Null authenticationId");
    if (domain == null) throw new IllegalArgumentException("Null domain");
    if (networkZone == null) throw new IllegalArgumentException("Null networkZone");
    if (sessionType == null) throw new IllegalArgumentException("Null sessionType");
    if (sessionName == null) throw new IllegalArgumentException("Null sessionName");
    if (qosLevel == null) throw new IllegalArgumentException("Null qosLevel");
    if (priority == null) throw new IllegalArgumentException("Null priority");
    if (uriTo == null && brokerUri == null) throw new IllegalArgumentException("Null provider and broker URIs");
    MessageDispatcher messageDispatcher = getMalContext().getMessageDispatcher(endPoint);
    CNESMALConsumer consumer = doCreateConsumer(
        endPoint, uriTo, brokerUri, service, authenticationId,
        domain, networkZone, sessionType, sessionName,
        qosLevel, qosProps, priority, messageDispatcher);
    messageDispatcher.addConsumer(consumer);
    return consumer;
  }
  
  private String getMBeanName(String uri, MALService service) {
    String escapedUri = uri.replace(':', '-');
    escapedUri = escapedUri.replace('=', '-');
    StringBuffer buf = new StringBuffer();
    buf.append(getJmxName());
    buf.append(",service=");
    buf.append(service.getArea().getName());
    buf.append("-v");
    buf.append(service.getArea().getVersion());
    buf.append("-");
    buf.append(service.getName());
    buf.append(",consumer=Consumer-");
    buf.append(escapedUri);
    return buf.toString();
  }
  
  public CNESMALConsumer doCreateConsumer(MALEndpoint endPoint,
      URI uriTo, URI brokerUri, MALService service, Blob authenticationId,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel qosLevel, Map qosProps, UInteger priority,
      MessageDispatcher messageDispatcher) throws MALException {
    String jmxName = getMBeanName(endPoint.getURI().getValue(), service);
    CNESMALConsumer consumer = new CNESMALConsumer(uriTo, brokerUri, service,
        authenticationId, domain, networkZone, sessionType, sessionName,
        qosLevel, qosProps, priority, this, endPoint, messageDispatcher, jmxName);
    try {
      MXWrapper.registerMBean(consumer, jmxName);
    } catch (Exception exc) {
      logger.log(BasicLevel.WARN, getClass().getName() + " jmx failed", exc);
    }
    addBinding(consumer);
    return consumer;
  }

  public int getConsumerCount() {
    return getBindingCount();
  }
  
  public synchronized void checkInteractionActivity(long currentTime, int timeout) {
    for (int i = 0; i < getBindingCount(); i++) {
      CNESMALConsumer consumer = getBinding(i);
      consumer.checkInteractionActivity(currentTime, timeout);
    }
  }
  
  @Override
  protected void finalizeManager() throws MALException {
    getMalContext().closeConsumerManager(this);
  }
  
  @Override
  protected void doClose() throws MALException {
    // Nothing to do
  }

}
