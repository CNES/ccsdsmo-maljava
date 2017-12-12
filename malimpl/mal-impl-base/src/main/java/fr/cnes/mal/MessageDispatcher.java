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
package fr.cnes.mal;

import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class MessageDispatcher implements MALMessageListener {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MessageDispatcher.class.getName());

  private Hashtable<ServiceKey, Binding> consumers;
  
  private Hashtable<ServiceKey, Binding> providers;
  
  private MALMessageListener broker;
  
  private MALEndpoint endpoint;
  
  private SubscriptionManager subscriptionManager;
  
  public MessageDispatcher(MALEndpoint endpoint) throws MALException {
    this.endpoint = endpoint;
    consumers = new Hashtable<MessageDispatcher.ServiceKey, Binding>();
    providers = new Hashtable<MessageDispatcher.ServiceKey, Binding>();
    subscriptionManager = new SubscriptionManager();
    endpoint.setMessageListener(this);
  }

  public MALEndpoint getEndPoint() {
    return endpoint;
  }

  public SubscriptionManager getSubscriptionManager() {
    return subscriptionManager;
  }

  public void onMessage(MALEndpoint callingEndpoint, MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher.onMessage(" + msg.getHeader() + ')');
    if (msg.getHeader().getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (msg.getHeader().getInteractionStage().getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
      case MALPubSubOperation._DEREGISTER_STAGE:
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
      case MALPubSubOperation._PUBLISH_DEREGISTER_STAGE:
        deliverBrokerMessage(callingEndpoint, msg);
        break;
      case MALPubSubOperation._REGISTER_ACK_STAGE:
      case MALPubSubOperation._DEREGISTER_ACK_STAGE:
      case MALPubSubOperation._NOTIFY_STAGE:
        deliverConsumerMessage(callingEndpoint, msg);
        break;
      case MALPubSubOperation._PUBLISH_STAGE:
        if (msg.getHeader().getIsErrorMessage().booleanValue()) {
          deliverProviderMessage(callingEndpoint, msg);
        } else {
          deliverBrokerMessage(callingEndpoint, msg);
        }
        break;
      case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
      case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
        deliverProviderMessage(callingEndpoint, msg);
        break;
      default:
        throw new RuntimeException("Unknown Pub/Sub stage: " + msg.getHeader().getInteractionStage().getValue());
      }
    } else {
      if (msg.getHeader().getInteractionStage() != null &&
          msg.getHeader().getInteractionStage().getValue() > 1) {
        deliverConsumerMessage(callingEndpoint, msg);
      } else {
        deliverProviderMessage(callingEndpoint, msg);
      }
    }
  }
  
  private void deliverConsumerMessage(MALEndpoint callingEndpoint, MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].deliverConsumerMessage(" + msg.getHeader() + ')');
    UShort area = msg.getHeader().getServiceArea();
    UShort service = msg.getHeader().getService();
    UOctet version = msg.getHeader().getAreaVersion();
    Binding consumer = consumers.get(new ServiceKey(area, service, version));
    if (consumer != null) {
      consumer.onMessage(callingEndpoint, msg);
    } else {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Failed to dispatch to consumer " + endpoint.getURI() + " " +
            area + "::" + service + " : " + msg);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Not found in: " + consumers);
    }
  }

  private void deliverProviderMessage(MALEndpoint callingEndpoint, MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].deliverProviderMessage(" + msg.getHeader() + ')');
    UShort area = msg.getHeader().getServiceArea();
    UShort service = msg.getHeader().getService();
    UOctet version = msg.getHeader().getAreaVersion();
    Binding provider = providers.get(new ServiceKey(area, service, version));
    if (provider != null) {
      provider.onMessage(callingEndpoint, msg);
    } else {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Failed to dispatch to provider " + endpoint.getURI() + " " +
            area + "::" + service + " : " + msg);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Not found in: " + providers);
    }
  }

  private void deliverBrokerMessage(MALEndpoint callingEndpoint, MALMessage msg) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].deliverBrokerMessage(" + msg.getHeader() + ')');
    if (broker != null) {
      broker.onMessage(callingEndpoint, msg);
    } else {
      // It may be a private broker
      deliverProviderMessage(callingEndpoint, msg);
    }
  }
  
  public void addProvider(Binding provider) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].addProvider(" + 
          provider.getService().getArea().getName() + "::" +
          provider.getService().getName() + ')');
    UShort area = provider.getService().getArea().getNumber();
    UShort service = provider.getService().getNumber();
    UOctet version = provider.getService().getArea().getVersion();
    ServiceKey key = new ServiceKey(area, service, version);
    synchronized (providers) {
      if (providers.get(key) != null) {
        throw CNESMALContext.createException("Already bound provider: " + key);
      } else {
        providers.put(key, provider);
      }
    }
  }
  
  public void removeProvider(Binding provider) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].removeProvider(" + 
          provider.getService().getArea().getName() + "::" +
          provider.getService().getName() + ')');
    UShort area = provider.getService().getArea().getNumber();
    UShort service = provider.getService().getNumber();
    UOctet version = provider.getService().getArea().getVersion();
    ServiceKey key = new ServiceKey(area, service, version);
    providers.remove(key);
  }
  
  public void addConsumer(Binding consumer) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].addConsumer(" + 
          consumer.getService().getArea().getName() + "::" +
          consumer.getService().getName() + ')');
    UShort area = consumer.getService().getArea().getNumber();
    UShort service = consumer.getService().getNumber();
    UOctet version = consumer.getService().getArea().getVersion();
    ServiceKey key = new ServiceKey(area, service, version);
    synchronized (consumers) {
      if (consumers.get(key) != null) {
        throw CNESMALContext.createException("Already bound consumer: " + key);
      } else {
        consumers.put(key, consumer);
      }
    }
  }
  
  public void removeConsumer(Binding consumer) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher[" + endpoint.getURI() + "].removeConsumer(" + 
          consumer.getService().getArea().getName() + "::" +
          consumer.getService().getName() + ')');
    UShort area = consumer.getService().getArea().getNumber();
    UShort service = consumer.getService().getNumber();
    UOctet version = consumer.getService().getArea().getVersion();
    ServiceKey key = new ServiceKey(area, service, version);
    consumers.remove(key);
  }
  
  public synchronized void setBroker(Binding brokerBinding) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher.setBroker(" + brokerBinding + ')');
    if (broker != null) {
      throw CNESMALContext.createException("Broker already set");
    } else {
      broker = brokerBinding;
    }
  }
  
  public synchronized void unsetBroker() throws MALException {
	if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher.unsetBroker()");
    broker = null;
  }

  public void onMessages(MALEndpoint callingEndpoint, MALMessage[] msgList) {
    for (int i = 0; i < msgList.length; i++) {
      onMessage(callingEndpoint, msgList[i]);
    }
  }

  public void onInternalError(MALEndpoint callingEndpoint, Throwable error) {
    if (logger.isLoggable(BasicLevel.ERROR))
      logger.log(BasicLevel.ERROR, "MessageDispatcher.onInternalError(" + error + ')');
  }
  
  public boolean isBound() {
    if (broker != null) return true;
    if (consumers.size() > 0) return true;
    if (providers.size() > 0) return true;
    return false;
  }
  
  static class ServiceKey {
    private UShort area;
    private UShort service;
    private UOctet version;
    
    public ServiceKey(UShort area, UShort service, UOctet version) {
      super();
      this.area = area;
      this.service = service;
      this.version = version;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((area == null) ? 0 : area.hashCode());
      result = prime * result + ((service == null) ? 0 : service.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ServiceKey other = (ServiceKey) obj;
      if (area == null) {
        if (other.area != null)
          return false;
      } else if (!area.equals(other.area))
        return false;
      if (service == null) {
        if (other.service != null)
          return false;
      } else if (!service.equals(other.service))
        return false;
      if (version == null) {
        if (other.version != null)
          return false;
      } else if (!version.equals(other.version))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "ServiceKey [area=" + area + ", service=" + service + ", version="
          + version + "]";
    }
    
  }
  
  private void deliverConsumerError(MALEndpoint callingEndpoint, 
      MALMessageHeader header, MALStandardError standardError, Map qosProperties) {
    UShort area = header.getServiceArea();
    UShort service = header.getService();
    UOctet version = header.getAreaVersion();
    Binding consumer = consumers.get(new ServiceKey(area, service, version));
    if (consumer != null) {
      consumer.onTransmitError(callingEndpoint, header, standardError, qosProperties);
    } else {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Failed to dispatch TRANSMIT ERROR to consumer " + endpoint.getURI() + " " +
            area + "::" + service + " : " + header);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Not found in: " + consumers);
    }
  }
  
  private void deliverProviderError(MALEndpoint callingEndpoint,
      MALMessageHeader header, MALStandardError standardError, Map qosProperties) {
    UShort area = header.getServiceArea();
    UShort service = header.getService();
    UOctet version = header.getAreaVersion();
    Binding provider = providers.get(new ServiceKey(area, service, version));
    if (provider != null) {
      provider.onTransmitError(callingEndpoint, header, standardError, qosProperties);
    } else {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Failed to dispatch TRANSMIT ERROR to provider " + endpoint.getURI() + " " +
            area + "::" + service + " : " + header);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Not found in: " + providers);
    }
  }
  
  private void deliverBrokerError(MALEndpoint callingEndpoint, 
      MALMessageHeader header, MALStandardError standardError, Map qosProperties) {
    if (broker != null) {
      broker.onTransmitError(callingEndpoint, header, standardError, qosProperties);
    } else {
      // It may be a private broker
      deliverProviderError(callingEndpoint, header, standardError, qosProperties);
    }
  }

  public void onTransmitError(MALEndpoint callingEndpoint,
      MALMessageHeader header, MALStandardError standardError, Map qosProperties) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MessageDispatcher.onTransmitError(" + header + ',' + standardError + ')');
    if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (header.getInteractionStage().getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
      case MALPubSubOperation._DEREGISTER_STAGE:
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
      case MALPubSubOperation._PUBLISH_DEREGISTER_STAGE:
        deliverBrokerError(callingEndpoint, header, standardError, qosProperties);
        break;
      case MALPubSubOperation._REGISTER_ACK_STAGE:
      case MALPubSubOperation._DEREGISTER_ACK_STAGE:
      case MALPubSubOperation._NOTIFY_STAGE:
        deliverConsumerError(callingEndpoint, header, standardError, qosProperties);
        break;
      case MALPubSubOperation._PUBLISH_STAGE:
        if (header.getIsErrorMessage().booleanValue()) {
          deliverProviderError(callingEndpoint, header, standardError, qosProperties);
        } else {
          deliverBrokerError(callingEndpoint, header, standardError, qosProperties);
        }
        break;
      case MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE:
      case MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE:
        deliverProviderError(callingEndpoint, header, standardError, qosProperties);
        break;
      default:
        throw new RuntimeException("Unknown Pub/Sub stage: " + header.getInteractionStage().getValue());
      }
    } else {
      if (header.getInteractionStage().getValue() == 1) {
        deliverProviderError(callingEndpoint, header, standardError, qosProperties);
      } else {
        deliverConsumerError(callingEndpoint, header, standardError, qosProperties);
      }
    }
  }
}
