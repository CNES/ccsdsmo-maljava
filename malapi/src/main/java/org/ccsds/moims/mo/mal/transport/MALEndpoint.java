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
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALEndpoint} represents a communication endpoint created and
 * managed by a particular {@code MALTransport}. A {@code MALEndpoint}
 * allows to send and receive MAL messages.
 */
public interface MALEndpoint {
  
  /**
   * Returns the URI of this {@code MALEndpoint}.
   * @return the URI of this {@code MALEndpoint}
   */
  public URI getURI();
  
  /**
   * Returns the local name of this {@code MALEndpoint}.
   * @return the local name of this {@code MALEndpoint}
   */
  public String getLocalName();
  
  /**
   * Creates a new message.
   * @param authenticationId authentication identifier of the  message
   * @param uriTo URI of the message destination
   * @param timestamp timestamp of the message
   * @param qoSlevel QoS level of the message
   * @param priority priority of the message
   * @param domain domain of the service provider
   * @param networkZone network zone of the service provider
   * @param session session of the service provider
   * @param sessionName session name of the service provider
   * @param transactionId identifier of the interaction
   * @param isErrorMessage flag indicating if the message conveys an error
   * @param op Operation represented as a MALOperation
   * @param interactionStage interaction stage of the interaction
   * @param qosProperties QoS properties of the message
   * @param body Message body elements
   * @return a newly created message
   * @throws IllegalArgumentException if any of the parameters
   * except ‘qosProperties’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALMessage createMessage(
      Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      Long transactionId,
      Boolean isErrorMessage,
      MALOperation op,
      UOctet interactionStage,
      Map qosProperties,
      Object... body) throws IllegalArgumentException, MALException;
  
  /**
   * Creates a new message.
   * @param authenticationId authentication identifier of the  message
   * @param uriTo URI of the message destination
   * @param timestamp timestamp of the message
   * @param qoSlevel QoS level of the message
   * @param priority priority of the message
   * @param domain domain of the service provider
   * @param networkZone network zone of the service provider
   * @param session session of the service provider
   * @param sessionName session name of the service provider
   * @param transactionId identifier of the interaction
   * @param isErrorMessage flag indicating if the message conveys an error
   * @param op Operation represented as a MALOperation
   * @param interactionStage interaction stage of the interaction
   * @param qosProperties QoS properties of the message
   * @param encodedBody encoded body
   * @return a newly created message
   * @throws IllegalArgumentException if any of the parameters
   * except ‘qosProperties’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALMessage createMessage(
      Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      Long transactionId,
      Boolean isErrorMessage,
      MALOperation op,
      UOctet interactionStage,
      Map qosProperties,
      MALEncodedBody encodedBody) throws IllegalArgumentException, MALException;
  
  /**
   * Creates a new message.
   * @param authenticationId authentication identifier of the  message
   * @param uriTo URI of the message destination
   * @param timestamp timestamp of the message
   * @param qoSlevel QoS level of the message
   * @param priority priority of the message
   * @param domain domain of the service provider
   * @param networkZone network zone of the service provider
   * @param session session of the service provider
   * @param sessionName session name of the service provider
   * @param interactionType interaction type of the operation
   * @param interactionStage interaction stage of the interaction
   * @param transactionId transaction identifier of the interaction
   * @param serviceArea area number of the service
   * @param service service number
   * @param operation operation number
   * @param areaVersion area version number
   * @param isErrorMessage flag indicating if the message conveys an error
   * @param qosProperties QoS properties of the message
   * @param body Message body elements
   * @return a newly created message
   * @throws IllegalArgumentException if any of the parameters
   * except ‘qosProperties’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet areaVersion,
      Boolean isErrorMessage, Map qosProperties, Object... body)
      throws IllegalArgumentException, MALException;
  
  /**
   * Creates a new message.
   * @param authenticationId authentication identifier of the  message
   * @param uriTo URI of the message destination
   * @param timestamp timestamp of the message
   * @param qoSlevel QoS level of the message
   * @param priority priority of the message
   * @param domain domain of the service provider
   * @param networkZone network zone of the service provider
   * @param session session of the service provider
   * @param sessionName session name of the service provider
   * @param interactionType interaction type of the operation
   * @param interactionStage interaction stage of the interaction
   * @param transactionId transaction identifier of the interaction
   * @param serviceArea area number of the service
   * @param service service number
   * @param operation operation number
   * @param areaVersion service version number
   * @param isErrorMessage flag indicating if the message conveys an error
   * @param qosProperties QoS properties of the message
   * @param encodedBody encoded body
   * @return a newly created message
   * @throws IllegalArgumentException if any of the parameters
   * except ‘qosProperties’ are {@code null}
   * @throws MALException if an error occurs
   */
  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet areaVersion,
      Boolean isErrorMessage, Map qosProperties, MALEncodedBody encodedBody)
      throws IllegalArgumentException, MALException;
  
  /**
   * Starts the message delivery.
   * @throws MALException if an error occurs
   */
  public void startMessageDelivery() throws MALException;
  
  /**
   * Stops the message delivery.
   * @throws MALException if an error occurs
   */
  public void stopMessageDelivery() throws MALException;
  
  /**
   * Sets the {@code MALMessageListener} of this {@code MALEndpoint}
   * @param listener the listener
   * @throws MALException if the {@code MALEndpoint} has been closed or if an error occurs
   */
  public void setMessageListener(MALMessageListener listener) throws MALException;
  
  /**
   * Sends a MAL message.
   * @param msg the message to send.
   * @throws IllegalArgumentException if the parameter ‘msg’ is {@code null}
   * @throws MALTransmitErrorException if a TRANSMIT ERROR occurs
   * @throws MALException if an error occurs
   */
  public void sendMessage(MALMessage msg) 
      throws IllegalArgumentException, MALTransmitErrorException, MALException;
  
  /**
   * Sends a list of messages.
   * @param msgList list of messages to send
   * @throws IllegalArgumentException if the parameter ‘msgList’ is {@code null} 
   * @throws MALTransmitMultipleErrorException if a MULTIPLETRANSMIT ERROR occurs 
   * @throws MALException if an error occurs
   */
  public void sendMessages(MALMessage[] msgList) 
      throws IllegalArgumentException, MALTransmitMultipleErrorException, MALException;
  
  /**
   * Releases all the resources owned by this endpoint.
   * @throws MALException if an error occurs
   */
  public void close() throws MALException;
}
