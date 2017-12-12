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
 * A {@code MALEncodedElementList} represents a list of encoded elements.
 */
public class MALEncodedElementList extends java.util.ArrayList<MALEncodedElement> {
  
  private Object shortForm;

  /**
   * Creates a {@code MALEncodedElementList}.
   * @param shortForm short form of the list type
   * @param initialCapacity initial capacity of the list
   */
  public MALEncodedElementList(Object shortForm, 
      int initialCapacity) {
    super(initialCapacity);
    this.shortForm = shortForm;
  }

  /**
   * Returns the short form of the list type.
   * @return the short form of the list type
   */
  public Object getShortForm() {
    return shortForm;
  }

  @Override
  public String toString() {
    return "MALEncodedElementList [shortForm=" + shortForm + "]";
  }
}
