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

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.NullableAttribute;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilter;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilterList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.objectweb.util.monolog.api.BasicLevel;

import fr.dyade.aaa.util.management.MXWrapper;

public class PublisherContext implements Serializable, PublisherContextMBean {
  
  public static final Identifier ALL_ID = new Identifier("*");
  
  public static final Integer ALL_INT = new Integer(0);
  
  private PublisherKey key;
  private Long transactionId;
  private UOctet version;
//  private List<EntityPublishContext> entityPublishContextList;
  // TODO SL : seems that this variable is never really used
  private List<SubscriptionContext> subscriptionContexts;
  private IdentifierList subKeys;
  private AttributeTypeList keyTypes;

  private String jmxName;
  
  private void readObject(java.io.ObjectInputStream is) throws IOException,
      ClassNotFoundException {
    key = (PublisherKey) is.readObject();
    transactionId = new Long(is.readLong());
    version = new UOctet(is.readShort());
    int subscriptionContextsSize = is.readInt();
    subscriptionContexts = new ArrayList<SubscriptionContext>(subscriptionContextsSize);
    for (int i = 0; i < subscriptionContextsSize; i++) {
      SubscriptionContext subscriptionContext = (SubscriptionContext) is.readObject();
      subscriptionContexts.add(subscriptionContext);
    }
  }

  private void writeObject(java.io.ObjectOutputStream os) throws IOException {
    os.writeObject(key);
    os.writeLong(transactionId);
    os.writeShort(version.getValue());
    os.writeInt(subscriptionContexts.size());
    for (int i = 0; i < subscriptionContexts.size(); i++) {
      SubscriptionContext subscriptionContext = subscriptionContexts.get(i);
      os.writeObject(subscriptionContext);
    }
  }

  public PublisherContext(PublisherKey key, Long transactionId,
      UOctet version, IdentifierList subKeys, AttributeTypeList keyTypes, String jmxName) {
    super();
    this.key = key;
    this.transactionId = transactionId;
    this.version = version;
    this.subKeys = subKeys;
    this.keyTypes = keyTypes;
    this.jmxName = jmxName;
    subscriptionContexts = new ArrayList<SubscriptionContext>();
  }
  
  public void reregister(IdentifierList subKeys, AttributeTypeList keyTypes) {
    this.subKeys = subKeys;
    this.keyTypes = keyTypes;
  }
  
