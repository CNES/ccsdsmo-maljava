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
 * The Java representation of the MAL type FineTime.
 */
public class FineTime implements Attribute, Comparable<FineTime> {
  
  /**
   * Absolute short form of the MAL type FineTime.
   */
  public static final Long SHORT_FORM = FINETIME_SHORT_FORM;
  
  /**
   * Type short form of the MAL type FineTime.
   */
  public static final Integer TYPE_SHORT_FORM = FINETIME_TYPE_SHORT_FORM;
  
  /**
   * Short name of the MAL type FineTime.
   */
  public static final String SHORT_NAME = new String("C");
  
  private long value;
  
  /**
   * Public empty constructor.
   */
  public FineTime() {}
  
  /**
   * Constructs an instance of <code>FineTime</code>
   * given the FineTime value
   * @param value the FineTime value
   */
  public FineTime(long value) {
    this.value = value;
  }

  /**
   * Returns the FineTime value
   * @return the FineTime value
   */
  public long getValue() {
    return value;
  }
  
  public void setValue(long value) {
    this.value = value;
  }
  
  /**
   * Returns the FineTime short name
   * @return the FineTime short name
   */
  public String getShortName() {
    return SHORT_NAME;
  }
  
  public int hashCode() {
    return (int) value;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof FineTime))
      return false;
    FineTime other = (FineTime) obj;
    if (value != other.value)
      return false;
    return true;
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
    encoder.encodeFineTime(this);
  }


  public Element decode(MALDecoder decoder) throws MALException {
    return decoder.decodeFineTime();
  }
  

  public Element createElement() {
    return new FineTime();
  }


  public int compareTo(FineTime fineTime) {
    return new Long(value).compareTo(fineTime.getValue());
  }
}
