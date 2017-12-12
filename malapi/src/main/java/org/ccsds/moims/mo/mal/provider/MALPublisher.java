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

import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;

/**
 * A {@code MALPublisher} enable a provider to publish updates
 * and errors to registered consumers.
 */
public interface MALPublisher {
  
  /**
   * Returns the provider of the publisher.
   * @return the provider of the publisher
   */
  public MALProvider getProvider();
  
  /**
   * Initiates a synchronous PUBLISH REGISTER interaction.
   * @param entityKeys keys of the entities that are to be published
   * @param listener listener in charge of receiving the messages PUBLISH ERROR
   * @throws IllegalArgumentException if the parameters ‘entityKeyList’ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a PUBLISH REGISTER ERROR occurs
   * @throws MALException if the {@code MALPublisher} is closed or if an error occurs
   */
  public void register(EntityKeyList entityKeys, MALPublishInteractionListener listener) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous PUBLISH DEREGISTER interaction.
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void deregister() throws MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PUBLISH REGISTER interaction.
   * @param entityKeys keys of the entities that are to be published
   * @param listener listener in charge of receiving the messages PUBLISH REGISTER ACK,
   * PUBLISH REGISTER ERROR and PUBLISH ERROR
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘entityKeyList’ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage asyncRegister(EntityKeyList entityKeys, MALPublishInteractionListener listener) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PUBLISH DEREGISTER interaction.
   * @param listener listener in charge of receiving the messages PUBLISH DEREGISTER ACK
   * @return the initiation message
   * @throws IllegalArgumentException if the parameter ‘listener’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage asyncDeregister(MALPublishInteractionListener listener) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Publish a list of updates.
   * @param updateHeaderList published update headers
   * @param updateLists lists of updates to be published
   * @return the initiation message
   * @throws IllegalArgumentException if the parameter ‘updateHeaderList’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage publish(UpdateHeaderList updateHeaderList, List... updateLists) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Releases the resources owned by this {@code MALPublisher}.
   * @throws MALException if an error occurs
   */
  public void close() throws MALException;
}
