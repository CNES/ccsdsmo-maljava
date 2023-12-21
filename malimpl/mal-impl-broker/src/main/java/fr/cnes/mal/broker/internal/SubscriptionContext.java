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
import java.util.Arrays;
import java.util.HashMap;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.NullableAttribute;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilter;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilterList;
import org.objectweb.util.monolog.api.BasicLevel;

import fr.dyade.aaa.common.Strings;

/**
 * The SubscriptionContext collects all the data required for processing a single subscription.
 * This includes the subscriberContext that issued the subscription, the id and characteristics
 * of the subscription, and a set of contexts related to each publisher that may publish compatible messages.
 */
class SubscriptionContext implements Serializable, SubscriptionContextMBean {
  
  private SubscriberContext subscriberContext;
  
  private Identifier subscriptionId;
  
  private Long transactionId;
  
  // TODO SL remove?
//  private ArrayList<EntityRequestContext> entityRequests;
  private IdentifierList domain;
  private SubscriptionFilterList filters;
  private IdentifierList selectedKeys;
  
  private class PublisherIndexes {
    // indexes of the selected keys in the total list of subscription keys known from the PublishRegister 
    int[] selectedKeys;
    // indexes of the filter keys in the total list of subscription keys known from the PublishRegister 
    int[] filterKeys;
  }
  private HashMap <String, PublisherIndexes> publisherIndexes = new HashMap <String, PublisherIndexes> ();
  // TODO SL ne pas utiliser le tid comme clef, il est relatif au publisher
  
  private String jmxName;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    subscriberContext = (SubscriberContext) is.readObject();
    subscriptionId = new Identifier(is.readUTF());
    transactionId = new Long(is.readLong());
    /*
     * TODO SL optimize serialization
    int domainLength = is.readInt();
    if (domainLength > 0) {
      domain = new IdentifierList(domainLength);
      for (int i = 0; i < domainLength; i++) {
        domain.add(new Identifier(is.readUTF()));
      }
    }
    */
    domain = (IdentifierList) is.readObject();
    filters = (SubscriptionFilterList) is.readObject();
    selectedKeys = (IdentifierList) is.readObject();
    publisherIndexes = (HashMap) is.readObject();
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeObject(subscriberContext);
    os.writeUTF(subscriptionId.getValue());
    os.writeLong(transactionId.longValue());
    /*
     * TODO SL optimize serialization
    if (domain == null) {
      os.writeInt(0);
    } else {
      os.writeInt(domain.size());
      for (int i = 0; i < domain.size(); i++) {
        os.writeUTF(domain.get(i).getValue());
      }
    }
    */
    os.writeObject(domain);
    os.writeObject(filters);
    os.writeObject(selectedKeys);
    os.writeObject(publisherIndexes);
  }
  
  public SubscriptionContext(SubscriberContext subscriberContext,
      Identifier subscriptionId,
      Long transactionId,
      IdentifierList domain,
      IdentifierList selectedKeys,
      SubscriptionFilterList filters,
      String jmxName) {
    this.subscriberContext = subscriberContext;
    this.subscriptionId = subscriptionId;
    this.transactionId = transactionId;
    this.domain = domain;
    this.filters = filters;
    this.selectedKeys = selectedKeys;
    // filter/selectedKeysIndex are provided by PublisherContext.checkSubscription
    
    // TODO SL
    // je crois que la bonne valeur de jmxName est fournie par setJmxName, et non ici
    // du coup il faudrait peut-etre supprimer ce parametre du constructeur
    this.jmxName = jmxName;
//    entityRequests = new ArrayList<EntityRequestContext>();
  }
  
  public void resubscribe(
      IdentifierList domain,
      IdentifierList selectedKeys,
      SubscriptionFilterList filters) {
    this.domain = domain;
    this.filters = filters;
    this.selectedKeys = selectedKeys;
    // the rest of the structure is updated by a following call to check/updateSubscription
  }
  
  public void updatePublisher(PublisherContext publisherContext) {
    int[] filterKeysIndex = null;
    int[] selectedKeysIndex = null;
    boolean acceptPublisher = true;

    IdentifierList publisherKeys = publisherContext.getSubKeys();
    
    // check the filter keys all belong to the list of publish keys
    if (filters != null) {
      filterKeysIndex = new int[filters.size()];
      for (int i = 0; i < filters.size(); i++) {
        SubscriptionFilter filter = filters.get(i);
        filterKeysIndex[i] = publisherKeys.indexOf(filter.getName());
        if (filterKeysIndex[i] == -1) {
          if (Broker.logger.isLoggable(BasicLevel.DEBUG))
            Broker.logger.log(BasicLevel.DEBUG, "Unknown filter key in publisher subKeys: " + filter.getName());
          acceptPublisher = false;
        }
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Filter key " + i + " is publisher subKeys " + filterKeysIndex[i]);
      }
    }

    // check the selected keys all belong to the list of publish keys
    if (acceptPublisher == true && selectedKeys != null) {
      selectedKeysIndex = new int[selectedKeys.size()];
      for (int i = 0; i < selectedKeys.size(); i++) {
        Identifier keyName = selectedKeys.get(i);
        selectedKeysIndex[i] = publisherKeys.indexOf(keyName);
        if (selectedKeysIndex[i] == -1) {
          if (Broker.logger.isLoggable(BasicLevel.DEBUG))
            Broker.logger.log(BasicLevel.DEBUG, "Unknown selected key in publisher subKeys: " + keyName);
          acceptPublisher = false;
        }
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Selected key " + i + " is publisher subKeys " + selectedKeysIndex[i]);
      }
    }

    PublisherIndexes publisher = publisherIndexes.get(publisherContext.getURI());
    if (acceptPublisher == true) {
      if (publisher == null) {
        publisher = new PublisherIndexes();
        publisherIndexes.put(publisherContext.getURI(), publisher);
      }
      publisher.filterKeys = filterKeysIndex;
      publisher.selectedKeys = selectedKeysIndex;
      publisherContext.addSubscription(this);
    } else if (publisher != null) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "remove PublisherContext from the SubscriptionContext");
      publisherContext.removeSubscriptionContext(this);
      if (publisher != null) {
        publisherIndexes.remove(publisherContext.getURI());
      }
      // TODO SL remove PublisherContext from the SubscriptionContext
    }
  }
  
