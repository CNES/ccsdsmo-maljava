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
package org.ccsds.moims.mo.mal.encoding;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * A {@code MALEncodingContext} gives
 * information about the encoding context.
 */
public class MALEncodingContext {
  
  private MALMessageHeader header;
  
  private MALOperation operation;
  
  private int bodyElementIndex;
  
  private Map endpointProperties;
  
  private Map messageProperties;

  /**
   * Creates a context.
   * @param header header of the message that contains the body elements to encode or decode
   * @param operation description of the operation that has been called
   * @param bodyElementIndex index of the body element to encode or decode
   * @param endpointProperties QoS properties used by the endpoint that has received or sent the message
   * @param messageProperties QoS properties of the message
   */
  public MALEncodingContext(MALMessageHeader header, MALOperation operation,
      int bodyElementIndex, Map endpointProperties, Map messageProperties) {
    super();
    this.header = header;
    this.operation = operation;
    this.bodyElementIndex = bodyElementIndex;
    this.endpointProperties = endpointProperties;
    this.messageProperties = messageProperties;
  }

  /**
   * Returns the header of the message.
   * @return the header of the message
   */
  public MALMessageHeader getHeader() {
    return header;
  }

  /**
   * Sets the header of the message.
   * @param header the header of the message
   */
  public void setHeader(MALMessageHeader header) {
    this.header = header;
  }

  /**
   * Returns the operation of the message.
   * @return the operation of the message
   */
  public MALOperation getOperation() {
    return operation;
  }

  /**
   * Sets the operation of the message.
   * @param operation the operation of the message
   */
  public void setOperation(MALOperation operation) {
    this.operation = operation;
  }

  /**
   * Returns the QoS properties used by the endpoint.
   * @return the QoS properties used by the endpoint
   */
  public Map getEndpointQosProperties() {
    return endpointProperties;
  }

  /**
   * Sets the QoS properties used by the endpoint.
   * @param endpointProperties the QoS properties used by the endpoint
   */
  public void setEndpointQosProperties(Map endpointProperties) {
    this.endpointProperties = endpointProperties;
  }

  /**
   * Returns the QoS properties of the message.
   * @return the QoS properties of the message
   */
  public Map getMessageQosProperties() {
    return messageProperties;
  }

  /**
   * Sets the QoS properties of the message
   * @param messageProperties the QoS properties of the message
   */
  public void setMessageQosProperties(Map messageProperties) {
    this.messageProperties = messageProperties;
  }

  /**
   * Returns the index of the body element to encode or decode.
   * @return the index of the body element to encode or decode
   */
  public int getBodyElementIndex() {
    return bodyElementIndex;
  }

  /**
   * Sets the index of the body element to encode or decode.
   * @param bodyElementIndex the index of the body element to encode or decode
   */
  public void setBodyElementIndex(int bodyElementIndex) {
    this.bodyElementIndex = bodyElementIndex;
  }
}
