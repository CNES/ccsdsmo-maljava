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

/** IdBooleanPair is a simple pair type of an identifier and Boolean value. */
public final class IdBooleanPair implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(27);

  public static final Long SHORT_FORM = new Long(281474993487899L);

  /** The Identifier value. **/
  private org.ccsds.moims.mo.mal.structures.Identifier id;

  /** The Boolean value. **/
  private Boolean value;

  public IdBooleanPair() { }

  public IdBooleanPair(org.ccsds.moims.mo.mal.structures.Identifier id, Boolean value) {
    this.id = id;
    this.value = value;
  }

  /** The Identifier value. **/
  public org.ccsds.moims.mo.mal.structures.Identifier getId() {
    return id;
  }

  /** The Identifier value. **/
  public void setId(org.ccsds.moims.mo.mal.structures.Identifier id) {
    this.id = id;
  }

  /** The Boolean value. **/
  public Boolean getValue() {
    return value;
  }

  /** The Boolean value. **/
  public void setValue(Boolean value) {
    this.value = value;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeNullableIdentifier(id);
    encoder.encodeNullableBoolean(value);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    id = decoder.decodeNullableIdentifier();
    value = decoder.decodeNullableBoolean();
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof IdBooleanPair) {
      IdBooleanPair other = (IdBooleanPair) obj;
      if (id == null) {
        if (other.id != null) return false;
      } else {
        if (! id.equals(other.id)) return false;
      }
      if (value == null) {
        if (other.value != null) return false;
      } else {
        if (! value.equals(other.value)) return false;
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
    buf.append(",id");
    buf.append('=');
    buf.append(id);
    buf.append(",value");
    buf.append('=');
    buf.append(value);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.IdBooleanPair();
  }

}