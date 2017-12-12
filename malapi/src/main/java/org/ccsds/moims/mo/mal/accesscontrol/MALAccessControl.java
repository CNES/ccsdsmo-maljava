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
package org.ccsds.moims.mo.mal.accesscontrol;

import org.ccsds.moims.mo.mal.transport.MALMessage;

/**
 * A {@code MALAccessControl} allows to check if a 
 * message is allowed to be transmitted or received.
 */
public interface MALAccessControl {
  
  /**
   * Checks the specified MAL message and returns the message. The returned message 
   * may be different than the message that has been checked.
   * @param msg the message to check
   * @return the checked message
   * @throws IllegalArgumentException if the message is {@code null}
   * @throws MALCheckErrorException if the message is not allowed to be sent or delivered
   */
  public MALMessage check(MALMessage msg) 
    throws IllegalArgumentException, MALCheckErrorException;

}
