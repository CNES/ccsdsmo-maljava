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


/**
 * An {@code Enumeration} represents an item of a MAL enumeration.
 */
public abstract class Enumeration implements Element {

  private int ordinal;
  
  /**
   * Constructs an {@code Enumeration} with the index of the enumerated item.
   * @param ordinal the index of the enumerated item
   */
  protected Enumeration(int ordinal) {
    this.ordinal = ordinal;
  }
  
  /**
   * Returns the index of the enumerated item.
   * @return the index of the enumerated item
   */
  public final int getOrdinal() {
    return ordinal;
  }
  
  /**
   * Returns the numeric value of the enumerated item.
   * @return the numeric value of the enumerated item
   */
  public abstract UInteger getNumericValue();
  
  public int hashCode() {
    return ordinal;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (! obj.getClass().equals(getClass()))
      return false;
    Enumeration other = (Enumeration) obj;
    if (ordinal != other.ordinal)
      return false;
    return true;
  }
}
