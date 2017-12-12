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

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALSendOperation;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;

/**
 * A {@code MALConsumer} provides a communication context
 * to initiate interaction patterns as a service consumer.
 */
public interface MALConsumer {
  
  /**
   * Returns the consumer URI.
   * @return the consumer URI
   */
  public URI getURI();

  /**
   * Initiates a SEND interaction.
   * @param op the SEND operation to initiate
   * @param body the elements to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage send(MALSendOperation op, Object... body) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a SEND interaction.
   * @param op the SEND operation to initiate
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage send(MALSendOperation op, MALEncodedBody encodedBody) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous SUBMIT interaction.
   * @param op the SUBMIT operation to initiate
   * @param body the elements to transmit in the initiation message
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public void submit(MALSubmitOperation op, Object... body) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous SUBMIT interaction.
   * @param op the SUBMIT operation to initiate
   * @param encodedBody the encoded body to transmit in the initiation message
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public void submit(MALSubmitOperation op, MALEncodedBody encodedBody) 
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous REQUEST interaction
   * @param op the REQUEST operation to initiate
   * @param body the elements to transmit in the initiation message
   * @return the RESPONSE message body
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody request(MALRequestOperation op, Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous REQUEST interaction
   * @param op the REQUEST operation to initiate
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the RESPONSE message body
   * @throws IllegalArgumentException if the parameter ‘op’ is {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody request(MALRequestOperation op, MALEncodedBody encodedBody)
      throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous INVOKE interaction
   * @param op the INVOKE operation to initiate
   * @param listener Listener in charge of receiving the messages RESPONSE and RESPONSE ERROR
   * @param body the elements to transmit in the initiation message
   * @return the ACK message body
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody invoke(MALInvokeOperation op, 
      MALInteractionListener listener, Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous INVOKE interaction
   * @param op the INVOKE operation to initiate
   * @param listener Listener in charge of receiving the messages RESPONSE and RESPONSE ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the ACK message body
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody invoke(MALInvokeOperation op, 
      MALInteractionListener listener, MALEncodedBody encodedBody)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous PROGRESS interaction
   * @param op the PROGRESS operation to initiate
   * @param listener listener in charge of receiving the messages UPDATE, UPDATE ERROR, RESPONSE and RESPONSE ERROR
   * @param body the elements to transmit in the initiation message
   * @return the ACK message body
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody progress(MALProgressOperation op, 
      MALInteractionListener listener, Object... body) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous PROGRESS interaction
   * @param op the PROGRESS operation to initiate
   * @param listener listener in charge of receiving the messages UPDATE, UPDATE ERROR, RESPONSE and RESPONSE ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the ACK message body
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessageBody progress(MALProgressOperation op, 
      MALInteractionListener listener, MALEncodedBody encodedBody) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous PUBLISH-SUBSCRIBE REGISTER interaction.
   * @param op the PUBLISH-SUBSCRIBE operation to initiate
   * @param subscription subscription to be registered
   * @param listener listener in charge of receiving the messages NOTIFY and NOTIFY ERROR
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘subscription’ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public void register(MALPubSubOperation op,
      Subscription subscription,
      MALInteractionListener listener) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates a synchronous PUBLISH-SUBSCRIBE DEREGISTER interaction.
   * @param the PUBLISH-SUBSCRIBE operation to initiate
   * @param subscriptionIdList list of the subscription identifiers to deregister
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘subscriptionIdList’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public void deregister(MALPubSubOperation op, 
      IdentifierList subscriptionIdList)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous SUBMIT interaction.
   * @param op the SUBMIT operation to initiate
   * @param listener listener in charge of receiving the messages ACK and ACK ERROR
   * @param body the elements to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncSubmit(
      MALSubmitOperation op,
      MALInteractionListener listener,
      Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous SUBMIT interaction.
   * @param op the SUBMIT operation to initiate
   * @param listener listener in charge of receiving the messages ACK and ACK ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncSubmit(
      MALSubmitOperation op,
      MALInteractionListener listener,
      MALEncodedBody encodedBody)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous REQUEST interaction.
   * @param op the REQUEST operation to initiate
   * @param listener listener in charge of receiving the messages RESPONSE and RESPONSE ERROR
   * @param body the elements to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncRequest(
      MALRequestOperation op,
      MALInteractionListener listener,
      Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous REQUEST interaction.
   * @param op the REQUEST operation to initiate
   * @param listener listener in charge of receiving the messages RESPONSE and RESPONSE ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncRequest(
      MALRequestOperation op,
      MALInteractionListener listener,
      MALEncodedBody encodedBody)
    throws IllegalArgumentException, MALInteractionException, MALException;
 
  /**
   * Initiates an asynchronous INVOKE interaction.
   * @param op the INVOKE operation to initiate
   * @param listener listener in charge of receiving the messages ACK, ACK ERROR, RESPONSE and RESPONSE ERROR
   * @param body the elements to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncInvoke(
      MALInvokeOperation op,
      MALInteractionListener listener,
      Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous INVOKE interaction.
   * @param op the INVOKE operation to initiate
   * @param listener listener in charge of receiving the messages ACK, ACK ERROR, RESPONSE and RESPONSE ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncInvoke(
      MALInvokeOperation op,
      MALInteractionListener listener,
      MALEncodedBody encodedBody)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PROGRESS interaction.
   * @param op the PROGRESS operation to initiate
   * @param listener listener in charge of receiving the messages ACK, ACK ERROR, UPDATE, UPDATE ERROR, RESPONSE and RESPONSE ERROR
   * @param body the elements to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncProgress(
      MALProgressOperation op,
      MALInteractionListener listener,
      Object... body)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PROGRESS interaction.
   * @param op the PROGRESS operation to initiate
   * @param listener listener in charge of receiving the messages ACK, ACK ERROR, UPDATE, UPDATE ERROR, RESPONSE and RESPONSE ERROR
   * @param encodedBody the encoded body to transmit in the initiation message
   * @return the initiation message
   * @throws IllegalArgumentException if the parameters ‘op’ or 'listener' are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncProgress(
      MALProgressOperation op,
      MALInteractionListener listener,
      MALEncodedBody encodedBody)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PUBLISH-SUBSCRIBE REGISTER interaction.
   * @param op the PUBLISH-SUBSCRIBE operation to initiate
   * @param subscription subscription to be registered
   * @param listener listener in charge of receiving the messages REGISTER ACK, REGISTER ERROR, NOTIFY and NOTIFY ERROR
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘subscription’ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncRegister(
      MALPubSubOperation op,
      Subscription subscription,
      MALInteractionListener listener) 
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Initiates an asynchronous PUBLISH-SUBSCRIBE DEREGISTER interaction.
   * @param op the PUBLISH-SUBSCRIBE operation to initiate
   * @param subscriptionIdList list of the subscription identifiers to deregister
   * @param listener listener in charge of receiving the messages DEREGISTER ACK, DEREGISTER ERROR
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘subscriptionIdList’ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs during the initiation message sending
   * @throws MALException if a non-MAL error occurs during the initiation message sending
   */
  public MALMessage asyncDeregister(
      MALPubSubOperation op,
      IdentifierList subscriptionIdList,
      MALInteractionListener listener)
    throws IllegalArgumentException, MALInteractionException, MALException;
  
  /**
   * Continues an interaction that has been interrupted.
   * @param op the operation to continue
   * @param lastInteractionStage the last stage of the interaction to continue
   * @param initiationTimestamp timestamp of the interaction initiation message
   * @param transactionId transaction identifier of the interaction to continue
   * @param listener listener in charge of receiving the messages from the service provider
   * @throws IllegalArgumentException if the parameters ‘op’ or ‘lastInteractionStage’ or ‘initiationTimestamp’ or ‘transactionId ‘ or ‘listener’ are {@code null}
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void continueInteraction(
      MALOperation op,
      UOctet lastInteractionStage,
      Time initiationTimestamp,
      Long transactionId, 
      MALInteractionListener listener)
      throws IllegalArgumentException, MALInteractionException, MALException;
  
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
  
  /**
   * Closes the consumer.
   * @throws MALException if an internal error occurs 
   */
  public void close() throws MALException;
  
}
