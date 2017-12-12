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
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
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
  private QoSLevel qos;
  private UInteger priority;
  private UOctet version;
  private List<EntityPublishContext> entityPublishContextList;

  private String jmxName;
  
  private void readObject(java.io.ObjectInputStream is) throws IOException,
      ClassNotFoundException {
    key = (PublisherKey) is.readObject();
    transactionId = new Long(is.readLong());
    qos = QoSLevel.fromOrdinal(is.readInt());
    priority = new UInteger(is.readLong());
    version = new UOctet(is.readShort());
    int entityKeyListSize = is.readInt();
    entityPublishContextList = new ArrayList<EntityPublishContext>(
        entityKeyListSize);
    for (int i = 0; i < entityKeyListSize; i++) {
      EntityPublishContext entityPublishContext = (EntityPublishContext) is
          .readObject();
      entityPublishContextList.add(entityPublishContext);
    }
  }

  private void writeObject(java.io.ObjectOutputStream os) throws IOException {
    os.writeObject(key);
    os.writeLong(transactionId);
    os.writeInt(qos.getOrdinal());
    os.writeLong(priority.getValue());
    os.writeShort(version.getValue());
    os.writeInt(entityPublishContextList.size());
    for (int i = 0; i < entityPublishContextList.size(); i++) {
      EntityPublishContext entityPublishContext = entityPublishContextList
          .get(i);
      os.writeObject(entityPublishContext);
    }
  }

  public PublisherContext(PublisherKey key, Long transactionId, QoSLevel qos,
      UInteger priority, UOctet version, EntityKeyList patterns, String jmxName) {
    super();
    this.key = key;
    this.transactionId = transactionId;
    this.qos = qos;
    this.priority = priority;
    this.version = version;
    this.jmxName = jmxName;
    setPatterns(patterns);
  }
  
  public void initJmx(String jmxName) {
    this.jmxName = jmxName;
    for (int i = 0; i < entityPublishContextList.size(); i++) {
      EntityPublishContext entityPublishContext = entityPublishContextList.get(i);
      String mBeanName = getMBeanName(i);
      try {
        MXWrapper.registerMBean(entityPublishContext, mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  public void unregisterMBeans() {
    for (int i = 0; i < entityPublishContextList.size(); i++) {
      String mBeanName = getMBeanName(i);
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
      }
    }
  }
  
  private String getMBeanName(int index) {
    return jmxName + ",publishEntity=PublishEntity-" + index;
  }
  
  private void addPublish(EntityPublishContext entityPublishContext) {
    String mBeanName = getMBeanName(entityPublishContextList.size());
    entityPublishContextList.add(entityPublishContext);
    try {
      MXWrapper.registerMBean(entityPublishContext, mBeanName);
    } catch (Exception e) {
      Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed", e);
    }
  }
  
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

  public PublisherKey getKey() {
    return key;
  }

  public Long getTransactionId() {
    return transactionId;
  }

  public QoSLevel getQos() {
    return qos;
  }

  public void setQos(QoSLevel qos) {
    this.qos = qos;
  }

  public UInteger getPriority() {
    return priority;
  }

  public void setPriority(UInteger priority) {
    this.priority = priority;
  }

  public void checkUpdates(UpdateCheckReport report,
      UpdateHeaderList updateHeaderList, List... updateLists) {
    updateLoop: 
    for (int j = 0; j < updateHeaderList.size(); j++) {
      UpdateHeader updateHeader = updateHeaderList.get(j);
      EntityKey updateKey = updateHeader.getKey();
      for (int i = 0; i < entityPublishContextList.size(); i++) {
        EntityPublishContext entityPublishContext = entityPublishContextList
            .get(i);
        if (entityPublishContext.match(updateKey)) {
          report.addEntityPublishContext(entityPublishContext);
          report.addUpdateHeaderToNotify(updateHeader);
          for (int k = 0; k < updateLists.length; k++) {
            if (updateLists[k] != null) {
              report.addUpdateToNotify(k, updateLists[k].get(j));
            }
          }
          continue updateLoop;
        }
      }

      report.addFailedUpdateHeader(updateHeader);
      for (int k = 0; k < updateLists.length; k++) {
        if (updateLists[k] != null) {
          report.addFailedUpdate(k, updateLists[k].get(j));
        }
      }
    }
  }
  
  public void checkSubscription(SubscriptionContext subscriptionContext) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "PublisherContext.checkSubscription(" + subscriptionContext + ')');
    SubscriberContext subscriberContext = subscriptionContext.getSubscriberContext();
    DomainKey domainKey = key.getDomainKey();
    List<EntityRequestContext> entityRequests = subscriptionContext.getEntityRequestContexts();
    loop:
    for (EntityRequestContext entityRequestContext : entityRequests) {
      if (subscriberContext.match(domainKey.getDomain(),
          domainKey.getNetworkZone(), domainKey.getSessionType(),
          domainKey.getSessionName())
          && entityRequestContext.matchArea(domainKey.getArea())
          && entityRequestContext.matchService(domainKey.getService())
          && entityRequestContext.matchOperation(domainKey.getOperation())
          && entityRequestContext.matchDomain(domainKey.getDomain())) {
        for (EntityPublishContext entityPublishContext : entityPublishContextList) {
          if (entityPublishContext.checkEntityRequestEquals(entityRequestContext)) continue loop;
        }
        for (EntityPublishContext entityPublishContext : entityPublishContextList) {
          entityPublishContext.checkEntityRequestIncludes(entityRequestContext);
        }
        for (EntityPublishContext entityPublishContext : entityPublishContextList) {
          entityPublishContext.checkEntityRequestPotentialMatch(entityRequestContext);
        }
      }
    }
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "-> entityPublishContextList=" + entityPublishContextList);
  }
  
  public void removeEntityRequest(EntityRequestContext entityRequestContext) {
    for (int i = 0; i < entityPublishContextList.size(); i++) {
      EntityPublishContext entityPublishContext = entityPublishContextList.get(i);
      entityPublishContext.removeEntityRequest(entityRequestContext);
    }
  }
  
  public void removeSubscriptionContext(SubscriptionContext subscriptionContext) {
    List<EntityRequestContext> entityRequestContextList = subscriptionContext.getEntityRequestContexts();
    for (EntityRequestContext entityRequestContext : entityRequestContextList) {
      removeEntityRequest(entityRequestContext);
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
        + ", qos=" + qos + ", priority=" + priority + ", entityPublishContextList=" + entityPublishContextList
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
    return qos.toString();
  }

  public long getPriorityAsLong() {
    return priority.getValue();
  }
}