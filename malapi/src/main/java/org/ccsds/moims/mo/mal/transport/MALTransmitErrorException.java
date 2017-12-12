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

import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;

/**
 * A {@code MALTransmitErrorException} enables to raise a 
 * TRANSMIT ERROR as an exception.
 */
public class MALTransmitErrorException extends MALInteractionException {
  
  private MALMessageHeader header;
  
  private Map qosProperties;

  /**
   * Constructs a {@code MALTransmitErrorException}.
   * @param header Header of the message that cannot be transmitted
   * @param standardError Error preventing the message to be transmitted
   * @param qosProperties QoS properties of the message which cannot be transmitted
   */
  public MALTransmitErrorException(MALMessageHeader header, 
      MALStandardError standardError, Map qosProperties) {
    super(standardError);
    this.header = header;
    this.qosProperties = qosProperties;
  }

  /**
   * Returns the header of the message that cannot be transmitted.
   * @return the header of the message that cannot be transmitted
   */
  public MALMessageHeader getHeader() {
    return header;
  }

  /**
   * Returns the error preventing the message to be transmitted.
   * @return the error preventing the message to be transmitted
   */
  public Map getQosProperties() {
    return qosProperties;
  }
}
