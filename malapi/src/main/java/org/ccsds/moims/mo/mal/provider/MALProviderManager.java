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
package org.ccsds.moims.mo.mal.provider;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;

/**
 * A {@code MALProviderManager} enables a MAL client
 * to create and manage service providers.
 */
public interface MALProviderManager {

  /**
   * Creates a service provider.
   * @param localName name of the private {@code MALEndpoint} to be created and used by the provider
   * @param protocol name of the protocol used to bind the provider
   * @param service description of the provided service
   * @param authenticationId authentication identifier to be used by the provider
   * @param handler interaction handler
   * @param expectedQos QoS levels the provider can rely on
   * @param priorityLevelNumber number of priorities the provider uses
   * @param defaultQoSProperties default QoS properties used by the provider 
   * to send messages back to the consumer and to publish updates to a shared broker
   * @param isPublisher specifies whether the provider is a PUBLISH-SUBSCRIBE publisher or not
   * @param sharedBrokerUri URI of the shared broker to be used
   * @return a newly created provider
   * @throws MALException if the manager has been closed or if an internal error occurs
   */
  public MALProvider createProvider(
      String localName,
      String protocol,
      MALService service,
      Blob authenticationId,
      MALInteractionHandler handler, 
      QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber,
      Map defaultQoSProperties,
      Boolean isPublisher,
      URI sharedBrokerUri) throws MALException;
  
  /**
   * Creates a service provider.
   * @param endpoint shared {@code MALEndpoint} to be used by the provider
   * @param service description of the provided service
   * @param authenticationId authentication identifier to be used by the provider
   * @param handler interaction handler
   * @param expectedQos QoS levels the provider can rely on
   * @param priorityLevelNumber number of priorities the provider uses
   * @param defaultQoSProperties default QoS properties used by the provider 
   * to send messages back to the consumer and to publish updates to a shared broker
   * @param isPublisher specifies whether the provider is a PUBLISH-SUBSCRIBE publisher or not
   * @param sharedBrokerUri URI of the shared broker to be used
   * @return a newly created provider
   * @throws MALException if the manager has been closed or if an internal error occurs
   */
  public MALProvider createProvider(
      MALEndpoint endpoint,
      MALService service,
      Blob authenticationId,
      MALInteractionHandler handler, 
      QoSLevel[] expectedQos, 
      UInteger priorityLevelNumber,
      Map defaultQoSProperties,
      Boolean isPublisher,
      URI sharedBrokerUri) throws MALException;
  
  /**
   * Closes the provider manager.
   * @throws MALException if an internal error occurs
   */
  public void close() throws MALException;
  
}
