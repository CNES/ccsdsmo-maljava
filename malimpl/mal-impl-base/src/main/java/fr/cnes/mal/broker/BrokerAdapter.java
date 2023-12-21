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
package fr.cnes.mal.broker;

import java.util.Arrays;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.broker.internal.Broker;
import fr.cnes.mal.broker.internal.BrokerNotification;
import fr.cnes.mal.broker.internal.BrokerPublication;
import fr.cnes.mal.broker.internal.PublisherContext;
import fr.cnes.mal.broker.internal.UnknownEntityException;
import fr.cnes.mal.broker.internal.UnknownPublisherException;

public class BrokerAdapter {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(BrokerAdapter.class.getName());
  
  private Broker broker;
  
  private Blob authenticationId;
  
  public BrokerAdapter(Broker broker, Blob authenticationId) {
    super();
    this.broker = broker;
    this.authenticationId = authenticationId;
  }

  public Blob getAuthenticationId() {
    return authenticationId;
  }

  // TODO SL le supplements de header est-il ok ? Faut-il ajouter un parametre specifique ?
  public synchronized void handleRegister(MALMessageHeader header,
      Subscription subscription, Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "TopicHandler.handleRegister("
          + header + ',' + subscription + ')');
    try {
      broker.register(
        header.getFrom(), 
        header.getTransactionId(), 
        header.getSupplements(),
        qosProperties, 
        subscription.getSubscriptionId(), 
        subscription,
        header.getServiceArea(), 
        header.getService(), 
        header.getOperation(),
        header.getServiceVersion()); // getAreaVersion()); TODO SL: go back to area
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      throw new MALException(exc.toString(), exc);
    }
  }
  
  public synchronized void handlePublishRegister(MALMessageHeader header,
                                                 IdentifierList subKeys,
                                                 AttributeTypeList keyTypes)
          throws MALException, MALInteractionException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "TopicHandler.handlePublishRegister(" + 
          header + ',' + subKeys + ')');
    if (header.getSupplements() == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "TopicHandler.handlePublishRegister null supplements, " +
      Arrays.toString(new Exception("stack trace").getStackTrace()));
    }
    PublisherContext publisherContext = broker.registerPublisher(
        header.getFrom(),
        header.getTransactionId(),
        null, // header.getDomain(),  TODO SL: impact du domain dans le matching
        subKeys,
        keyTypes,
        header.getServiceArea(),
        header.getService(),
        header.getOperation(),
        header.getServiceVersion()); // getAreaVersion()); TODO SL: go back to area
    // Update the QoS and priority levels so that the ack
    // QoS and priority header fields are assigned with the FIRST
    // PUBLISH REGISTER values.
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "publisherContext=" + publisherContext);
    // header.setQoSlevel(publisherContext.getQos());
    // header.setPriority(publisherContext.getPriority());
  }
  
  public synchronized void handlePublishDeregister(MALMessageHeader header) throws MALException {
    PublisherContext publisherContext = broker.deregisterPublisher(
      header.getFrom(),
      null, // header.getDomain(),
      null, // header.getNetworkZone(),
      null, // header.getSession(),
      null, // header.getSessionName(),
      header.getServiceArea(),
      header.getService(),
      header.getOperation(),
      header.getServiceVersion()); // getAreaVersion()); TODO SL: go back to area
    if (publisherContext != null) {
      // Update the QoS and priority levels so that the ack
      // QoS and priority header fields are assigned with the FIRST
      // PUBLISH REGISTER values.
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "publisherContext=" + publisherContext);
      // header.setQoSlevel(publisherContext.getQos());
      // header.setPriority(publisherContext.getPriority());
    }
  }
  
  public synchronized void handleDeregister(MALMessageHeader header,
      IdentifierList subscriptionIds) throws MALException {
    try {
      broker.deregister(
        header.getFrom(),
        subscriptionIds,
        header.getServiceArea(),
        header.getService(),
        header.getOperation(),
        header.getServiceVersion()); // getAreaVersion()); TODO SL: go back to area
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw e;
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      throw new MALException(exc.toString(), exc);
    }
  }
  
  public synchronized void deregister(MALMessageHeader header) throws MALException {
    try {
      broker.deregister(
        header.getTo(),
        header.getServiceArea(),
        header.getService(),
        header.getOperation(),
        header.getServiceVersion()); // getAreaVersion()); TODO SL: go back to area
    } catch (MALException e) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", e);
      throw e;
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      throw new MALException(exc.toString(), exc);
    }
  }

  public synchronized void unregisterMBeans() {
    broker.unregisterMBeans();
  }
  
  public synchronized BrokerNotification[] publish(BrokerPublication publication)
      throws UnknownEntityException, UnknownPublisherException {
    return broker.publish(publication);
  }
}
