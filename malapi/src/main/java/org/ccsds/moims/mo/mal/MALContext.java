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
package org.ccsds.moims.mo.mal;

import org.ccsds.moims.mo.mal.accesscontrol.MALAccessControl;
import org.ccsds.moims.mo.mal.broker.MALBrokerManager;
import org.ccsds.moims.mo.mal.consumer.MALConsumerManager;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALTransport;

/**
 * A {@code MALContext} enables a MAL client to consume and/or provide services.
 */
public interface MALContext {
  
  /**
   * Creates a consumer manager.
   * @return a newly created consumer manager
   * @throws MALException if the MAL context fails to create a consumer manager due to some internal error
   */
  public MALConsumerManager createConsumerManager() throws MALException;
  
  /**
   * Creates a provider manager.
   * @return a newly created provider manager
   * @throws MALException if the MAL context fails to create a provider manager due to some internal error
   */
  public MALProviderManager createProviderManager() throws MALException;
  
  /**
   * Creates a broker manager.
   * @return a newly created broker manager
   * @throws MALException if the MAL context fails to create a broker manager due to some internal error
   */
  public MALBrokerManager createBrokerManager() throws MALException;
  
  /**
   * Returns the transport which protocol is given by the specified URI.
   * @param uri gives the protocol of the transport to return
   * @return the transport which protocol is given by the specified URI
   * @throws MALException if no transport can be returned 
   */
  public MALTransport getTransport(URI uri) throws MALException;
  
  /**
   * Returns the transport which protocol is specified.
   * @param protocol protocol of the transport to return
   * @return the transport which protocol is specified
   * @throws MALException if no transport can be returned 
   */
  public MALTransport getTransport(String protocol) throws MALException;
  
  /**
   * Returns the access control used by the {@code MALContext}.
   * @return the access control used by the {@code MALContext}
   * @throws MALException if no MALAccessControl can be returned
   */
  public MALAccessControl getAccessControl() throws MALException;
  
  /**
   * Closes the MAL context.
   * @throws MALException if an internal error occurs 
   */
  public void close() throws MALException;
  
}
