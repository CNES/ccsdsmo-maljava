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

import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;

/**
 * A {@code MALBrokerBinding} represents a binding of a shared MAL level 
 * broker to a transport layer. It can also represent a transport level broker.
 */
public interface MALBrokerBinding {
  
  /**
   * Returns the URI of the broker.
   * @return the URI of the broker
   */
  public URI getURI();
  
  /**
   * Returns the authentication identifier of the broker.
   * @return the authentication identifier of the broker
   */
  public Blob getAuthenticationId();
  
  /**
   * Terminates all pending interactions and deactivates the broker binding.
   * @throws MALException if an internal error occurs
   */
  public void close() throws MALException;
  
  /**
   * Enables a {@code MALBrokerHandler} to send a notify message to a subscriber.
   * @param operation operation of the notify message
   * @param subscriber URI of the subscriber
   * @param transactionId transaction identifier of the notify message
   * @param domainId domain identifier of the notify message
   * @param networkZone network zone of the notify message
   * @param sessionType session type of the notify message
   * @param sessionName session name of the notify message
   * @param notifyQos QoS level of the notify message
   * @param notifyQosProps QoS properties of the notify message
   * @param notifyPriority priority of the notify message
   * @param subscriptionId subscription identifier
   * @param updateHeaderList headers of the notified updates
   * @param updateList notified update lists
   * @return the notified MAL message
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage sendNotify(
      MALOperation operation,
      URI subscriber, 
      Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel notifyQos,
      Map notifyQosProps,
      UInteger notifyPriority,
      Identifier subscriptionId,
      UpdateHeaderList updateHeaderList,
      List... updateList) throws MALInteractionException, MALException;
  
  /**
   * Enables a {@code MALBrokerHandler} to send a notify error message to a subscriber.
   * @param operation operation of the notify error message
   * @param subscriber URI of the subscriber
   * @param transactionId transaction identifier of the notify error message
   * @param domainId domain identifier of the notify error message
   * @param networkZone network zone of the notify error message
   * @param sessionType session type of the notify error message
   * @param sessionName session name of the notify error message
   * @param notifyQos QoS level of the notify message
   * @param notifyQosProps QoS properties of the notify error message
   * @param notifyPriority priority of the notify error message
   * @param error the notified error
   * @return the notified MAL message
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage sendNotifyError(
      MALOperation operation,
      URI subscriber, 
      Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel notifyQos,
      Map notifyQosProps,
      UInteger notifyPriority,
      MALStandardError error) throws MALInteractionException, MALException;

  /**
   * Enables a {@code MALBrokerHandler} to send a publish error message to a publisher.
   * @param operation operation of the publish error message
   * @param publisher URI of the publisher
   * @param transactionId transaction identifier of the publish error message
   * @param domainId domain identifier of the publish error message
   * @param networkZone network zone of the publish error message
   * @param sessionType session type of the publish error message
   * @param sessionName session name of the publish error message
   * @param qos QoS level of the publish error message
   * @param qosProps QoS properties of the publish error message
   * @param priority priority of the publish error message
   * @param error the publish error
   * @return the publish error MAL message
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public MALMessage sendPublishError(
      MALOperation operation,
      URI publisher, 
      Long transactionId,
      IdentifierList domainId,
      Identifier networkZone,
      SessionType sessionType,
      Identifier sessionName,
      QoSLevel qos,
      Map qosProps,
      UInteger priority,
      MALStandardError error) throws MALInteractionException, MALException;
  
  /**
   * Sets the {@code MALTransmitErrorListener}.
   * @param listener listener called when an asynchronous TRANSMIT ERROR occurs 
   * and cannot be returned as a message
   * @throws MALException if an error occurs
   */
  public void setTransmitErrorListener(MALTransmitErrorListener listener) throws MALException;
  
  /**
   * Returns the {@code MALTransmitErrorListener}.
   * @return the {@code MALTransmitErrorListener}
   * @throws MALException if an error occurs
   */
  public MALTransmitErrorListener getTransmitErrorListener() throws MALException;
}
