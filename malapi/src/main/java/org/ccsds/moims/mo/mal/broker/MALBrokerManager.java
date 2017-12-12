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
package org.ccsds.moims.mo.mal.broker;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;

/**
 * A {@code MALBrokerManager} enables a MAL client
 * to manage brokers.
 */
public interface MALBrokerManager {
  
  /**
   * Creates a broker.
   * @return a newly created broker
   * @throws MALException if an error occurs
   */
  public MALBroker createBroker() throws MALException;
  
  /**
   * Creates a broker with a {@code MALBrokerHandler}.
   * @param handler broker handler responsible for handling the Pub/Sub interactions
   * on the broker side
   * @return a newly created broker
   * @throws MALException if an error occurs
   */
  public MALBroker createBroker(MALBrokerHandler handler) throws MALException;
  
  /**
   * Creates a broker binding.
   * @param broker MAL level broker to be bound
   * @param localName name of the private {@code MALEndpoint} to be created and used by the broker binding 
   * @param protocol name of the protocol used to bind the broker
   * @param authenticationId authentication identifier that should be used by the broker
   * @param expectedQos QoS levels the broker assumes it can rely on
   * @param priorityLevelNumber number of priorities the broker uses
   * @param qosProperties default QoS properties used by the broker to send messages
   * @return a newly created broker binding
   * @throws IllegalArgumentException  if the parameters ‘protocol’ or 
   * ‘authenticationId’ or ‘expectedQos’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALBrokerBinding createBrokerBinding(
      MALBroker broker,
      String localName, 
      String protocol,
      Blob authenticationId,
      QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, 
      Map qosProperties) throws IllegalArgumentException, MALException;
  
  /**
   * Creates a broker binding.
   * @param broker MAL level broker to be bound
   * @param endpoint shared {@code MALEndpoint} to be used by the broker
   * @param authenticationId authentication identifier that should be used by the broker
   * @param expectedQos QoS levels the broker assumes it can rely on
   * @param priorityLevelNumber number of priorities the broker uses
   * @param qosProperties default QoS properties used by the broker to send messages
   * @return a newly created broker binding
   * @throws IllegalArgumentException  if the parameters ‘endpoint’ or 
   * ‘authenticationId’ or ‘expectedQos’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALBrokerBinding createBrokerBinding(
      MALBroker broker,
      MALEndpoint endpoint,
      Blob authenticationId,
      QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, 
      Map qosProperties) throws MALException;
  
  /**
   * Releases the resources owned by the {@code MALBrokerManager}.
   * @throws MALException if an error occurs
   */
  public void close() throws MALException;

}
