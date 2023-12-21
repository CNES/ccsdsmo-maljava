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
package fr.cnes.mal.provider;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageDispatcher;
import fr.cnes.mal.BindingManager;
import fr.dyade.aaa.util.management.MXWrapper;

public class CNESMALProviderManager extends BindingManager<CNESMALProvider> implements MALProviderManager, CNESMALProviderManagerMBean {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
    .getLogger(CNESMALProviderManager.class.getName());
  
  public CNESMALProviderManager(int threadPoolSize, CNESMALContext mal, String name, String jmxName) {
    super(mal, threadPoolSize, name, jmxName);
  }

  public synchronized MALProvider createProvider(
      String localName, 
      String protocol,
      MALService service, 
      Blob authenticationId,
      MALInteractionHandler handler, QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber, 
      Map defaultQoSProperties,
      Boolean isPublisher, 
      Identifier sharedBrokerUri,
      NamedValueList supplements) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProviderManager.createProvider(" +
		  localName + ',' + 
		  defaultQoSProperties + ',' + 
		  protocol + ',' + 
		  sharedBrokerUri + ')');
    checkClosed();
    if (protocol == null) throw new IllegalArgumentException("Null protocol");
    if (service == null) throw new IllegalArgumentException("Null service");
    if (authenticationId == null) throw new IllegalArgumentException("Null authenticationId");
    if (handler == null) throw new IllegalArgumentException("Null handler");
    if (expectedQos == null) throw new IllegalArgumentException("Null expectedQos");
    if (priorityLevelNumber == null) throw new IllegalArgumentException("Null priorityLevelNumber");
    if (supplements == null) supplements = new NamedValueList();
    // Currently ignore the supplements for the provider, ping back the supplements from the consumer
    
    MALTransport transport = getMalContext().getTransport(protocol);
    MALEndpoint ep = transport.createEndpoint(localName, defaultQoSProperties, supplements);
    
