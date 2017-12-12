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

/**
 * The {@code MALMessageListener} interface is to be implemented 
 * by the MAL layer to be notified by the transport module when
 * a message has been received by an endpoint or when an asynchronous 
 * error has been raised by the transport layer as a consequence 
 * of an asynchronous TRANSMIT ERROR or a severe failure making 
 * the transport unable to work.
 */
public interface MALMessageListener extends MALTransmitErrorListener {
  
  /**
   * Called when a message is to be delivered.
   * @param callingEndpoint endpoint calling the listener
   * @param msg the message to deliver
   */
  public void onMessage(MALEndpoint callingEndpoint, MALMessage msg);
  
  /**
   * Called when multiple messages are to be delivered.
   * @param callingEndpoint endpoint calling the listener
   * @param msgList the messages to deliver
   */
  public void onMessages(MALEndpoint callingEndpoint, MALMessage[] msgList);
  
  /**
   * Called when an internal error causes the
   * transport to stop.
   * @param callingEndpoint endpoint calling the listener
   * @param error the internal error to be notified
   */
  public void onInternalError(MALEndpoint callingEndpoint, Throwable error);

}
