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
package fr.cnes.mal.broker.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.objectweb.util.monolog.api.BasicLevel;

import fr.dyade.aaa.common.Strings;
import fr.dyade.aaa.util.management.MXWrapper;

class SubscriberContext implements Serializable, SubscriberContextMBean {
  
  private SubscriberKey key;
  
  private QoSLevel qosLevel;
  
  private Map qosProperties;
  
  private UInteger priority;
  
  private String jmxName;
  
  private UShort area;
  
  private UShort service;
  
  private UShort operation;
  
  private UOctet version;
  
  private ArrayList<SubscriptionContext> subscriptions;
  
  public SubscriberContext(SubscriberKey key,
      QoSLevel qosLevel, Map qosProperties,
      UInteger priority, String jmxName, UShort area, UShort service,
      UShort operation, UOctet version) throws Exception {
    super();
    this.key = key;
    this.qosLevel = qosLevel;
    this.qosProperties = qosProperties;
    this.priority = priority;
    this.jmxName = jmxName;
    this.area = area;
    this.service = service;
    this.operation = operation;
    this.version = version;
    subscriptions = new ArrayList<SubscriptionContext>();
  }
  
  public void initJmx(String jmxName) {
    this.jmxName = jmxName;
    for (SubscriptionContext subscription : subscriptions) {
      String mBeanName = getMBeanName(subscription.getSubscriptionId());
      subscription.initJmx(mBeanName);
      try {
        MXWrapper.registerMBean(subscription, mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  public void unregisterMBeans() {
    for (SubscriptionContext subscription : subscriptions) {
      String mBeanName = getMBeanName(subscription.getSubscriptionId());
      subscription.unregisterMBeans();
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  public SubscriberKey getKey() {
    return key;
  }
  
  public QoSLevel getQoSLevel() {
    return qosLevel;
  }
  
  public Map getQosProperties() {
    return qosProperties;
  }
  
  public UInteger getPriority() {
    return priority;
  }
  
  public UShort getArea() {
    return area;
  }

  public UShort getService() {
    return service;
  }

  public UShort getOperation() {
    return operation;
  }

  public synchronized void addSubscription(SubscriptionContext newSubscription) {
    String mBeanName = getMBeanName(newSubscription.getSubscriptionId());
    newSubscription.setJmxName(mBeanName);
    
    for (int i = 0; i < subscriptions.size(); i++) {
      SubscriptionContext subscription = 
        (SubscriptionContext) subscriptions.get(i);
      if (subscription.getSubscriptionId().equals(
          newSubscription.getSubscriptionId())) {
        subscriptions.remove(i);
        try {
          MXWrapper.unregisterMBean(mBeanName);
        } catch (Exception exc) {
          Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
        }
        break;
      }
    }
    subscriptions.add(newSubscription);
    try {
      MXWrapper.registerMBean(newSubscription, mBeanName);
    } catch (Exception e) {
      Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
    }
  }
  
  public synchronized SubscriptionContext removeSubscription(Identifier subscriptionId) {
    for (int i = 0; i < subscriptions.size(); i++) {
      SubscriptionContext subscription = 
        (SubscriptionContext) subscriptions.get(i);
      if (subscription.getSubscriptionId().equals(subscriptionId)) {
        subscriptions.remove(i);
        subscription.unregisterMBeans();
        try {
          MXWrapper.unregisterMBean(getMBeanName(subscriptionId));
        } catch (Exception exc) {
          Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", exc);
        }
        return subscription;
      }
    }
    return null;
  }
    
  boolean match(IdentifierList publicationDomain, 
      Identifier publicationNetwork, 
      SessionType publicationSessionType, 
      Identifier publicationSessionName) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, toString() + ".match(domain=" + 
          Strings.toString(publicationDomain) + 
          ",network=" + publicationNetwork + 
          ",sessionType=" + publicationSessionType +
          ",sessionName=" + publicationSessionName + ')');
    // The subscription domain can't be longer than the
    // publication domain
    IdentifierList domain = key.getDomainKey().getDomain();
    if (domain.size() > publicationDomain.size()) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Subscription domain longer");
      return false;
    }
    for (int i = 0; i < domain.size(); i++) {
      if (! domain.get(i).equals(publicationDomain.get(i))) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Subscription domain is different");
        return false;
      }
    }
    SessionType sessionType = key.getDomainKey().getSessionType();
    // The network zone is ignored
    //if (! networkZone.equals(publicationNetwork)) return false;
    if (sessionType.getOrdinal() != publicationSessionType.getOrdinal()) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Different session type");
      return false;
    }
    Identifier sessionName = key.getDomainKey().getSessionName();
    if (! sessionName.equals(publicationSessionName)) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Different session name");
      return false;
    }
    return true; 
  }
  
