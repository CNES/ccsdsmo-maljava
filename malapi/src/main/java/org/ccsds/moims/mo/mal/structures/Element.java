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
package org.ccsds.moims.mo.mal.structures;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;

/**
 * An {@code Element} represents a MAL element.
 */
public interface Element {
  
  /**
   * Returns the number of the area this element belongs to.
   * @return the number of the area this element belongs to.
   */
  public UShort getAreaNumber();
  
  /**
   * Returns the version of the area this element belongs to.
   * @return the version of the area this element belongs to.
   */
  public UOctet getAreaVersion();
  
  /**
   * Returns the number of the service this element belongs to.
   * @return the number of the service this element belongs to.
   */
  public UShort getServiceNumber();
  
  /**
   * Returns the type short form of the element.
   * @return the type short form of the element.
   */
  public Integer getTypeShortForm();
  
  /**
   * Returns the absolute short form of the element.
   * @return the absolute short form of the element
   */
  public Long getShortForm();
  
  /**
   * Encodes this {@code Element}.
   * @param encoder {@code MALEncoder} to be used by the encoding
   * @throws MALException if an encoding error occurs
   */
  public void encode(MALEncoder encoder) throws MALException;

  /**
   * Decodes an {@code Element}.
   * @param decoder {@code MALDecoder} to be used by the decoding
   * @return the decoded {@code Element}
   * @throws MALException if an decoding error occurs
   */
  public Element decode(MALDecoder decoder) throws MALException;
  
  /**
   * Creates an {@code Element} of the same type as this {@code Element}.
   * @return an {@code Element} of the same type as this {@code Element}
   */
  public Element createElement();
}
