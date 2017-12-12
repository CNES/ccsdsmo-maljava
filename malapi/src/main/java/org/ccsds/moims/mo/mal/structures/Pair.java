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

/** Pair is a simple composite structure for holding pairs. The pairs can be user-defined attributes. */
public final class Pair implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(28);

  public static final Long SHORT_FORM = new Long(281474993487900L);

  /** The attribute value for the first element of this pair. **/
  private org.ccsds.moims.mo.mal.structures.Attribute first;

  /** The attribute value for the second element of this pair. **/
  private org.ccsds.moims.mo.mal.structures.Attribute second;

  public Pair() { }

  public Pair(org.ccsds.moims.mo.mal.structures.Attribute first, org.ccsds.moims.mo.mal.structures.Attribute second) {
    this.first = first;
    this.second = second;
  }

  /** The attribute value for the first element of this pair. **/
  public org.ccsds.moims.mo.mal.structures.Attribute getFirst() {
    return first;
  }

  /** The attribute value for the first element of this pair. **/
  public void setFirst(org.ccsds.moims.mo.mal.structures.Attribute first) {
    this.first = first;
  }

  /** The attribute value for the second element of this pair. **/
  public org.ccsds.moims.mo.mal.structures.Attribute getSecond() {
    return second;
  }

  /** The attribute value for the second element of this pair. **/
  public void setSecond(org.ccsds.moims.mo.mal.structures.Attribute second) {
    this.second = second;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeNullableAttribute(first);
    encoder.encodeNullableAttribute(second);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    first = decoder.decodeNullableAttribute();
    second = decoder.decodeNullableAttribute();
    return this;
  }

  public org.ccsds.moims.mo.mal.structures.UShort getAreaNumber() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_NUMBER;
  }

  public org.ccsds.moims.mo.mal.structures.UOctet getAreaVersion() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_VERSION;
  }

  public org.ccsds.moims.mo.mal.structures.UShort getServiceNumber() {
    return org.ccsds.moims.mo.mal.MALService.NULL_SERVICE_NUMBER;
  }

  public Long getShortForm() {
    return SHORT_FORM;
  }

  public Integer getTypeShortForm() {
    return TYPE_SHORT_FORM;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((first == null) ? 0 : first.hashCode());
    result = prime * result + ((second == null) ? 0 : second.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Pair) {
      Pair other = (Pair) obj;
      if (first == null) {
        if (other.first != null) return false;
      } else {
        if (! first.equals(other.first)) return false;
      }
      if (second == null) {
        if (other.second != null) return false;
      } else {
        if (! second.equals(other.second)) return false;
      }
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append('(');
    buf.append(super.toString());
    buf.append(",first");
    buf.append('=');
    buf.append(first);
    buf.append(",second");
    buf.append('=');
    buf.append(second);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.Pair();
  }

}