  public void initJmx(String jmxName) {
    this.jmxName = jmxName;
    for (int i = 0; i < subscriptionContexts.size(); i++) {
      SubscriptionContext subscriptionContext = subscriptionContexts.get(i);
      String mBeanName = getMBeanName(subscriptionContext);
      try {
        MXWrapper.registerMBean(subscriptionContext, mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  public void unregisterMBeans() {
    for (int i = 0; i < subscriptionContexts.size(); i++) {
      SubscriptionContext subscriptionContext = subscriptionContexts.get(i);
      String mBeanName = getMBeanName(subscriptionContext);
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  private String getMBeanName(SubscriptionContext subscriptionContext) {
    String subscriber = subscriptionContext.getSubscriberContext().getURI();
    String[] subParts = subscriber.split("/");
    subscriber = subParts[subParts.length-1];
    return jmxName +
        ",subscriber=" + subscriber +
        ",subscription=" + subscriptionContext.getSubscriptionId().getValue();
  }
  
  public void addSubscription(SubscriptionContext subscriptionContext) {
    String mBeanName = getMBeanName(subscriptionContext);
    if (! subscriptionContexts.contains(subscriptionContext)) {
      // the function is also called on resubscription
      subscriptionContexts.add(subscriptionContext);
      try {
        MXWrapper.registerMBean(subscriptionContext, mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  /*
  private void insertPatternsWithoutWildcard(EntityKeyList patterns) {
    for (int i = 0; i < patterns.size();) {
      EntityKey entityKey = patterns.get(i);
      if (!ALL_ID.equals(entityKey.getFirstSubKey())
          && !ALL_INT.equals(entityKey.getSecondSubKey())
          && !ALL_INT.equals(entityKey.getThirdSubKey())
          && !ALL_INT.equals(entityKey.getFourthSubKey())) {
        addPublish(new EntityPublishContext(this, entityKey));
        patterns.remove(i);
      } else {
        i++;
      }
    }
  }
  
  private void insertPatternsWithWildcardAtFourth(EntityKeyList patterns) {
    for (int i = 0; i < patterns.size();) {
      EntityKey entityKey = patterns.get(i);
      if (ALL_INT.equals(entityKey.getFourthSubKey())
          && !ALL_ID.equals(entityKey.getFirstSubKey())
          && !ALL_INT.equals(entityKey.getSecondSubKey())
          && !ALL_INT.equals(entityKey.getThirdSubKey())) {
        addPublish(new EntityPublishContext(this, entityKey));
        patterns.remove(i);
      } else {
        i++;
      }
    }
  }
  
  private void insertPatternsWithWildcardAtThird(EntityKeyList patterns) {
    for (int i = 0; i < patterns.size();) {
      EntityKey entityKey = patterns.get(i);
      if (ALL_INT.equals(entityKey.getThirdSubKey())
          && !ALL_ID.equals(entityKey.getFirstSubKey())
          && !ALL_INT.equals(entityKey.getSecondSubKey())) {
        addPublish(new EntityPublishContext(this, entityKey));
        patterns.remove(i);
      } else {
        i++;
      }
    }
  }
  
  private void insertPatternsWithWildcardAtSecond(EntityKeyList patterns) {
    for (int i = 0; i < patterns.size();) {
      EntityKey entityKey = patterns.get(i);
      if (ALL_INT.equals(entityKey.getSecondSubKey())
          && !ALL_ID.equals(entityKey.getFirstSubKey())) {
        addPublish(new EntityPublishContext(this, entityKey));
        patterns.remove(i);
      } else {
        i++;
      }
    }
  }
  
  private void insertPatternsWithWildcardAtFirst(EntityKeyList patterns) {
    for (int i = 0; i < patterns.size();) {
      EntityKey entityKey = patterns.get(i);
      if (ALL_ID.equals(entityKey.getFirstSubKey())) {
        addPublish(new EntityPublishContext(this, entityKey));
        patterns.remove(i);
      } else {
        i++;
      }
    }
  }
  
  public void setPatterns(EntityKeyList patterns) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "PublisherContext.setPatterns(" + patterns + ')');
    if (entityPublishContextList != null) {
      unregisterMBeans();
    }
    this.entityPublishContextList = new ArrayList<EntityPublishContext>(patterns.size());
    patterns = (EntityKeyList) patterns.clone();
    insertPatternsWithoutWildcard(patterns);
    insertPatternsWithWildcardAtFourth(patterns);
    insertPatternsWithWildcardAtThird(patterns);
    insertPatternsWithWildcardAtSecond(patterns);
    insertPatternsWithWildcardAtFirst(patterns);
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "-> entityPublishContextList = " + entityPublishContextList);
  }
  **/

  public PublisherKey getKey() {
    return key;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public void checkUpdates(UpdateCheckReport report,
      UpdateHeader updateHeader, Object... updateObjects) {
    NullableAttributeList keyValues = updateHeader.getKeyValues();
    // TODO SL new PublishRegister to define in v2
    // check the keyValues match the subKeys
    int expectedKeyNumber = (subKeys == null ? 0 : subKeys.size());
    int keyNumber = (keyValues == null ? 0 : keyValues.size());
    if (keyNumber != expectedKeyNumber) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Key numbers do not match.");
      report.addFailedUpdateHeader(updateHeader);
      report.addFailedUpdate(updateObjects);
      return;
    }
    // check the key value types
    for (int i = 0; i < keyNumber; i++) {
      Attribute key = keyValues.get(i).getValue();
      
      if (key!= null && key.getTypeShortForm().intValue() != keyTypes.get(i).getNumericValue().getValue()) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Key(" + i + ") type does not match: " +
              key.getTypeShortForm().intValue() + " / " + keyTypes.get(i).getNumericValue().getValue());
        report.addFailedUpdateHeader(updateHeader);
        report.addFailedUpdate(updateObjects);
        return;
      }
    }
    
    // TODO SL a simplifier
    report.addPublisherContext(this);
    report.addUpdateHeaderToNotify(updateHeader);
    report.addUpdateToNotify(updateObjects);
  }
  
  public void checkSubscription(SubscriptionContext subscriptionContext) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "PublisherContext.checkSubscription(" + subscriptionContext + ')');
    SubscriberContext subscriberContext = subscriptionContext.getSubscriberContext();
    DomainKey domainKey = key.getDomainKey();
    // check the operation
    if (! subscriberContext.matchArea(domainKey.getArea())
        || ! subscriberContext.matchService(domainKey.getService())
        || ! subscriberContext.matchOperation(domainKey.getOperation())) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Operation does not match.");
      return;
    }
    subscriptionContext.updatePublisher(this);
    // TODO SL register the subscription
    // as a subscriptionContext? or as an EntityRequestContext?
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "-> subscriptionContexts=" + subscriptionContexts);
  }
  
  public void removeSubscriptionContext(SubscriptionContext subscriptionContext) {
    subscriptionContexts.remove(subscriptionContext);
    String mBeanName = getMBeanName(subscriptionContext);
    try {
      MXWrapper.unregisterMBean(mBeanName);
    } catch (Exception e) {
      Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
    }
  }
  
  public void removeSubscriberContext(SubscriberContext subscriberContext) {
    List<SubscriptionContext> subscriptionContextList = subscriberContext.getSubscriptions();
    for (SubscriptionContext subscriptionContext : subscriptionContextList) {
      removeSubscriptionContext(subscriptionContext);
    }
  }

  @Override
  public String toString() {
    return "PublisherContext [key=" + key + ", transactionId=" + transactionId
        + ", subscriptionContexts=" + subscriptionContexts
        + "subKeys=" + subKeys + "keyTypes=" + keyTypes
        + "]";
  }

  public String getURI() {
    return key.getUri().getValue();
  }

  public String getDomain() {
    return key.getDomainKey().getDomain().toString();
  }
  
  public String getAreaName() {
    UShort area = key.getDomainKey().getArea();
    return MALContextFactory.lookupArea(area, version).getName().getValue();
  }
  
  public String getServiceName() {
    UShort areaNumber = key.getDomainKey().getArea();
    UShort serviceNumber = key.getDomainKey().getService();
    MALArea area = MALContextFactory.lookupArea(areaNumber, version);
    return area.getServiceByNumber(serviceNumber).getName().getValue();
  }
  
  public String getOperationName() {
    UShort areaNumber = key.getDomainKey().getArea();
    UShort serviceNumber = key.getDomainKey().getService();
    MALArea area = MALContextFactory.lookupArea(areaNumber, version);
    MALService service = area.getServiceByNumber(serviceNumber);
    UShort operationNumber = key.getDomainKey().getOperation();
    return service.getOperationByNumber(operationNumber).getName().getValue();
  }

  public List<SubscriptionContext> getSubscriptionContexts() {
    return subscriptionContexts;
  }
  
  public IdentifierList getSubKeys() {
    return subKeys;
  }
}