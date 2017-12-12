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

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;

/**
 * The {@code MALInteractionListener} interface is to be implemented 
 * by any MAL clients that need to asynchronously receive messages
 * on the consumer side.
 */
public interface MALInteractionListener {
  
  /**
   * Invoked when a SUBMIT ACK message has been received.
   * @param header header of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void submitAckReceived(MALMessageHeader header, 
      Map qosProperties) throws MALException;

  /**
   * Invoked when a SUBMIT ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void submitErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a REQUEST RESPONSE message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void requestResponseReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties) throws MALException;
  
  /**
   * Invoked when a REQUEST ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void requestErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when an INVOKE ACK message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void invokeAckReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when an INVOKE ACK ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void invokeAckErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when an INVOKE RESPONSE message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void invokeResponseReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when a INVOKE RESPONSE ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void invokeResponseErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;
  
  /**
   * Invoked when a PROGRESS ACK message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressAckReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a PROGRESS ACK ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressAckErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;
  
  /**
   * Invoked when a PROGRESS UPDATE message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressUpdateReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when a PROGRESS UPDATE ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressUpdateErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a PROGRESS RESPONSE message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressResponseReceived(MALMessageHeader header, 
      MALMessageBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when a PROGRESS RESPONSE ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void progressResponseErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a REGISTER ACK message has been received.
   * @param header header of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void registerAckReceived(MALMessageHeader header, 
      Map qosProperties)
      throws MALException;

  /**
   * Invoked when a REGISTER ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void registerErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a NOTIFY message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void notifyReceived(MALMessageHeader header, 
      MALNotifyBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a NOTIFY ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void notifyErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties)
      throws MALException;

  /**
   * Invoked when a DEREGISTER ACK message has been received.
   * @param header header of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void deregisterAckReceived(MALMessageHeader header, 
      Map qosProperties)
      throws MALException;

}
