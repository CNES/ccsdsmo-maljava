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
package org.ccsds.moims.mo.mal.transport;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A {@code MALTransport} enables a MAL instance
 * to send and receive MAL messages through a particular
 * transport protocol.
 */
public interface MALTransport {

  /**
   * Creates a new {@code MALEndpoint}.
   * @param localName name of the {@code MALEndpoint}
   * @param qosProperties QoS properties to be used
   * @return a newly created {@code MALEndpoint}
   * @throws MALException if an error occurs
   */
  public MALEndpoint createEndpoint(
      String localName, Map qosProperties) throws MALException;
  
  /**
   * Returns the {@code MALEndpoint} which name is specified.
   * @param localName the name of the {@code MALEndpoint}
   * @return the {@code MALEndpoint} which name is specified
   * @throws MALException if an error occurs
   */
  public MALEndpoint getEndpoint(String localName) throws MALException;
  
  /**
   * Returns the {@code MALEndpoint} which URI is specified.
   * @param uri the URI of the {@code MALEndpoint}
   * @return the URI which name is specified
   * @throws MALException if an error occurs
   */
  public MALEndpoint getEndpoint(URI uri) throws MALException;
  
  /**
   * Deletes the {@code MALEndpoint} which name is specified.
   * @param localName the name of the {@code MALEndpoint}
   * @throws MALException if an error occurs
   */
  public void deleteEndpoint(String localName) throws MALException;
  
  /**
   * Checks that the specified QoS level is supported.
   * @param qos the QoS level to check
   * @return {@code true} if the QoS level is supported
   */
  public boolean isSupportedQoSLevel(QoSLevel qos);
  
  /**
   * Checks that the specified interaction type is supported.
   * @param type the interaction type to check
   * @return {@code true} if the interaction type is supported
   */
  public boolean isSupportedInteractionType(InteractionType type);
  
  /**
   * Creates a transport level broker using a private {@code MALEndpoint}.
   * @param localName name of the private MALEndpoint to be created and used by the broker
   * @param authenticationId authentication identifier that should be used by the broker
   * @param expectedQos QoS levels the broker assumes it can rely on
   * @param priorityLevelNumber number of priorities the broker uses
   * @param qosProperties default QoS properties used by the broker to send messages
   * @return a newly created transport level broker
   * @throws MALException if an error occurs
   */
  public MALBrokerBinding createBroker(
      String localName,
      Blob authenticationId,
      QoSLevel[] expectedQos,
      UInteger priorityLevelNumber,
      Map qosProperties) throws MALException;
  
  /**
   * Creates a transport level broker using a shared {@code MALEndpoint}.
   * @param endpoint shared {@code MALEndpoint} to be used by the broker
   * @param authenticationId authentication identifier that should be used by the broker
   * @param expectedQos QoS levels the broker assumes it can rely on
   * @param priorityLevelNumber number of priorities the broker uses
   * @param qosProperties default QoS properties used by the broker to send messages
   * @return a newly created transport level broker
   * @throws MALException if an error occurs
   */
  public MALBrokerBinding createBroker(
      MALEndpoint endpoint,
      Blob authenticationId,
      QoSLevel[] expectedQos,
      UInteger priorityLevelNumber,
      Map qosProperties) throws MALException;
  
  /**
   * Releases all the resources owned by this {@code MALEndpoint} .
   * @throws MALException if an error occurs
   */
  public void close() throws MALException;
}
