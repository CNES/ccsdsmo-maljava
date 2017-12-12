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
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;

/**
 * A {@code MALProvider} represents the execution context 
 * of a service provider for a given URI.
 */
public interface MALProvider {
  
  /**
   * Returns the URI of the provider.
   * @return the URI of the provider.
   */
  public URI getURI();
  
  /**
   * Indicates whether the provider is a publisher or not.
   * @return true if the provider is a publisher
   */
  public boolean isPublisher();
  
  /**
   * Returns the URI of the broker used by the provider 
   * if the provider is a publisher. Otherwise returns {@code null}.
   * @return the URI of the broker or null
   */
  public URI getBrokerURI();
  
  /**
   * Returns the broker authentication id if the broker is private.
   * Otherwise returns {@code null}.
   * @return the URI of the broker or null
   */
  public Blob getBrokerAuthenticationId();
  
  /**
   * Creates a {@code MALPublisher}.
   * @param op PUBLISH-SUBSCRIBE operation
   * @param domain domain of the PUBLISH messages 
   * @param networkZone network zone of the PUBLISH messages
   * @param sessionType session type of the PUBLISH messages
   * @param sessionName session name of the PUBLISH messages
   * @param publishQos QoS level of the PUBLISH messages
   * @param publishQosProps QoS properties of the PUBLISH messages
   * @param publishPriority priority of the PUBLISH messages
   * @return a newly created publisher
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘domain’ or ‘networkZone’ or 
   * ‘sessionType’ or ‘sessionName’ or ‘remotePublisherQos’ or ‘remotePublisherPriority’ are NULL 
   * @throws MALException if the MALProvider is not a publisher or is closed or if an internal error occurs
   */
  public MALPublisher createPublisher(MALPubSubOperation op, IdentifierList domain,
    Identifier networkZone, SessionType sessionType, Identifier sessionName,
    QoSLevel publishQos, Map publishQosProps, UInteger publishPriority)
    throws IllegalArgumentException, MALException;

  /**
   * Sets the {@code MALTransmitErrorListener}.
   * @param listener listener called when an asynchronous TRANSMIT ERROR occurs
   * @throws MALException if an error occurs
   */
  public void setTransmitErrorListener(MALTransmitErrorListener listener) throws MALException;
  
  /**
   * Returns the {@code MALTransmitErrorListener}.
   * @return the {@code MALTransmitErrorListener}
   * @throws MALException if an error occurs
   */
  public MALTransmitErrorListener getTransmitErrorListener() throws MALException;
  
  /**
   * Closes this {@code MALProvider}.
   * @throws MALException if an internal error occurs
   */
  public void close() throws MALException;

}
