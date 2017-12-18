/*******************************************************************************
 * Copyright or © or Copr. CNES
 *
 * This software is a computer program whose purpose is to provide a 
 * framework for the CCSDS Mission Operations services.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package fr.cnes.ccsds.mo.transport.amqp;

import java.io.IOException;

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
  private String subscriptionQueueName;
  private String consumerTag;
  private boolean activated;
  private MALAMQPEndPoint endpoint;

  public SubscriptionContext(Long transactionId, 
      Identifier networkZone,
      QoSLevel qos,
      Subscription subscription, MALAMQPEndPoint endpoint) {
    this.transactionId =  transactionId;
    this.networkZone = networkZone;
    this.qos = qos;
    this.subscription = subscription;
    this.endpoint = endpoint;
    subscriptionQueueName = null;
    consumerTag = null;
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

  /**
   * @return the subscriptionQueueName
   */
  public String getSubscriptionQueueName() {
    return subscriptionQueueName;
  }

  /**
   * @param subscriptionQueueName the subscriptionQueueName to set
   */
  public void setSubscriptionQueueName(String subscriptionQueueName) {
    this.subscriptionQueueName = subscriptionQueueName;
  }

  /**
   * @return the consumerTag
   */
  public String getConsumerTag() {
    return consumerTag;
  }

  /**
   * @param consumerTag the consumerTag to set
   */
  public void setConsumerTag(String consumerTag) {
    this.consumerTag = consumerTag;
  }
  
  public synchronized void activate(MALMessageListener listener) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionContext.activate(" + listener + ')');
    if (activated) return;
    boolean nolocal = true;
    boolean exclusiveConsumer = true;
    SubscriptionConsumer subscriptionConsumer = new SubscriptionConsumer(
      listener, this, endpoint);
    String clientConsumerTag = "";
    try {
      consumerTag = endpoint.getChannel().basicConsume(
        subscriptionQueueName, endpoint.getNoAck(), 
        clientConsumerTag, nolocal, exclusiveConsumer, subscriptionConsumer);
      activated = true;
    } catch (IOException e) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", e);
      throw MALAMQPHelper.createMALException(e.toString());
    }
  }

  public synchronized void deactivate() throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SubscriptionContext.deactivate()");
    if (! activated) return;
    if (consumerTag != null) {
      endpoint.getChannel().basicCancel(consumerTag);
    }
    activated = false;
  }

}