    CNESMALProvider provider = doCreateProvider(ep, service, authenticationId, 
        handler, expectedQos, priorityLevelNumber,
        defaultQoSProperties, isPublisher, supplements, sharedBrokerUri, null);
    ep.setMessageListener(provider);
    ep.startMessageDelivery();
    return provider;
  }
  
  public synchronized MALProvider createProvider(MALEndpoint endPoint, MALService service,
      Blob authenticationId, MALInteractionHandler handler,
      QoSLevel[] expectedQos, UInteger priorityLevelNumber,
      Map defaultQoSProperties, Boolean isPublisher, Identifier sharedBrokerUri,
      NamedValueList supplements)
      throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProviderManager.createProvider(" +
          endPoint.getURI() + ',' + 
          service.getName() + ',' + 
          isPublisher + ',' + 
          sharedBrokerUri + ')');
    checkClosed();
    if (endPoint == null) throw new IllegalArgumentException("Null endPoint");
    if (service == null) throw new IllegalArgumentException("Null service");
    if (authenticationId == null) throw new IllegalArgumentException("Null authenticationId");
    if (handler == null) throw new IllegalArgumentException("Null handler");
    if (expectedQos == null) throw new IllegalArgumentException("Null expectedQos");
    if (priorityLevelNumber == null) throw new IllegalArgumentException("Null priorityLevelNumber");
    if (isPublisher == null) throw new IllegalArgumentException("Null isPublisher");
    if (supplements == null) supplements = new NamedValueList();

    MessageDispatcher messageDispatcher = getMalContext().getMessageDispatcher(endPoint);
    CNESMALProvider provider = doCreateProvider(endPoint, service, authenticationId, 
        handler, expectedQos, priorityLevelNumber,
        defaultQoSProperties, isPublisher, supplements, sharedBrokerUri, messageDispatcher);
    messageDispatcher.addProvider(provider);
    return provider;
  }
  
  private CNESMALProvider doCreateProvider(
      MALEndpoint ep,
      MALService service, 
      Blob authenticationId,
      MALInteractionHandler handler, QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber, 
      Map defaultQoSProperties,
      Boolean isPublisher, 
      NamedValueList supplements,
      Identifier sharedBrokerUri,
      MessageDispatcher messageDispatcher) throws MALException {
    String mBeanName = getMBeanName(ep.getURI().getValue(), service);
    // added for commit 0042213
    MALContextFactory.getElementsRegistry().loadServiceAndAreaElements(service);
    CNESMALProvider provider = new CNESMALProvider(
        this, ep, service, handler, 
        authenticationId,
        defaultQoSProperties,
        supplements,
        isPublisher,
        sharedBrokerUri,
        mBeanName, messageDispatcher);
    try {
      MXWrapper.registerMBean(provider, mBeanName);
    } catch (Exception e) {
      logger.log(BasicLevel.ERROR, getClass().getName() + " jmx failed", e);
    }
    addBinding(provider);
    if (handler != null) {
      handler.malInitialize(provider);
    }
    return provider;
  }

  private String getMBeanName(String uri, MALService service) {
    String escapedUri = uri.replace(':', '-');
    escapedUri = escapedUri.replace('=', '-');
    StringBuffer buf = new StringBuffer();
    String areaName = service.getAreaNumber().toString();
    MALArea area = MALContextFactory.lookupArea(service.getAreaNumber(), service.getServiceVersion());
    if (area != null)
      areaName = area.getName().getValue();
    buf.append(getJmxName());
    buf.append(",service=");
    buf.append(areaName);
    buf.append("-v");
    buf.append(service.getServiceVersion());
    buf.append("-");
    buf.append(service.getName());
    buf.append(",provider=Provider-");
    buf.append(escapedUri);
    return buf.toString();
  }

  public int getProviderCount() {
    return getBindingCount();
  }
  
  @Override
  protected void finalizeManager() throws MALException {
    getMalContext().closeProviderManager(this);
  }
  
  @Override
  protected void doClose() throws MALException {
    // Nothing to do
  }

  // Implement MALProviderManager with from/to as URIs
  public MALProvider createProvider(String localName, String protocol,
                                    MALService service, Blob authenticationId,
                                    MALInteractionHandler handler,
                                    QoSLevel[] expectedQos,
                                    UInteger priorityLevelNumber,
                                    Map defaultQoSProperties,
                                    Boolean isPublisher, URI sharedBrokerUri,
                                    NamedValueList supplements) throws IllegalArgumentException,
                                                                MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProviderManager.createProvider-3");
    Identifier sharedBrokerDestinationId = null;
    if (sharedBrokerUri != null)
      sharedBrokerDestinationId = new Identifier(sharedBrokerUri.getValue());
    return createProvider(localName, protocol,
                          service, authenticationId,
                          handler,
                          expectedQos,
                          priorityLevelNumber,
                          defaultQoSProperties,
                          isPublisher,
                          sharedBrokerDestinationId,
                          supplements);
  }

  public MALProvider createProvider(MALEndpoint endpoint, MALService service,
                                    Blob authenticationId,
                                    MALInteractionHandler handler,
                                    QoSLevel[] expectedQos,
                                    UInteger priorityLevelNumber,
                                    Map defaultQoSProperties,
                                    Boolean isPublisher, URI sharedBrokerUri,
                                    NamedValueList supplements) throws IllegalArgumentException,
                                                                MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALProviderManager.createProvider-4");
    Identifier sharedBrokerDestinationId = null;
    if (sharedBrokerUri != null)
      sharedBrokerDestinationId = new Identifier(sharedBrokerUri.getValue());
    return createProvider(endpoint, service,
                          authenticationId,
                          handler,
                          expectedQos,
                          priorityLevelNumber,
                          defaultQoSProperties,
                          isPublisher,
                          sharedBrokerDestinationId,
                          supplements);
  }

}
