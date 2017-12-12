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

/** The NamedValue structure represents a simple pair type of an identifier and abstract attribute value. */
public final class NamedValue implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(29);

  public static final Long SHORT_FORM = new Long(281474993487901L);

  /** The Identifier value. **/
  private org.ccsds.moims.mo.mal.structures.Identifier name;

  /** The Attribute value. **/
  private org.ccsds.moims.mo.mal.structures.Attribute value;

  public NamedValue() { }

  public NamedValue(org.ccsds.moims.mo.mal.structures.Identifier name, org.ccsds.moims.mo.mal.structures.Attribute value) {
    this.name = name;
    this.value = value;
  }

  /** The Identifier value. **/
  public org.ccsds.moims.mo.mal.structures.Identifier getName() {
    return name;
  }

  /** The Identifier value. **/
  public void setName(org.ccsds.moims.mo.mal.structures.Identifier name) {
    this.name = name;
  }

  /** The Attribute value. **/
  public org.ccsds.moims.mo.mal.structures.Attribute getValue() {
    return value;
  }

  /** The Attribute value. **/
  public void setValue(org.ccsds.moims.mo.mal.structures.Attribute value) {
    this.value = value;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeNullableIdentifier(name);
    encoder.encodeNullableAttribute(value);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    name = decoder.decodeNullableIdentifier();
    value = decoder.decodeNullableAttribute();
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
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof NamedValue) {
      NamedValue other = (NamedValue) obj;
      if (name == null) {
        if (other.name != null) return false;
      } else {
        if (! name.equals(other.name)) return false;
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
    buf.append(",name");
    buf.append('=');
    buf.append(name);
    buf.append(",value");
    buf.append('=');
    buf.append(value);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.NamedValue();
  }

}