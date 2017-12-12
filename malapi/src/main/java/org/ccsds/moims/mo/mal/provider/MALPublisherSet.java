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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;

/**
 * A {@code MALPublisherSet} manages a set of {@code MALPublisher} 
 * publishing updates through the same PUBLISH-SUBSCRIBE operation, 
 * in the same domain, network zone and session, with the same QoS level and priority.
 * A {@code MALPublisherSet} should not be directly used by a MAL client.
 * It is intended to be used by the generated skeleton classes.
 */
public class MALPublisherSet {
  
  private org.ccsds.moims.mo.mal.MALPubSubOperation op;

  private IdentifierList domain;

  private org.ccsds.moims.mo.mal.structures.Identifier networkZone;

  private org.ccsds.moims.mo.mal.structures.SessionType sessionType;

  private org.ccsds.moims.mo.mal.structures.Identifier sessionName;

  private org.ccsds.moims.mo.mal.structures.QoSLevel publishQos;

  private Map publishQosProps;

  private UInteger publishPriority;
  
  private HashMap<MALProvider, MALPublisher> publishers;
  
  private MALProviderSet providerSet;
  
  /**
   * Constructs a {@code MALPublisherSet}.
   * @param providerSet provider set that owns this {@code MALPublisherSet}
   * @param op PUBLISH-SUBSCRIBE operation 
   * @param domain domain of the PUBLISH messages
   * @param networkZone network zone of the PUBLISH messages
   * @param sessionType session type of the PUBLISH messages
   * @param sessionName session name of the PUBLISH messages
   * @param publishQos QoS level of the PUBLISH messages
   * @param publishQosProps QoS properties of the PUBLISH messages
   * @param publishPriority priority of the PUBLISH messages
   */
  public MALPublisherSet(MALProviderSet providerSet,
      MALPubSubOperation op, IdentifierList domain,
      Identifier networkZone, SessionType sessionType, Identifier sessionName,
      QoSLevel publishQos, Map publishQosProps, UInteger publishPriority) {
    this.providerSet = providerSet;
    this.op = op;
    this.domain = domain;
    this.networkZone = networkZone;
    this.sessionType = sessionType;
    this.sessionName = sessionName;
    this.publishQos = publishQos;
    this.publishQosProps = publishQosProps;
    this.publishPriority = publishPriority;
    publishers = new HashMap<MALProvider, MALPublisher>();
  }

  /**
   * Creates and adds a {@code MALPublisher}.
   * @param provider {@code MALProvider} to be used in order to create the {@code MALPublisher}
   * @throws IllegalArgumentException if the provider is {@code null}
   * @throws MALException if an error occurs
   */
  synchronized void createPublisher(MALProvider provider) 
    throws IllegalArgumentException, MALException {
    if (provider == null) throw new IllegalArgumentException("Null provider");
    MALPublisher publisher = provider.createPublisher(op, domain, networkZone, sessionType,
        sessionName, publishQos, publishQosProps, publishPriority);
    publishers.put(provider, publisher);
  }
  
  /**
   * Closes and removes a {@code MALPublisher}.
   * @param provider {@code MALProvider} that owns the {@code MALPublisher} to remove
   * @throws IllegalArgumentException if the provider is {@code null}
   * @throws MALException if an error occurs
   */
  synchronized void deletePublisher(MALProvider provider) 
    throws IllegalArgumentException, MALException {
    if (provider == null) throw new IllegalArgumentException("Null provider");
    MALPublisher publisher = publishers.remove(provider);
    publisher.close();
  }
  
  /**
   * Closes every {@code MALPublisher}.
   * @throws MALException if an error occurs
   */
  public synchronized void close() throws MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      publisher.close();
    }
    providerSet.closePublisherSet(this);
  }
  
  /**
   * Synchronously registers through every {@code MALPublisher}.
   * @param entityKeys keys of the entities that are to be published
   * @param listener listener in charge of receiving the messages PUBLISH ERROR
   * @throws MALInteractionException if a PUBLISH REGISTER ERROR occurs
   * @throws MALException if an error occurs
   */
  public synchronized void register(org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys,
      org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      publisher.register(entityKeys, listener);
    }
  }

  /**
   * Synchronously deregisters through every {@code MALPublisher}.
   * @throws MALInteractionException if a MAL error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public synchronized void deregister() throws MALInteractionException, MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      publisher.deregister();
    }
  }

  /**
   * Asynchronously registers through every {@code MALPublisher}.
   * @param entityKeys keys of the entities that are to be published
   * @param listener listener in charge of receiving the messages PUBLISH REGISTER ACK,
   * PUBLISH REGISTER ERROR and PUBLISH ERROR
   * @throws MALInteractionException if a MAL error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public synchronized MALMessage asyncRegister(
      org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys,
      org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    MALMessage res = null;
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      res = publisher.asyncRegister(entityKeys, listener);
    }
    return res;
  }

  /**
   * Asynchronously deregisters through every {@code MALPublisher}.
   * @param listener listener in charge of receiving the messages PUBLISH DEREGISTER ACK
   * @throws MALInteractionException if a MAL error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public synchronized MALMessage asyncDeregister(
      org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    MALMessage res = null;
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      res = publisher.asyncDeregister(listener);
    }
    return res;
  }

  /**
   * Publish the specified update lists through every {@code MALPublisher}.
   * @param updateHeaderList published update headers
   * @param updateLists update lists to be published
   * @return the initiation message
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public synchronized MALMessage publish(UpdateHeaderList updateHeaderList, List... updateLists)
      throws MALInteractionException, MALException {
    Collection<MALPublisher> elements = publishers.values();
    Iterator<MALPublisher> iterator = elements.iterator();
    MALMessage res = null;
    while (iterator.hasNext()) {
      MALPublisher publisher = iterator.next();
      res = publisher.publish(updateHeaderList, updateLists);
    }
    return res;
  }
  
}
