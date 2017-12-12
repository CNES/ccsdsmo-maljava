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
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

/**
 * A {@code UInteger} represents a MAL UOctet.
 */
public class UOctet implements Attribute, Comparable<UOctet> {
  
  /**
   * Absolute short form.
   */
  public static final Long SHORT_FORM = UOCTET_SHORT_FORM;
  
  /**
   * Type short form.
   */
  public static final Integer TYPE_SHORT_FORM = UOCTET_TYPE_SHORT_FORM;
  
  private short value;
  
  /**
   * Constructs a {@code UOctet} without any parameters.
   */
  public UOctet() {}
  
  /**
   * Constructs a {@code UOctet} with a short integer.
   * @param value the value of this {@code UOctet}
   */
  public UOctet(short value) {
    super();
    this.value = value;
  }
  
  /**
   * Returns the {@code UOctet} value
   * @return the {@code UOctet} value
   */
  public short getValue() {
    return value;
  }
  
  public int hashCode() {
    return value;
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof UOctet) {
      UOctet d = (UOctet) obj;
      return d.value == value;
    }
    return false;
  }
  
  public String toString() {
    return "" + value;
  }
  
  public UShort getAreaNumber() {
    return MALHelper.MAL_AREA_NUMBER;
  }

  public UOctet getAreaVersion() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_VERSION;
  }
  
  public UShort getServiceNumber() {
    return MALService.NULL_SERVICE_NUMBER;
  }

  public Integer getTypeShortForm() {
    return TYPE_SHORT_FORM;
  }

  public Long getShortForm() {
    return SHORT_FORM;
  }
  
  public void encode(MALEncoder encoder) throws MALException {
    encoder.encodeUOctet(this);
  }

  public Element decode(MALDecoder decoder) throws MALException {
    return decoder.decodeUOctet();
  }
  
  public Element createElement() {
    return new UOctet();
  }

  public int compareTo(UOctet uoctet) {
    return new Short(value).compareTo(uoctet.getValue());
  }
}
