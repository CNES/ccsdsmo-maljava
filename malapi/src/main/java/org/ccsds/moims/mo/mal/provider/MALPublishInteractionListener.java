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
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * The {@code MALPublishInteractionListener} interface is to be implemented 
 * by any MAL clients that need to asynchronously receive Pub/Sub messages
 * on the publisher side.
 */
public interface MALPublishInteractionListener {
  
  /**
   * Invoked when a PUBLISH REGISTER ACK message has been received.
   * @param header header of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void publishRegisterAckReceived(MALMessageHeader header,
      Map qosProperties) throws MALException;

  /**
   * Invoked when a PUBLISH REGISTER ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void publishRegisterErrorReceived(MALMessageHeader header,
      MALErrorBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when a PUBLISH ERROR message has been received.
   * @param header header of the message
   * @param body body of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void publishErrorReceived(MALMessageHeader header, 
      MALErrorBody body, Map qosProperties) throws MALException;

  /**
   * Invoked when a PUBLISH DEREGISTER ACK message has been received.
   * @param header header of the message
   * @param qosProperties QoS properties of the message
   * @throws MALException if an error occurs
   */
  public void publishDeregisterAckReceived(MALMessageHeader header,
      Map qosProperties) throws MALException;

}
