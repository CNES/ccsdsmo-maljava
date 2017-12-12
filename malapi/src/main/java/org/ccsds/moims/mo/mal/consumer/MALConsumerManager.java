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
package org.ccsds.moims.mo.mal.consumer;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;

/**
 * A {@code MALConsumerManager} enables a MAL client
 * to create and manage service consumers.
 */
public interface MALConsumerManager {
  
  /**
   * Creates a consumer.
   * @param localName name of the private {@code MALEndpoint} to be created and used by the consumer
   * @param uriTo URI of the service provider the consumer interacts with
   * @param brokerUri URI of the broker used by the service provider to publish updates
   * @param service definition of the consumed service
   * @param authenticationId authentication identifier used by the consumer during all the interactions with the service provider
   * @param domain domain the service provider belongs to
   * @param networkZone network zone the provider belongs to
   * @param sessionType session type of the service
   * @param sessionName name of the session
   * @param qosLevel QoS level required by the consumer for all the interactions with the provider
   * @param qosProps QoS properties that are needed to configure the QoS level
   * @param priority message priority required by the consumer for all the interactions with the provider
   * @return a newly created consumer
   * @throws MALException if the manager has been closed or if an internal error occurs
   */
  public MALConsumer createConsumer(
      String localName,
      URI uriTo,
      URI brokerUri,
      MALService service,
      Blob authenticationId,
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel qosLevel,
      Map qosProps, 
      UInteger priority) throws MALException;
  
  /**
   * Creates a consumer.
   * @param endpoint shared {@code MALEndpoint} to be used by the consumer
   * @param uriTo URI of the service provider the consumer interacts with
   * @param brokerUri URI of the broker used by the service provider to publish updates
   * @param service definition of the consumed service
   * @param authenticationId authentication identifier used by the consumer during all the interactions with the service provider
   * @param domain domain the service provider belongs to
   * @param networkZone network zone the provider belongs to
   * @param sessionType session type of the service
   * @param sessionName name of the session
   * @param qosLevel QoS level required by the consumer for all the interactions with the provider
   * @param qosProps QoS properties that are needed to configure the QoS level
   * @param priority message priority required by the consumer for all the interactions with the provider
   * @return a newly created consumer
   * @throws MALException if the manager has been closed or if an internal error occurs
   */
  public MALConsumer createConsumer(
      MALEndpoint endpoint,
      URI uriTo,
      URI brokerUri,
      MALService service,
      Blob authenticationId,
      IdentifierList domain,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel qosLevel,
      Map qosProps, 
      UInteger priority) throws MALException;
  
  /**
   * Closes the consumer manager.
   * @throws MALException if an internal error occurs
   */
  public void close() throws MALException;

}
