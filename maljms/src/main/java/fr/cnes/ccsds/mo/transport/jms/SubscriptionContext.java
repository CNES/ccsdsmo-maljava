package fr.cnes.ccsds.mo.transport.jms;

import javax.jms.MessageConsumer;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class SubscriptionContext {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(SubscriptionContext.class.getName());
  
  private Long transactionId;
  private Identifier networkZone;
  private QoSLevel qos;
  private Subscription subscription;

  private String subscriptionName;

  private boolean activated;
  private MALJMSEndpoint endpoint;
  
  private MessageConsumer jmsConsumer;

  public SubscriptionContext(Long transactionId, 
      Identifier networkZone,
      QoSLevel qos,
      Subscription subscription, MALJMSEndpoint endpoint) {
    this.transactionId =  transactionId;
    this.networkZone = networkZone;
    this.qos = qos;
    this.subscription = subscription;
    this.endpoint = endpoint;
    subscriptionName = null;
    activated = false;
  }

  /**
   * @return the transactionId
   */
  public Long getTransactionId() {
    return transactionId;
  }

  /**
   * @param transactionId the transactionId to set
   */
  public void setTransactionId(Long transactionId) {
    this.transactionId = transactionId;
  }

  public Identifier getNetworkZone() {
    return networkZone;
  }

  public QoSLevel getQos() {
    return qos;
  }

  /**
   * @return the subscription
   */
  public Subscription getSubscription() {
    return subscription;
  }

  /**
   * @param subscription the subscription to set
   */
  public void setSubscription(Subscription subscription) {
    this.subscription = subscription;
  }
  
  public String getSubscriptionName() {
    return subscriptionName;
  }

  public void setSubscriptionName(String subscriptionName) {
    this.subscriptionName = subscriptionName;
  }
  
  public MessageConsumer getJmsConsumer() {
    return jmsConsumer;
  }

  public void setJmsConsumer(MessageConsumer jmsConsumer) {
    this.jmsConsumer = jmsConsumer;
  }

  public synchronized void activate(MALMessageListener listener) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionContext.activate(" + listener + ')');
    if (activated) return;
    SubscriptionConsumer subscriptionConsumer = new SubscriptionConsumer(
      listener, this, endpoint);
    try {
      jmsConsumer.setMessageListener(subscriptionConsumer);
      activated = true;
    } catch (Exception e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALJMSHelper.createMALException(e.toString());
    }
  }

  public synchronized void deactivate() throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionContext.deactivate()");
    if (! activated) return;
    if (jmsConsumer != null) {
      jmsConsumer.setMessageListener(null);
      jmsConsumer.close();
    }
    activated = false;
  }
  
  public void delete() throws Exception {
    if (subscriptionName != null) {
      endpoint.getSession().unsubscribe(subscriptionName);
    }
  }

}