  private String getMBeanName(Identifier subscriptionId) {
    return jmxName + ",subscription=Subscription-" + subscriptionId.getValue();
  }
  
  public SubscriptionContext getSubscription(Identifier subscriptionId) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, toString() + 
          ".getSubscription(" + subscriptionId + ')');
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "-> subscriptions=" + subscriptions);
    for (int i = 0; i < subscriptions.size(); i++) {
      SubscriptionContext subscription = 
        (SubscriptionContext) subscriptions.get(i);
      if (subscription.getSubscriptionId().equals(subscriptionId)) {
        return subscription;
      }
    }
    return null;
  }
  
  public List<SubscriptionContext> getSubscriptions() {
    return subscriptions;
  }
  
  public int getSubscriptionNumber() {
    return subscriptions.size();
  }
  
  public void checkPublisher(PublisherContext publisherContext) {
    for (int i = 0; i < subscriptions.size(); i++) {
      SubscriptionContext subscriptionContext = subscriptions.get(i);
      subscriptionContext.checkPublisher(publisherContext);
    }
  }
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    key = (SubscriberKey) is.readObject();
    qosLevel = QoSLevel.fromOrdinal(is.readInt());
    qosProperties = (Map) is.readObject();
    priority = new UInteger(is.readLong());
    area = new UShort(is.readInt());
    service = new UShort(is.readInt());
    operation = new UShort(is.readInt());
    version = new UOctet(is.readShort());
    subscriptions = (ArrayList<SubscriptionContext>) is.readObject();
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeObject(key);
    os.writeInt(qosLevel.getOrdinal());
    os.writeObject(qosProperties);
    os.writeLong(priority.getValue());
    os.writeInt(area.getValue());
    os.writeInt(service.getValue());
    os.writeInt(operation.getValue());
    os.writeShort(version.getValue());
    os.writeObject(subscriptions);
  }

  @Override
  public String toString() {
    return "SubscriberContext [key=" + key + ", qosLevel=" + qosLevel
        + ", qosProperties=" + qosProperties + ", priority=" + priority
        + ", jmxName=" + jmxName + ", area=" + area + ", service="
        + service + ", operation=" + operation + ", version=" + version
        + ", subscriptions=" + subscriptions + "]";
  }

  public String getURI() {
    return key.getSubscriberUri().getValue();
  }

  public String getAreaName() {
    return MALContextFactory.lookupArea(area, version).getName().getValue();
  }

  public String getServiceName() {
    return MALContextFactory.lookupArea(area, version)
        .getServiceByNumber(service).getName().getValue();
  }

  public String getOperationName() {
    return MALContextFactory.lookupArea(area, version)
        .getServiceByNumber(service)
        .getOperationByNumber(operation).getName().getValue();
  }

  public String getDomain() {
    return key.getDomainKey().getDomain().toString();
  }

  public String getNetworkZone() {
    return key.getDomainKey().getNetworkZone().getValue();
  }

  public String getSessionType() {
    return key.getDomainKey().getSessionType().toString();
  }

  public String getSessionName() {
    return key.getDomainKey().getSessionName().getValue();
  }

  public String getQoSLevelAsString() {
    return qosLevel.toString();
  }

  public long getPriorityAsLong() {
    return priority.getValue();
  }
}