//  public void initJmx(String jmxName) {
//    this.jmxName = jmxName;
//    for (int i = 0; i < entityRequests.size(); i++) {
//      EntityRequestContext entityRequest = entityRequests.get(i);
//      String mBeanName = getMBeanName(i);
//      try {
//        MXWrapper.registerMBean(entityRequest, mBeanName);
//      } catch (Exception exc) {
//        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
//      }
//    }
//  }
  
  public void unregisterMBeans() {
//    for (int i = 0; i < entityRequests.size(); i++) {
//      String mBeanName = getMBeanName(i);
//      try {
//        MXWrapper.unregisterMBean(mBeanName);
//      } catch (Exception exc) {
//        Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
//      }
//    }
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

  public IdentifierList getDomain() {
    return domain;
  }
  
  public SubscriptionFilterList getFilters() {
    return filters;
  }
  
//  private String getMBeanName(int index) {
//    return jmxName + ",entityRequest=EntityRequest-" + index;
//  }
  
//  public void addEntityRequest(EntityRequestContext entityRequest) {
//    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
//      Broker.logger.log(BasicLevel.DEBUG, "SubscriptionContext.addEntityRequest(" + entityRequest + ')');
//    String mBeanName = getMBeanName(entityRequests.size());
//    entityRequests.add(entityRequest);
//    try {
//      MXWrapper.registerMBean(entityRequest, mBeanName);
//    } catch (Exception exc) {
//      Broker.logger.log(BasicLevel.WARN, this.getClass().getName() + " jmx failed: " + mBeanName, exc);
//    }
//  }
  
//  public List<EntityRequestContext> getEntityRequestContexts() {
//    return entityRequests;
//  }
  
  public void reset() {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, "SubscriptionContext.reset(" + subscriptionId + ")");
    unregisterMBeans();
    // TODO SL TBC
//    entityRequests.clear();
  }

