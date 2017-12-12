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

import org.ccsds.moims.mo.mal.MALException;

/**
 * A {@code MALElementOutputStream} encodes elements 
 * pursuant to the protocol handled by the {@code MALElementStreamFactory}.
 */
public interface MALElementOutputStream {

  /**
   * Encodes an element.
   * @param element element to be encoded
   * @param ctx context to be used during the encoding
   * @throws MALException if an error occurs
   */
  public void writeElement(Object element, MALEncodingContext ctx) throws MALException;
  
  /**
   * Flushes the encoding stream.
   * @throws MALException if an error occurs
   */
  public void flush() throws MALException;
  
  /**
   * Closes the encoding stream
   * @throws MALException if an error occurs
   */
  public void close() throws MALException;
}
