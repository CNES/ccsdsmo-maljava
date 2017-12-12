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

import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
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

  public void register(URI subscriberUri, Long transactionId,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel qosLevel, Map qosProperties,
      UInteger priority, Identifier subscriptionId,
      BrokerEntityRequest[] entityRequests, UShort area, UShort service,
      UShort operation, UOctet version) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(
          BasicLevel.DEBUG,
          "Broker.register(" + subscriberUri + ',' + transactionId + ','
              + Strings.toString(domain) + ',' + networkZone + ','
              + sessionType + ',' + qosLevel + ',' + qosProperties + ','
              + priority + ',' + subscriptionId + ','
              + Strings.toString(entityRequests) + ')');
    String sessionNameS = null;
    if (sessionName != null) {
      sessionNameS = sessionName.getValue();
    }
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, domain, networkZone, sessionType, sessionName, version));
    SubscriberContext subscriberContext = (SubscriberContext) subscriberContexts
        .get(key);
    if (subscriberContext == null) {
      String mBeanName = getSubscriberMBeanName(key);
      subscriberContext = new SubscriberContext(key, qosLevel, qosProperties,
          priority, mBeanName, area, service, operation, version);
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
          subscriptionId, transactionId, jmxName);
      subscriberContext.addSubscription(subscriptionContext);
    } else {
      Collection<PublisherContext> publishers = publisherContexts.values();
      Iterator<PublisherContext> publisherIterator = publishers.iterator();
      while (publisherIterator.hasNext()) {
        PublisherContext publisherContext = publisherIterator.next();
        publisherContext.removeSubscriptionContext(subscriptionContext);
      }
      subscriptionContext.reset();
    }

    for (int i = 0; i < entityRequests.length; i++) {
      EntityRequestContext entityRequest = new EntityRequestContext(
          subscriptionContext, entityRequests[i].getSubDomain(),
          entityRequests[i].isAllAreas(), entityRequests[i].isAllServices(),
          entityRequests[i].isAllOperations(), entityRequests[i].getKey(),
          entityRequests[i].isOnlyOnChange());
      subscriptionContext.addEntityRequest(entityRequest);
    }
    
    Collection<PublisherContext> publishers = publisherContexts.values();
    Iterator<PublisherContext> publisherIterator = publishers.iterator();
    while (publisherIterator.hasNext()) {
      PublisherContext publisherContext = publisherIterator.next();
      publisherContext.checkSubscription(subscriptionContext);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "publisherContext=" + publisherContext);
    }
  }

  public void deregister(URI subscriberUri, IdentifierList domain,
      Identifier networkZone, SessionType sessionType, Identifier sessionName,
      IdentifierList idList, UShort area, UShort service,
      UShort operation, UOctet areaVersion) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
          "Broker.deregister(" + subscriberUri + ',' + Strings.toString(domain)
              + ',' + networkZone + ',' + sessionType + ',' + sessionName + ','
              + subscriberUri + ',' + Strings.toString(idList) + ')');
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, domain,
        networkZone, sessionType, sessionName, areaVersion));
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

  public void deregister(URI subscriberUri, IdentifierList domain,
      Identifier networkZone, SessionType sessionType, Identifier sessionName,
      UShort area, UShort service,
      UShort operation, UOctet areaVersion)
      throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.deregister(" + subscriberUri + ','
          + Strings.toString(domain) + ',' + networkZone + ',' + sessionType
          + ',' + sessionName + ','
          + area + ',' + service + ',' + operation + ')');
    SubscriberKey key = new SubscriberKey(subscriberUri, new DomainKey(area,
        service, operation, domain,
        networkZone, sessionType, sessionName, areaVersion));
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
    buf.append("-");
    IdentifierList domainId = key.getDomainKey().getDomain();
    if (domainId.size() > 0) {
      buf.append(domainId.get(0));
      for (int i = 1; i < domainId.size(); i++) {
        Identifier id = domainId.get(i);
        buf.append("-");
        buf.append(id);
      }
    }
    buf.append("-");
    buf.append(key.getDomainKey().getNetworkZone());
    buf.append("-");
    buf.append(key.getDomainKey().getSessionType().toString());
    buf.append("-");
    buf.append(key.getDomainKey().getSessionName());
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
    buf.append("-");
    IdentifierList domainId = key.getDomainKey().getDomain();
    if (domainId.size() > 0) {
      buf.append(domainId.get(0));
      for (int i = 1; i < domainId.size(); i++) {
        Identifier id = domainId.get(i);
        buf.append("-");
        buf.append(id);
      }
    }
    buf.append("-");
    buf.append(key.getDomainKey().getNetworkZone());
    buf.append("-");
    buf.append(key.getDomainKey().getSessionType().toString());
    buf.append("-");
    buf.append(key.getDomainKey().getSessionName());
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
      logger.log(BasicLevel.DEBUG, "Broker.publish("
          + publication.getUpdateHeaders().size() + ')');

    UpdateCheckReport report = checkUpdates(publication.getUriFrom(),
        publication.getArea(), publication.getService(), publication.getOperation(), 
        publication.getVersion(),
        publication.getDomain(), publication.getNetworkZone(),
        publication.getSessionType(), publication.getSessionName(),
        publication.getUpdateHeaders(), publication.getUpdateLists());
    HashMap<SubscriberKey, BrokerNotification> notifications = new HashMap<SubscriberKey, BrokerNotification>();
    List<EntityPublishContext> entityPublishContexts = report
        .getEntityPublishContextList();
    UpdateHeaderList updateHeadersToNotify = report.getUpdateHeadersToNotify();
    List[] udpatesToNotify = report.getUpdatesToNotify();
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "entityPublishContexts=" + entityPublishContexts);
    for (int i = 0; i < entityPublishContexts.size(); i++) {
      EntityPublishContext entityPublishContext = entityPublishContexts.get(i);
      UpdateHeader updateHeader = updateHeadersToNotify.get(i);
      List<EntityRequestContext> entityRequestContexts = entityPublishContext
          .getEntityRequestContexts();
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "entityRequestContexts=" + entityRequestContexts);
      for (EntityRequestContext entityRequestContext : entityRequestContexts) {
        if (entityRequestContext.matchUpdateType(updateHeader.getUpdateType().getOrdinal())) {
          publish(publication, entityRequestContext, notifications,
            updateHeadersToNotify, udpatesToNotify, i);
        }
      }
      List<EntityRequestContext> uncheckedEntityRequestContexts = entityPublishContext
          .getUncheckedEntityRequestContexts();
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "uncheckedEntityRequestContexts=" + uncheckedEntityRequestContexts);
      for (EntityRequestContext entityRequestContext : uncheckedEntityRequestContexts) {
        if (entityRequestContext.matchUpdateType(updateHeader.getUpdateType().getOrdinal()) &&
            entityRequestContext.matchEntityKey(updateHeader.getKey())) {
          publish(publication, entityRequestContext, notifications,
              updateHeadersToNotify, udpatesToNotify, i);
        }
      }
    }
    BrokerNotification[] res = new BrokerNotification[notifications.size()];
    notifications.values().toArray(res);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "notifications: " + Strings.toString(res));
    return res;
  }
  
  private void publish(BrokerPublication publication,
      EntityRequestContext entityRequestContext, 
      HashMap<SubscriberKey, BrokerNotification> notifications,
      UpdateHeaderList updateHeadersToNotify,
      List[] updatesToNotify,
      int entityPublishContextIndex) {
    SubscriptionContext subscriptionContext = entityRequestContext.getSubscriptionContext();
    SubscriberContext subscriberContext = subscriptionContext.getSubscriberContext();
    BrokerNotification notification = notifications.get(subscriberContext
        .getKey());
    if (notification == null) {
      notification = new BrokerNotification(
          subscriberContext.getKey().getSubscriberUri(),
          subscriptionContext.getTransactionId(),
          publication.getDomain(),
          publication.getNetworkZone(),
          //subscriptionContext.getSubscriberContext().getKey().getDomainKey().getNetworkZone(),
          // Must match the publication session type
          subscriberContext.getKey().getDomainKey().getSessionType(),
          subscriberContext.getKey().getDomainKey().getSessionName(),
          // Must match the publication session name
          subscriberContext.getQoSLevel(),
          subscriberContext.getQosProperties(),
          subscriberContext.getPriority(), publication.getArea(),
          publication.getService(), publication.getOperation(),
          publication.getVersion());
      notifications.put(subscriberContext.getKey(), notification);
    }
    BrokerSubscriptionUpdate subscriptionUpdate = notification
        .getSubscriptionUpdate(subscriptionContext.getSubscriptionId());
    if (subscriptionUpdate == null) {
      List[] notifiedUpdateLists = new List[updatesToNotify.length];
      for (int j = 0; j < notifiedUpdateLists.length; j++) {
        notifiedUpdateLists[j] = createUpdateList(updatesToNotify[j]);
      }
      subscriptionUpdate = new BrokerSubscriptionUpdate(
          subscriptionContext.getSubscriptionId(), notifiedUpdateLists);
      notification.addSubscriptionUpdate(subscriptionUpdate);
    }
    UpdateHeader updateHeader = updateHeadersToNotify
        .get(entityPublishContextIndex);
    subscriptionUpdate.addUpdateHeader(updateHeader);
    for (int j = 0; j < updatesToNotify.length; j++) {
      if (updatesToNotify[j] != null) {
        subscriptionUpdate.addUpdate(j,
            updatesToNotify[j].get(entityPublishContextIndex));
      }
    }
  }
  
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

  public PublisherContext registerPublisher(URI providerURI, Long tid,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, QoSLevel qos, UInteger priority,
      EntityKeyList patterns, UShort area, UShort service,
      UShort operation, UOctet version) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.registerPublisher(" + providerURI
          + ',' + tid + ',' + Strings.toString(domain) + ',' + networkZone
          + ',' + sessionType + ',' + sessionName + ',' + qos + ',' + priority
          + ',' + patterns + ')');
    DomainKey domainKey = new DomainKey(area,
        service, operation,domain, networkZone, sessionType,
        sessionName, version);
    PublisherKey publisherKey = new PublisherKey(providerURI, domainKey);
    PublisherContext publisherContext = (PublisherContext) publisherContexts
        .get(publisherKey);
    if (publisherContext == null) {
      String mBeanName = getPublisherMBeanName(publisherKey);
      publisherContext = new PublisherContext(publisherKey, tid, qos, priority,
          version, patterns, mBeanName);
      publisherContexts.put(publisherKey, publisherContext);
      try {
        MXWrapper.registerMBean(publisherContext, mBeanName);
      } catch (Exception e) {
        logger.log(BasicLevel.WARN, this.getClass().getName()
            + " jmx failed: " + mBeanName, e);
      }
    } else {
      publisherContext.setPatterns(patterns);
    }

    Collection<SubscriberContext> subscribers = subscriberContexts.values();
    Iterator<SubscriberContext> subscriberIterator = subscribers.iterator();
    while (subscriberIterator.hasNext()) {
      SubscriberContext subscriberContext = subscriberIterator.next();
      subscriberContext.checkPublisher(publisherContext);
    }

    return publisherContext;
  }

  public PublisherContext deregisterPublisher(URI providerURI,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, UShort area, UShort service,
      UShort operation, UOctet areaVersion) {
    DomainKey domainKey = new DomainKey(area,
        service, operation, domain, networkZone, sessionType,
        sessionName, areaVersion);
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

  private UpdateCheckReport checkUpdates(URI providerURI,
      UShort area, UShort service,
      UShort operation,
      UOctet areaVersion,
      IdentifierList domain, Identifier networkZone, SessionType sessionType,
      Identifier sessionName, UpdateHeaderList updateHeaderList,
      List... updateLists) throws UnknownPublisherException,
      UnknownEntityException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Broker.checkUpdates(" + providerURI + ','
          + Strings.toString(domain) + ',' + networkZone + ',' + sessionType
          + ',' + sessionName + ',' + updateHeaderList + ')');
    DomainKey domainKey = new DomainKey(area,
        service, operation, domain, networkZone, sessionType,
        sessionName, areaVersion);
    PublisherKey publisherKey = new PublisherKey(providerURI, domainKey);
    PublisherContext ctx = (PublisherContext) publisherContexts
        .get(publisherKey);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "publisherContexts=" + publisherContexts
          + ')');
    if (ctx == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Unknown publisher context: "
            + publisherKey);
      throw new UnknownPublisherException("Unknown publisher context: "
          + publisherKey);
    }
    List[] updatesToNotify = new List[updateLists.length];
    List[] failedUpdates = new List[updateLists.length];
    for (int i = 0; i < updatesToNotify.length; i++) {
      updatesToNotify[i] = createUpdateList(updateLists[i]);
    }
    for (int i = 0; i < failedUpdates.length; i++) {
      failedUpdates[i] = createUpdateList(updateLists[i]);
    }
    UpdateCheckReport report = new UpdateCheckReport(ctx.getTransactionId(),
        ctx.getQos(), ctx.getPriority(), updatesToNotify, failedUpdates);
    ctx.checkUpdates(report, updateHeaderList, updateLists);
    if (report.getFailedUpdateCount() > 0) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "report.getFailedUpdateHeaders()=" + report.getFailedUpdateHeaders());
      throw new UnknownEntityException(report);
    }
    return report;
  }
}
