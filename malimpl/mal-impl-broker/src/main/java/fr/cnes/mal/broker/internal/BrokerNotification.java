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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

public class BrokerNotification {
  
  private URI subscriberUri;
  
  private Long transactionId;
  
  private IdentifierList domain;
  
  private Identifier networkZone;
  
  private SessionType sessionType;
  
  private Identifier sessionName;
  
  private QoSLevel qosLevel;
  
  private Map qosProperties;
  
  private UInteger priority;
  
  private UShort area;
  
  private UShort service;
  
  private UShort operation;
  
  private UOctet version;
  
  private List<BrokerSubscriptionUpdate> subscriptionUpdates;
  
  public BrokerNotification(URI subscriberUri, Long transactionId,
      IdentifierList domain, Identifier networkZone, SessionType sessionType, Identifier sessionName,
      QoSLevel qosLevel, Map qosProperties, UInteger priority, UShort area,
      UShort service, UShort operation, UOctet version) {
    super();
    this.subscriberUri = subscriberUri;
    this.transactionId = transactionId;
    this.domain = domain;
    this.networkZone = networkZone;
    this.sessionType = sessionType;
    this.sessionName = sessionName;
    this.qosLevel = qosLevel;
    this.qosProperties = qosProperties;
    this.priority = priority;
    this.area = area;
    this.service = service;
    this.operation = operation;
    this.version = version;
    subscriptionUpdates = new ArrayList<BrokerSubscriptionUpdate>();
  }
  
  public URI getSubscriberUri() {
    return subscriberUri;
  }
  
  public Long getTransactionId() {
    return transactionId;
  }
  
  public IdentifierList getDomain() {
    return domain;
  }
  
  public Identifier getNetworkZone() {
    return networkZone;
  }
  
  public SessionType getSessionType() {
    return sessionType;
  }
  
  public Identifier getSessionName() {
    return sessionName;
  }

  public UInteger getPriority() {
    return priority;
  }
  
  public QoSLevel getQosLevel() {
    return qosLevel;
  }
  
  public Map getQosProperties() {
    return qosProperties;
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

  public UOctet getVersion() {
    return version;
  }
  
  public List<BrokerSubscriptionUpdate> getSubscriptionUpdateList() {
    return subscriptionUpdates;
  }
  
  public void addSubscriptionUpdate(BrokerSubscriptionUpdate subscriptionUpdate) {
    subscriptionUpdates.add(subscriptionUpdate);
  }
  
  public BrokerSubscriptionUpdate getSubscriptionUpdate(Identifier subscriptionId) {
    for (int i = 0; i < subscriptionUpdates.size(); i++) {
      BrokerSubscriptionUpdate subscriptionUpdate = subscriptionUpdates.get(i);
      if (subscriptionUpdate.getSubscriptionId().equals(subscriptionId)) {
        return subscriptionUpdate;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return "BrokerNotification [subscriberUri=" + subscriberUri
        + ", transactionId=" + transactionId + ", domain="
        + domain + ", networkZone=" + networkZone
        + ", sessionType=" + sessionType + ", sessionName=" + sessionName
        + ", qosLevel=" + qosLevel + ", qosProperties=" + qosProperties
        + ", priority=" + priority + ", area=" + area + ", service=" + service
        + ", operation=" + operation + ", version=" + version
        + ", subscriptionUpdates=" + subscriptionUpdates + "]";
  }
}