//  public String[] getEntityRequests() {
//    String[] res = new String[entityRequests.size()];
//    for (int i = 0; i < entityRequests.size(); i++) {
//      EntityRequestContext entityRequest = (EntityRequestContext) entityRequests.get(i);
//      // Build a string representation of the entity request
//      res[i] = entityRequest.toString();
//    }
//    return res;
//  }
  
  public void checkPublisher(PublisherContext publisherContext) {
    publisherContext.checkSubscription(this);
  }

  boolean matchDomain(IdentifierList publicationDomain) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, toString() + ".match(domain=" + 
          Strings.toString(publicationDomain) + ')');
    int toMatch = domain.size();
    // A final * in the subscription domain may match any final parts of the publication domain
    if (domain.size() > publicationDomain.size()) {
      for (int i = publicationDomain.size(); i < domain.size(); i++) {
        if (! "*".equals(domain.get(i).getValue())) {
          if (Broker.logger.isLoggable(BasicLevel.DEBUG))
            Broker.logger.log(BasicLevel.DEBUG, "Publication domain too short");
          return false;
        }
      }
      toMatch = publicationDomain.size();
    }
    if (domain.size() < publicationDomain.size()) {
      if (! "*".equals(domain.get(domain.size()-1).getValue())) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Publication domain too long");
        return false;
      }
      toMatch = domain.size()-1;
    }
    for (int i = 0; i < toMatch; i++) {
      if (! "*".equals(domain.get(i).getValue())
          && ! domain.get(i).equals(publicationDomain.get(i))) {
        if (Broker.logger.isLoggable(BasicLevel.DEBUG))
          Broker.logger.log(BasicLevel.DEBUG, "Subscription domain is different");
        return false;
      }
    }
    return true; 
  }

  boolean matchKeys(String uri, NullableAttributeList keyValues) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, toString() + ".match(keyValues=" + keyValues + ')');
    PublisherIndexes publisher = publisherIndexes.get(uri);
    if (publisher == null) {
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Filter matching provider not found");
      return false;
    }
    if (filters == null) {
      return true;
    }
    filter_loop:
    for (int i = 0; i < filters.size(); i++) {
      SubscriptionFilter filter = filters.get(i);
      if (filter.getValues() == null || filter.getValues().isEmpty()) {
        // matches any value, including null
        // filter cannot be null according to the specification, the check is left for safety
        continue filter_loop;
      }
      ArrayList<Attribute> filterValues = filter.getValues().getAsAttributes();
      for (int j = 0; j < filterValues.size(); j++) {
        Attribute filterValue = filterValues.get(j);
        if (filterValue == null) {
          // illegal value, should have been checked in Broker.register
          if (Broker.logger.isLoggable(BasicLevel.DEBUG))
            Broker.logger.log(BasicLevel.DEBUG, "Null filter value");
          return false;
        }
        if (filterValue.equals(keyValues.get(publisher.filterKeys[i]).getValue())) {
          // value found
          continue filter_loop;
        }
        Object kvalue = keyValues.get(publisher.filterKeys[i]).getValue();
      }
      // this filter matching fails
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "Filter matching fails on " + filter.getName().getValue());
      return false;
    }
    return true; 
  }

  NullableAttributeList trimKeys(String uri, NullableAttributeList keyValues) {
    if (Broker.logger.isLoggable(BasicLevel.DEBUG))
      Broker.logger.log(BasicLevel.DEBUG, toString() + ".trimKeys()");
    PublisherIndexes publisher = publisherIndexes.get(uri);
    if (publisher == null) {
      // should not occur as it has been previously validated by the call to matchKeys
      if (Broker.logger.isLoggable(BasicLevel.DEBUG))
        Broker.logger.log(BasicLevel.DEBUG, "trimKeys tid not found");
      return null;
    }
    if (selectedKeys == null) {
      return keyValues;
    }
    
    NullableAttributeList notifyKeys = new NullableAttributeList();
    for (int i = 0; i < publisher.selectedKeys.length; i++) {
      notifyKeys.add(keyValues.get(publisher.selectedKeys[i]));
    }
    return notifyKeys;
  }
  
  
  
  public String toString() {
    return '(' + super.toString() +
    ",subscriptionId=" + subscriptionId + 
    ",transactionId=" + transactionId + 
//    ",entityRequests=" + entityRequests + ')';
    ",filters=" + filters + ')';
  }

  public String getSubscriptionIdAsString() {
    return subscriptionId.getValue();
  }
}