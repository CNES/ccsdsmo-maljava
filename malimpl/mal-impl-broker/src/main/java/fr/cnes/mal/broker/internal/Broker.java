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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilter;
import org.ccsds.moims.mo.mal.structures.SubscriptionFilterList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALEncodedElementList;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.dyade.aaa.common.Strings;
import fr.dyade.aaa.util.management.MXWrapper;

public class Broker implements Serializable {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(Broker.class.getName());

  public static final String ALL = "*";
  
  public static MALInteractionException createException(UInteger errorCode, String msg) {
    return new MALInteractionException(new MOErrorException(errorCode, new Union(msg)));
  }
  
  private HashMap<SubscriberKey, SubscriberContext> subscriberContexts;

  private HashMap<PublisherKey, PublisherContext> publisherContexts;

  private String jmxName;

  private void readObject(java.io.ObjectInputStream is) throws IOException,
      ClassNotFoundException {
    subscriberContexts = (HashMap) is.readObject();
    publisherContexts = (HashMap) is.readObject();
  }

  private void writeObject(java.io.ObjectOutputStream os) throws IOException {
    os.writeObject(subscriberContexts);
    os.writeObject(publisherContexts);
  }

  public Broker(String jmxName) {
    subscriberContexts = new HashMap<SubscriberKey, SubscriberContext>();
    publisherContexts = new HashMap<PublisherKey, PublisherContext>();
    this.jmxName = jmxName;
  }
  
