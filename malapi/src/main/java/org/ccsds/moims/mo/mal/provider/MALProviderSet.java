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

import java.util.ArrayList;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;

/**
 * A {@code MALProviderSet} manages a list of {@code MALProvider} providing
 * the same service through different protocols.
 * It creates instances of {@code MALPublisherSet}, registers their references
 * and updates them when a {@code MALProvider} is either added or removed.
 * A {@code MALProviderSet} should not be directly used by a MAL client.
 * It is intended to be used by the generated skeleton classes.
 */
public class MALProviderSet {
  
  private MALService service;
  
  private ArrayList<MALProvider> providers;
  
  private ArrayList<MALPublisherSet> publisherSets;
  
  /**
   * Constructs a {@code MALProviderSet}.
   * @param service the service to be provided by every {@code MALProvider}
   * added to this {@code MALProviderSet}
   */
  public MALProviderSet(MALService service) {
    this.service = service;
    this.providers = new ArrayList<MALProvider>();
    this.publisherSets = new ArrayList<MALPublisherSet>();
  }

  /**
   * Adds a provider.
   * @param provider the provider to be added
   * @throws IllegalArgumentException if the provider is {@code null}
   * @throws MALException if an error occurs
   */
  public synchronized void addProvider(MALProvider provider) 
      throws IllegalArgumentException, MALException {
    if (provider == null) throw new IllegalArgumentException("Null provider");
    providers.add(provider);
    for(MALPublisherSet ps : publisherSets) {
      ps.createPublisher(provider);
    }
  }
  
  /**
   * Removes a provider.
   * @param provider the provider to be removed
   * @throws IllegalArgumentException if the provider is {@code null}
   * @throws MALException if an error occurs
   */
  public synchronized void removeProvider(MALProvider provider) 
      throws IllegalArgumentException, MALException {
    if (provider == null) throw new IllegalArgumentException("Null provider");
    providers.remove(provider);
    for(MALPublisherSet ps : publisherSets) {
      ps.deletePublisher(provider);
    }
  }
  
  /**
   * Creates a {@code MALPublisherSet} and registers its reference.
   * @param op the publish-subscribe operation
   * @param domain domain of the PUBLISH messages
   * @param networkZone Network zone of the PUBLISH messages
   * @param sessionType Session type of the PUBLISH messages
   * @param sessionName Session name of the PUBLISH messages
   * @param publishQos QoS level of the PUBLISH messages
   * @param publishQosProps QoS properties of the PUBLISH messages
   * @param publishPriority Priority of the PUBLISH messages
   * @return the newly created publisher set
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘domain’ or ‘networkZone’ or ‘sessionType’ or 
   * ‘sessionName’ or ‘remotePublisherQos’ or ‘remotePublisherPriority’ are {@code null} 
   * @throws MALException if an error occurs
   */
  public synchronized MALPublisherSet createPublisherSet(
      MALPubSubOperation op,
      IdentifierList domain,
      org.ccsds.moims.mo.mal.structures.Identifier networkZone,
      org.ccsds.moims.mo.mal.structures.SessionType sessionType,
      org.ccsds.moims.mo.mal.structures.Identifier sessionName,
      org.ccsds.moims.mo.mal.structures.QoSLevel publishQos,
      Map publishQosProps,
      UInteger publishPriority) throws IllegalArgumentException, MALException {
    if (op == null) throw new IllegalArgumentException("Null operation");
    if (domain == null) throw new IllegalArgumentException("Null domain");
    if (networkZone == null) throw new IllegalArgumentException("Null network zone");
    if (sessionType == null) throw new IllegalArgumentException("Null session type");
    if (sessionName == null) throw new IllegalArgumentException("Null session name");
    if (publishQos == null) throw new IllegalArgumentException("Null QoS");
    if (publishPriority == null) throw new IllegalArgumentException("Null priority");
    MALPublisherSet publisherSet = new MALPublisherSet(this, op, domain, networkZone, sessionType, 
        sessionName, publishQos, publishQosProps, publishPriority);
    for (MALProvider p : providers) {
      publisherSet.createPublisher(p);
    }
    publisherSets.add(publisherSet);
    return publisherSet;
  }
  
  /**
   * Removes the specified {@code MALPublisherSet}.
   * @param publisherSet the {@code MALPublisherSet} to remove
   */
  synchronized void closePublisherSet(MALPublisherSet publisherSet) {
    publisherSets.remove(publisherSet);
  }

}
