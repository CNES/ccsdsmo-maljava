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

import org.ccsds.moims.mo.mal.MALException;

/**
 * A {@code MALMessageBody} represents the body of a message.
 */
public interface MALMessageBody {
  
  /**
   * Returns the number of elements in the body.
   * @return the number of elements in the body.
   */
  public int getElementCount();
  
  /**
   * Returns an element of the body.
   * @param index index of the element in the body
   * @param element element to be decoded
   * @return the body element
   * @throws MALException if an error occurs
   */
  public Object getBodyElement(int index, Object element) throws MALException;
  
  /**
   * Returns an element of the body in its encoded format.
   * @param index index of the element in the body
   * @return the body element
   * @throws MALException if an error occurs
   */
  public MALEncodedElement getEncodedBodyElement(int index) throws MALException;
  
  /**
   * Returns the body in its encoded format.
   * @return the encoded body
   * @throws MALException if an error occurs
   */
  public MALEncodedBody getEncodedBody() throws MALException;

}
