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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.broker.MALBrokerHandler;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALDeregisterBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.ccsds.moims.mo.mal.transport.MALPublishRegisterBody;
import org.ccsds.moims.mo.mal.transport.MALRegisterBody;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.broker.internal.Broker;
import fr.cnes.mal.broker.internal.BrokerNotification;
import fr.cnes.mal.broker.internal.BrokerPublication;
import fr.cnes.mal.broker.internal.BrokerSubscriptionUpdate;
import fr.cnes.mal.broker.internal.UnknownEntityException;
import fr.cnes.mal.broker.internal.UnknownPublisherException;
import fr.cnes.mal.broker.internal.UpdateCheckReport;

public class CNESMALBrokerHandler implements MALBrokerHandler, MALTransmitErrorListener {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALBrokerHandler.class.getName());
  
  private Hashtable<Identifier, BrokerContext> brokerContexts;
  
  private Blob authenticationId;
  
  private String jmxName;
  
  public CNESMALBrokerHandler(
      Blob authenticationId,
      String jmxName) {
    this.authenticationId = authenticationId;
    this.jmxName = jmxName;
    brokerContexts = new Hashtable<Identifier, CNESMALBrokerHandler.BrokerContext>();
  }
  
  private BrokerContext getBrokerContext(Identifier brokerUri) throws MALException {
    BrokerContext brokerContext = (BrokerContext) brokerContexts.get(brokerUri);
    if (brokerContext == null) {
      throw new MALException("Unknown broker URI: " + brokerUri);
    } else {
      return brokerContext;
    }
  }
  
  private String getMBeanName(URI uri) {
    String escapedUri = uri.getValue().replace(':', '-');
    escapedUri = escapedUri.replace('=', '-');
    return jmxName + ",brokerBinding=BrokerBinding-" + escapedUri;
  }

  public void malInitialize(MALBrokerBinding brokerBinding) {
    BrokerContext brokerContext = (BrokerContext) brokerContexts.get(brokerBinding.getURI());
    if (brokerContext == null) {
      Broker broker = new Broker(getMBeanName(brokerBinding.getURI()));
      try {
        brokerBinding.setTransmitErrorListener(this);
      } catch (MALException exc) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "", exc);
      }
      // the binding should distinguish the name and the URI
      Identifier destinationId = null;
      if (brokerBinding instanceof CNESMALBrokerBinding) {
        destinationId = ((CNESMALBrokerBinding) brokerBinding).getDestinationId();
      } else {
        destinationId = new Identifier(brokerBinding.getURI().getValue());
      }
      brokerContexts.put(destinationId, 
          new BrokerContext(brokerBinding, new BrokerAdapter(broker, authenticationId)));
    }
  }

  public void handleRegister(MALInteraction interaction, MALRegisterBody body)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerHandler.register(" + interaction + ',' + 
          body + ')');
    Enumeration brokerContextEnum = brokerContexts.elements();
    while (brokerContextEnum.hasMoreElements()) {
      BrokerContext ctx = (BrokerContext) brokerContextEnum.nextElement();
      Subscription subscription = body.getSubscription();
      BrokerAdapter brokerAdapter = ctx.getBrokerAdapter();
      brokerAdapter.handleRegister(interaction.getMessageHeader(), subscription, interaction.getQoSProperties());
    }
  }

  public void handlePublishRegister(MALInteraction interaction,
      MALPublishRegisterBody body) throws MALException, MALInteractionException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALBrokerHandler.publishRegister(" + interaction + ',' + 
          body + ')');
    Enumeration brokerContextEnum = brokerContexts.elements();
    while (brokerContextEnum.hasMoreElements()) {
      BrokerContext ctx = (BrokerContext) brokerContextEnum.nextElement();
      IdentifierList subKeys = body.getSubscriptionKeyNames();
      AttributeTypeList keyTypes = body.getSubscriptionKeyTypes();
      BrokerAdapter brokerAdapter = ctx.getBrokerAdapter();
      brokerAdapter.handlePublishRegister(interaction.getMessageHeader(), subKeys, keyTypes);
    }
  }

  public void handlePublish(MALInteraction interaction, MALPublishBody body) 
      throws MALInteractionException, MALException {
    MALMessageHeader header = interaction.getMessageHeader();
    BrokerContext brokerContext = getBrokerContext(interaction.getMessageHeader().getTo());
    
    BrokerPublication publication = new BrokerPublication(
      header.getFrom(),
      body.getUpdateHeader(),
      body.getUpdateObjects(),
      header.getServiceArea(),
      header.getService(),
      header.getOperation(),
      header.getServiceVersion());
    
    Enumeration brokerContextEnum = brokerContexts.elements();
    while (brokerContextEnum.hasMoreElements()) {
      BrokerContext ctx = (BrokerContext) brokerContextEnum.nextElement();

      BrokerNotification[] notifications;
      try {
        notifications = ctx.getBrokerAdapter().publish(publication);
      } catch (UnknownPublisherException upe) {
        MOErrorException error = new MOErrorException(MALHelper.INTERNAL_ERROR_NUMBER, null);
        // TODO SL change to new API v2
        ((CNESMALBrokerBinding)brokerContext.getBinding()).sendPublishError(
            interaction.getOperation(), 
            header.getFrom(), 
            header.getTransactionId(), 
            // TODO SL missing supplements
            new NamedValueList(),
            interaction.getQoSProperties(),
            error);
        return;
      } catch (UnknownEntityException uee) {
        // TODO SL pas sur que cela existe encore
        UpdateCheckReport report = uee.getReport();
        ((CNESMALBrokerBinding)brokerContext.getBinding()).sendPublishError(
            interaction.getOperation(),
            header.getFrom(), 
            header.getTransactionId(), // TODO SL why not header.getTransactionId()?
            // TODO SL missing supplements
            new NamedValueList(),
            interaction.getQoSProperties(),
            new MOErrorException(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        return;
      }
      
      if (notifications == null || notifications.length == 0)
        return;
      
      for (int i = 0; i < notifications.length; i++) {
        List<BrokerSubscriptionUpdate> subscriptionUpdateList = notifications[i].getSubscriptionUpdateList();
        for (BrokerSubscriptionUpdate subscriptionUpdate : subscriptionUpdateList) {
          ((CNESMALBrokerBinding)ctx.getBinding()).sendNotify(
            interaction.getOperation(),
            notifications[i].getSubscriberUri(),
            notifications[i].getTransactionId(),
            new NamedValueList(),
            // notifications[i].getSupplements(),
            notifications[i].getQosProperties(),
            subscriptionUpdate.getSubscriptionId(),
            subscriptionUpdate.getUpdateHeader(),
            subscriptionUpdate.getUpdateObjects());
        }
      }
    }
  }

  public void handlePublishDeregister(MALInteraction interaction)
      throws MALException {
    Enumeration brokerContextEnum = brokerContexts.elements();
    while (brokerContextEnum.hasMoreElements()) {
      BrokerContext ctx = (BrokerContext) brokerContextEnum.nextElement();
      BrokerAdapter brokerAdapter = ctx.getBrokerAdapter();
      brokerAdapter.handlePublishDeregister(interaction.getMessageHeader());
    }
  }

  public void handleDeregister(MALInteraction interaction,
      MALDeregisterBody body) throws MALException {
    IdentifierList subscriptionIds = body.getSubscriptionIds();
    BrokerAdapter brokerAdapter = getBrokerContext(interaction.getMessageHeader().getTo()).getBrokerAdapter();
    brokerAdapter.handleDeregister(interaction.getMessageHeader(), subscriptionIds);
  }

  public void malFinalize(MALBrokerBinding brokerBinding) {
    BrokerContext brokerCtx = brokerContexts.remove(brokerBinding.getURI());
    brokerCtx.getBrokerAdapter().unregisterMBeans();
  }
  
  static class BrokerContext {
    private MALBrokerBinding binding;
    private BrokerAdapter brokerAdapter;
    
    BrokerContext(MALBrokerBinding binding, BrokerAdapter brokerAdapter) {
      this.binding = binding;
      this.brokerAdapter = brokerAdapter;
    }

    public MALBrokerBinding getBinding() {
      return binding;
    }

    public BrokerAdapter getBrokerAdapter() {
      return brokerAdapter;
    }
  }

  public void onTransmitError(MALEndpoint callingEndpoint,
      MALMessageHeader header, MOErrorException standardError, Map qosProperties) {
    if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX &&
        header.getInteractionStage().getValue() == MALPubSubOperation._NOTIFY_STAGE) {
      if (standardError.getErrorNumber().getValue() == MALHelper._DESTINATION_UNKNOWN_ERROR_NUMBER) {
        // A message could not be delivered to a subscriber
        // because it is unknown
        // Deregister the subscriber
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Deregister unknown subscriber: " + header);
        try {
          deregister(header);
        } catch (MALException e) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "", e);
        }
      } else {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "Transmit error: " + header + " - " + standardError);
      }
    } else {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "Transmit error: " + header + " - " + standardError);
    }
  }
  
  private void deregister(MALMessageHeader header) throws MALException {
    Enumeration brokerContextEnum = brokerContexts.elements();
    while (brokerContextEnum.hasMoreElements()) {
      BrokerContext ctx = (BrokerContext) brokerContextEnum.nextElement();
      ctx.getBrokerAdapter().deregister(header);
    }
  }
}