  public void initJmx(String jmxName) {
    this.jmxName = jmxName;
    Collection<SubscriberContext> subscribers = subscriberContexts.values();
    Iterator<SubscriberContext> subscriberIterator = subscribers.iterator();
    while (subscriberIterator.hasNext()) {
      SubscriberContext subscriberContext = subscriberIterator.next();
      String mBeanName = getSubscriberMBeanName(
          subscriberContext.getKey());
      subscriberContext.initJmx(mBeanName);
      try {
        MXWrapper.registerMBean(subscriberContext, mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }
    Collection<PublisherContext> publishers = publisherContexts.values();
    Iterator<PublisherContext> publisherIterator = publishers.iterator();
    while (publisherIterator.hasNext()) {
      PublisherContext publisherContext = publisherIterator.next();
      String mBeanName = getPublisherMBeanName(publisherContext.getKey());
      publisherContext.initJmx(mBeanName);
      try {
        MXWrapper.registerMBean(publisherContext, mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }
  }
  
  public void unregisterMBeans() {
    Collection<SubscriberContext> subscribers = subscriberContexts.values();
    Iterator<SubscriberContext> subscriberIterator = subscribers.iterator();
    while (subscriberIterator.hasNext()) {
      SubscriberContext subscriberContext = subscriberIterator.next();
      String mBeanName = getSubscriberMBeanName(
          subscriberContext.getKey());
      subscriberContext.unregisterMBeans();
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }
    
    Collection<PublisherContext> publishers = publisherContexts.values();
    Iterator<PublisherContext> publisherIterator = publishers.iterator();
    while (publisherIterator.hasNext()) {
      PublisherContext publisherContext = publisherIterator.next();
      String mBeanName = getPublisherMBeanName(publisherContext.getKey());
      publisherContext.unregisterMBeans();
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }
  }

  public void register(Identifier subscriberUri, Long transactionId,
      NamedValueList supplements, Map qosProperties,
      Identifier subscriptionId, Subscription subscription,
      UShort area, UShort service, UShort operation, UOctet version) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(
          BasicLevel.DEBUG,
          "Broker.register(" + subscriberUri + ',' + transactionId + ','
              + String.valueOf(supplements) + ',' + qosProperties + ','
              + subscriptionId + ',' + subscription + ')');

    SubscriptionFilterList filters = subscription.getFilters();
    if (filters != null) {
      for (int i = 0; i < filters.size(); i++) {
        SubscriptionFilter filter = filters.get(i);
        if (filter.getName() == null)
          throw new IllegalArgumentException("null name in SubscriptionFilter");
        if (filter.getValues() == null)
          throw new IllegalArgumentException("null values list in SubscriptionFilter");
        if (filter.getValues().contains(null))
          throw new IllegalArgumentException("null value in SubscriptionFilter");
      }
    }
    
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, subscription.getDomain(), version));
    SubscriberContext subscriberContext = (SubscriberContext) subscriberContexts
        .get(key);
    if (subscriberContext == null) {
      String mBeanName = getSubscriberMBeanName(key);
      subscriberContext = new SubscriberContext(key, supplements, qosProperties,
          mBeanName, area, service, operation, version);
      subscriberContexts.put(key, subscriberContext);
      try {
        MXWrapper.registerMBean(subscriberContext, mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }

    SubscriptionContext subscriptionContext = subscriberContext
        .getSubscription(subscriptionId);
    if (subscriptionContext == null) {
      subscriptionContext = new SubscriptionContext(subscriberContext,
          subscriptionId, transactionId,
          subscription.getDomain(), subscription.getSelectedKeys(), subscription.getFilters(), jmxName);
      subscriberContext.addSubscription(subscriptionContext);
    } else {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "subscription context already exists");
      // TODO SL je ne comprends pas ce code
      Collection<PublisherContext> publishers = publisherContexts.values();
      Iterator<PublisherContext> publisherIterator = publishers.iterator();
      while (publisherIterator.hasNext()) {
        PublisherContext publisherContext = publisherIterator.next();
        publisherContext.removeSubscriptionContext(subscriptionContext);
      }
      subscriptionContext.resubscribe(subscription.getDomain(), subscription.getSelectedKeys(), subscription.getFilters());
    }

    // TODO SL process subscription
//    for (int i = 0; i < entityRequests.length; i++) {
//      EntityRequestContext entityRequest = new EntityRequestContext(
//          subscriptionContext, entityRequests[i].getSubDomain(),
//          entityRequests[i].isAllAreas(), entityRequests[i].isAllServices(),
//          entityRequests[i].isAllOperations(), entityRequests[i].getKey(),
//          entityRequests[i].isOnlyOnChange());
//      subscriptionContext.addEntityRequest(entityRequest);
//    }
    
    Collection<PublisherContext> publishers = publisherContexts.values();
    Iterator<PublisherContext> publisherIterator = publishers.iterator();
    while (publisherIterator.hasNext()) {
      PublisherContext publisherContext = publisherIterator.next();
      publisherContext.checkSubscription(subscriptionContext);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "publisherContext=" + publisherContext);
    }
  }

  public void deregister(Identifier subscriberUri,
      IdentifierList idList, UShort area, UShort service,
      UShort operation, UOctet areaVersion) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
          "Broker.deregister(" + subscriberUri + ',' + Strings.toString(idList) + ')');
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, null, areaVersion));
    SubscriberContext subscriberContext = (SubscriberContext) subscriberContexts
        .get(key);
    if (subscriberContext != null) {
      for (int i = 0; i < idList.size(); i++) {
        Identifier subscriptionId = idList.get(i);
        if (subscriberContext.getSubscription(subscriptionId) == null) {
          throw new Exception("Unknown subscription " + subscriptionId
              + ". Can't unsubscribe.");
        }
      }
      for (int i = 0; i < idList.size(); i++) {
        Identifier subscriptionId = idList.get(i);
        SubscriptionContext subscriptionContext = subscriberContext.removeSubscription(subscriptionId);
        if (subscriptionContext != null) {
          Collection<PublisherContext> publishers = publisherContexts.values();
          Iterator<PublisherContext> publisherIterator = publishers.iterator();
          while (publisherIterator.hasNext()) {
            PublisherContext publisherContext = publisherIterator.next();
            publisherContext.removeSubscriptionContext(subscriptionContext);
          }
        }
        if (subscriberContext.getSubscriptionNumber() == 0) {
          subscriberContexts.remove(key);
          String subscriberMBeanName = getSubscriberMBeanName(key);
          subscriberContext.unregisterMBeans();
          try {
            MXWrapper.unregisterMBean(subscriberMBeanName);
          } catch (Exception exc) {
            logger.log(
                BasicLevel.WARN,
                this.getClass().getName()
                    + " jmx failed: "
                    + subscriberMBeanName);
          }
        }
      }
    }
    // Else idempotent
  }

  public void deregister(Identifier subscriberUri,
      UShort area, UShort service,
      UShort operation, UOctet areaVersion)
      throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.deregister(" + subscriberUri + ','
          + area + ',' + service + ',' + operation + ')');
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, null, areaVersion));
    SubscriberContext subscriberContext = subscriberContexts.remove(key);
    if (subscriberContext != null) {
      subscriberContext.unregisterMBeans();
      Collection<PublisherContext> publishers = publisherContexts.values();
      Iterator<PublisherContext> publisherIterator = publishers.iterator();
      while (publisherIterator.hasNext()) {
        PublisherContext publisherContext = publisherIterator.next();
        publisherContext.removeSubscriberContext(subscriberContext);
      }
      String mBeanName = getSubscriberMBeanName(key);
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "subscriberContexts=" + subscriberContexts);
  }

  private String getSubscriberMBeanName(SubscriberKey key) {
    StringBuffer buf = new StringBuffer();
    buf.append(jmxName);
    buf.append(",pubsub=");
    buf.append("area#");
    buf.append(key.getDomainKey().getArea());
    buf.append("-v");
    buf.append(key.getDomainKey().getAreaVersion());
    buf.append("-service#");
    buf.append(key.getDomainKey().getService());
    buf.append("-op#");
    buf.append(key.getDomainKey().getOperation());
    buf.append(",subscriber=Subscriber-");
    String subUri = key.getSubscriberUri().toString();
    subUri = subUri.replace(':', '-');
    subUri = subUri.replace('=', '-');
    buf.append(subUri);
    return buf.toString();
  }
  
  private String getPublisherMBeanName(PublisherKey key) {
    StringBuffer buf = new StringBuffer();
    buf.append(jmxName);
    buf.append(",pubsub=");
    buf.append("area#");
    buf.append(key.getDomainKey().getArea());
    buf.append("-v");
    buf.append(key.getDomainKey().getAreaVersion());
    buf.append("-service#");
    buf.append(key.getDomainKey().getService());
    buf.append("-op#");
    buf.append(key.getDomainKey().getOperation());
    buf.append(",publisher=Publisher-");
    String publisherUri = key.getUri().toString();
    publisherUri = publisherUri.replace(':', '-');
    publisherUri = publisherUri.replace('=', '-');
    buf.append(publisherUri);
    return buf.toString();
  }

  public BrokerNotification[] publish(BrokerPublication publication)
      throws UnknownEntityException, UnknownPublisherException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.publish()");

    UpdateCheckReport report = checkUpdates(publication.getUriFrom(),
        publication.getArea(), publication.getService(), publication.getOperation(), publication.getVersion(),
        publication.getUpdateHeader(), publication.getUpdateObjects());
    HashMap<SubscriberKey, BrokerNotification> notifications = new HashMap<SubscriberKey, BrokerNotification>();
    List<PublisherContext> publisherContexts = report.getPublisherContextList();
    // TODO SL simplification en cours
    // la liste ne peut avoir qu'un seul element
    if (publisherContexts == null || publisherContexts.isEmpty()) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Broker.publish, no message to notify");
      if (report.getFailedUpdateHeader() == null)
        return null;
      throw new UnknownEntityException(report);
    }
    
    // si elle n'est pas vide, alors on prend l'unique update dans la publication
    PublisherContext publisherContext = publisherContexts.get(0);
    UpdateHeader updateHeaderToNotify = publication.getUpdateHeader();
    Object[] udpatesToNotify = publication.getUpdateObjects();
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "publisherContext=" + publisherContext);
    
    for (SubscriptionContext subscriptionContext : publisherContext.getSubscriptionContexts()) {
      // match domain and subscription keys
      if (subscriptionContext.matchDomain(updateHeaderToNotify.getDomain()) &&
          subscriptionContext.matchKeys(publisherContext.getURI(), updateHeaderToNotify.getKeyValues())) {
        // perform trimming of subscription keys
        UpdateHeader updateHeader = new UpdateHeader(updateHeaderToNotify.getSource(), updateHeaderToNotify.getDomain(),
                                                     subscriptionContext.trimKeys(publisherContext.getURI(), updateHeaderToNotify.getKeyValues()));
        publish(publication, subscriptionContext, notifications,
                updateHeader, udpatesToNotify);
      }
    }
  
    BrokerNotification[] res = new BrokerNotification[notifications.size()];
    notifications.values().toArray(res);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "notifications: " + Strings.toString(res));
    return res;
  }
  
  private void publish(BrokerPublication publication,
      SubscriptionContext subscriptionContext, 
      HashMap<SubscriberKey, BrokerNotification> notifications,
      UpdateHeader updateHeaderToNotify,
      Object[] updatesToNotify) {
    SubscriberContext subscriberContext = subscriptionContext.getSubscriberContext();
    BrokerNotification notification = notifications.get(subscriberContext.getKey());
    if (notification == null) {
      notification = new BrokerNotification(
          subscriberContext.getKey().getSubscriberUri(),
          subscriptionContext.getTransactionId(),
          subscriberContext.getSupplements(),
          subscriberContext.getQosProperties(),
          publication.getArea(), publication.getService(),
          publication.getOperation(), publication.getVersion());
      notifications.put(subscriberContext.getKey(), notification);
    }
    // TODO SL verifier ce passage par BrokerSubscriptionUpdate.
    // peut-il vraiment y avoir plusieurs updates ?
    BrokerSubscriptionUpdate subscriptionUpdate = notification
        .getSubscriptionUpdate(subscriptionContext.getSubscriptionId());
    if (subscriptionUpdate != null) {
      // unexpected case with the move to a single update per Notify message
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "Unexpected existing SubscriptionUpdate");
      throw new IllegalStateException("Unexpected existing SubscriptionUpdate");
    }
    subscriptionUpdate = new BrokerSubscriptionUpdate(
        subscriptionContext.getSubscriptionId(), updatesToNotify);
    notification.addSubscriptionUpdate(subscriptionUpdate);
    UpdateHeader updateHeader = updateHeaderToNotify;
    subscriptionUpdate.addUpdateHeader(updateHeader);
    subscriptionUpdate.addUpdate(updatesToNotify);
  }
  
  // TODO SL a simplifier, single update
  private static List createUpdateList(List updateList) {
    if (updateList == null) {
      return null;
    } else if (updateList instanceof MALEncodedElementList) {
      MALEncodedElementList encodedElementList = (MALEncodedElementList) updateList;
      return new MALEncodedElementList(encodedElementList.getShortForm(),
          encodedElementList.size());
    } else {
      return (List) ((Element) updateList).createElement();
    }
  }

  public int getSubscriptionNumber() {
    return subscriberContexts.size();
  }

  public PublisherContext registerPublisher(Identifier providerURI, Long tid,
      IdentifierList domain,
      IdentifierList subKeys, AttributeTypeList keyTypes, UShort area, UShort service,
      UShort operation, UOctet version) throws MALInteractionException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.registerPublisher(" + providerURI
          + ',' + tid + ',' + Strings.toString(domain) + ',' + subKeys + ',' + keyTypes + ')');
    if (subKeys == null)
      throw new IllegalArgumentException("null key names in PublishRegister.");
    if (keyTypes == null)
      throw new IllegalArgumentException("null key types in PublishRegister.");
    if (keyTypes.size() != subKeys.size())
      throw new IllegalArgumentException("key names and key types lists don't match in PublishRegister.");

    DomainKey domainKey = new DomainKey(area,
        service, operation, domain, version);
    PublisherKey publisherKey = new PublisherKey(providerURI, domainKey);
    PublisherContext publisherContext = (PublisherContext) publisherContexts
        .get(publisherKey);
    if (publisherContext != null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Broker reregister Publisher " + providerURI);
      // re-register case
      publisherContext.reregister(subKeys, keyTypes);
      // the structures are updated in the following code
    } else {
      String mBeanName = getPublisherMBeanName(publisherKey);
      publisherContext = new PublisherContext(publisherKey, tid, version, subKeys, keyTypes, mBeanName);
      publisherContexts.put(publisherKey, publisherContext);
      try {
        MXWrapper.registerMBean(publisherContext, mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
                   + " jmx failed: " + mBeanName, e);
      }
    }

    Collection<SubscriberContext> subscribers = subscriberContexts.values();
    Iterator<SubscriberContext> subscriberIterator = subscribers.iterator();
    while (subscriberIterator.hasNext()) {
      SubscriberContext subscriberContext = subscriberIterator.next();
      subscriberContext.checkPublisher(publisherContext);
    }

    return publisherContext;
  }

  public PublisherContext deregisterPublisher(Identifier providerURI,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, UShort area, UShort service,
      UShort operation, UOctet areaVersion) {
    DomainKey domainKey = new DomainKey(area, service, operation, null, areaVersion);
    PublisherKey publisherKey = new PublisherKey(providerURI, domainKey);
    PublisherContext publisherContext = (PublisherContext) publisherContexts
        .remove(publisherKey);
    if (publisherContext != null) {
      publisherContext.unregisterMBeans();
      String mBeanName = getPublisherMBeanName(publisherKey);
      try {
        MXWrapper.unregisterMBean(mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
          + " jmx failed: " + mBeanName, e);
      }
    }
    return publisherContext;
  }

  private UpdateCheckReport checkUpdates(Identifier providerURI,
      UShort area, UShort service,
      UShort operation,
      UOctet areaVersion,
      UpdateHeader updateHeader,
      Object... updateObjects) throws UnknownPublisherException,
      UnknownEntityException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.checkUpdates(" + providerURI + ','
          + updateHeader + ')');
    // TODO SL bug here regarding the publisher key
    // the domain is important to find the publisher according to the Java API
    // however this publisher domain is unknown here
    // the question remains about the legality of the MonitorPublisher Java API
    // as there should probably exist only one publisher for the provider.
    DomainKey domainKey = new DomainKey(area,
        service, operation, null, areaVersion);
    PublisherKey publisherKey = new PublisherKey(providerURI, domainKey);
    PublisherContext ctx = (PublisherContext) publisherContexts
        .get(publisherKey);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "publisherContext=" + ctx);
    if (ctx == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Unknown publisher context: "
            + publisherKey);
      throw new UnknownPublisherException("Unknown publisher context: "
          + publisherKey);
    }
    UpdateCheckReport report = new UpdateCheckReport(ctx.getTransactionId());
    ctx.checkUpdates(report, updateHeader, updateObjects);
    return report;
  }
}
