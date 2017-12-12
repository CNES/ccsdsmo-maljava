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
import java.util.Vector;

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.objectweb.util.monolog.api.BasicLevel;

import fr.dyade.aaa.common.Strings;
import fr.dyade.aaa.util.management.MXWrapper;

class SubscriptionContext implements Serializable, SubscriptionContextMBean {
  
  private SubscriberContext subscriberContext;
  
  private Identifier subscriptionId;
  
  private Long transactionId;
  
  private ArrayList<EntityRequestContext> entityRequests;
  
  private String jmxName;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    subscriberContext = (SubscriberContext) is.readObject();
    subscriptionId = new Identifier(is.readUTF());
    transactionId = new Long(is.readLong());
    entityRequests = (ArrayList) is.readObject();
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeObject(subscriberContext);
    os.writeUTF(subscriptionId.getValue());
    os.writeLong(transactionId.longValue());
    os.writeObject(entityRequests);
  }
  
  public SubscriptionContext(SubscriberContext subscriberContext,
      Identifier subscriptionId,
      Long transactionId,
      String jmxName) {
    this.subscriberContext = subscriberContext;
    this.subscriptionId = subscriptionId;
    this.transactionId = transactionId;
    this.jmxName = jmxName;
    entityRequests = new ArrayList<EntityRequestContext>();
  }
  
  public void initJmx(String jmxName) {
    this.jmxName = jmxName;
    for (int i = 0; i < entityRequests.size(); i++) {
      EntityRequestContext entityRequest = entityRequests.get(i);
      String mBeanName = getMBeanName(i);
      try {
        MXWrapper.registerMBean(entityRequest, mBeanName);
      } catch (Exception exc) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
      }
    }
  }
  
  public void unregisterMBeans() {
    for (int i = 0; i < entityRequests.size(); i++) {
      String mBeanName = getMBeanName(i);
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception exc) {
        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
      }
    }
  }
  
  public String getJmxName() {
    return jmxName;
  }

  public void setJmxName(String jmxName) {
    this.jmxName = jmxName;
  }

  public SubscriberContext getSubscriberContext() {
    return subscriberContext;
  }

  public Identifier getSubscriptionId() {
    return subscriptionId;
  }
  
  public Long getTransactionId() {
    return transactionId;
  }
  
  private String getMBeanName(int index) {
    return jmxName + ",entityRequest=EntityRequest-" + index;
  }
  
  public void addEntityRequest(EntityRequestContext entityRequest) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "SubscriptionContext.addEntityRequest(" + entityRequest + ')');
    String mBeanName = getMBeanName(entityRequests.size());
    entityRequests.add(entityRequest);
    try {
      MXWrapper.registerMBean(entityRequest, mBeanName);
    } catch (Exception exc) {
      Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
    }
  }
  
  public List<EntityRequestContext> getEntityRequestContexts() {
    return entityRequests;
  }
  
  public void reset() {
    unregisterMBeans();
    entityRequests.clear();
  }

  public String[] getEntityRequests() {
    String[] res = new String[entityRequests.size()];
    for (int i = 0; i < entityRequests.size(); i++) {
      EntityRequestContext entityRequest = (EntityRequestContext) entityRequests.get(i);
      // Build a string representation of the entity request
      res[i] = entityRequest.toString();
    }
    return res;
  }
  
  public void checkPublisher(PublisherContext publisherContext) {
    publisherContext.checkSubscription(this);
  }
  
  public String toString() {
    return '(' + super.toString() +
    ",subscriptionId=" + subscriptionId + 
    ",transactionId=" + transactionId + 
    ",entityRequests=" + entityRequests + ')';
  }

  public String getSubscriptionIdAsString() {
    return subscriptionId.getValue();
  }
}