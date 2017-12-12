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

/** The EntityKey structure is used to identify an entity in the PUBSUB interaction pattern. */
public final class EntityKey implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(25);

  public static final Long SHORT_FORM = new Long(281474993487897L);

  /** The first sub-key of the key. **/
  private org.ccsds.moims.mo.mal.structures.Identifier firstSubKey;

  /** The second sub-key of the key.
 **/
  private Long secondSubKey;

  /** The third sub-key of the key.
 **/
  private Long thirdSubKey;

  /** The fourth sub-key of the key.
 **/
  private Long fourthSubKey;

  public EntityKey() { }

  public EntityKey(org.ccsds.moims.mo.mal.structures.Identifier firstSubKey, Long secondSubKey, Long thirdSubKey, Long fourthSubKey) {
    this.firstSubKey = firstSubKey;
    this.secondSubKey = secondSubKey;
    this.thirdSubKey = thirdSubKey;
    this.fourthSubKey = fourthSubKey;
  }

  /** The first sub-key of the key. **/
  public org.ccsds.moims.mo.mal.structures.Identifier getFirstSubKey() {
    return firstSubKey;
  }

  /** The first sub-key of the key. **/
  public void setFirstSubKey(org.ccsds.moims.mo.mal.structures.Identifier firstSubKey) {
    this.firstSubKey = firstSubKey;
  }

  /** The second sub-key of the key.
 **/
  public Long getSecondSubKey() {
    return secondSubKey;
  }

  /** The second sub-key of the key.
 **/
  public void setSecondSubKey(Long secondSubKey) {
    this.secondSubKey = secondSubKey;
  }

  /** The third sub-key of the key.
 **/
  public Long getThirdSubKey() {
    return thirdSubKey;
  }

  /** The third sub-key of the key.
 **/
  public void setThirdSubKey(Long thirdSubKey) {
    this.thirdSubKey = thirdSubKey;
  }

  /** The fourth sub-key of the key.
 **/
  public Long getFourthSubKey() {
    return fourthSubKey;
  }

  /** The fourth sub-key of the key.
 **/
  public void setFourthSubKey(Long fourthSubKey) {
    this.fourthSubKey = fourthSubKey;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeNullableIdentifier(firstSubKey);
    encoder.encodeNullableLong(secondSubKey);
    encoder.encodeNullableLong(thirdSubKey);
    encoder.encodeNullableLong(fourthSubKey);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    firstSubKey = decoder.decodeNullableIdentifier();
    secondSubKey = decoder.decodeNullableLong();
    thirdSubKey = decoder.decodeNullableLong();
    fourthSubKey = decoder.decodeNullableLong();
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
    result = prime * result + ((firstSubKey == null) ? 0 : firstSubKey.hashCode());
    result = prime * result + ((secondSubKey == null) ? 0 : secondSubKey.hashCode());
    result = prime * result + ((thirdSubKey == null) ? 0 : thirdSubKey.hashCode());
    result = prime * result + ((fourthSubKey == null) ? 0 : fourthSubKey.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof EntityKey) {
      EntityKey other = (EntityKey) obj;
      if (firstSubKey == null) {
        if (other.firstSubKey != null) return false;
      } else {
        if (! firstSubKey.equals(other.firstSubKey)) return false;
      }
      if (secondSubKey == null) {
        if (other.secondSubKey != null) return false;
      } else {
        if (! secondSubKey.equals(other.secondSubKey)) return false;
      }
      if (thirdSubKey == null) {
        if (other.thirdSubKey != null) return false;
      } else {
        if (! thirdSubKey.equals(other.thirdSubKey)) return false;
      }
      if (fourthSubKey == null) {
        if (other.fourthSubKey != null) return false;
      } else {
        if (! fourthSubKey.equals(other.fourthSubKey)) return false;
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
    buf.append(",firstSubKey");
    buf.append('=');
    buf.append(firstSubKey);
    buf.append(",secondSubKey");
    buf.append('=');
    buf.append(secondSubKey);
    buf.append(",thirdSubKey");
    buf.append('=');
    buf.append(thirdSubKey);
    buf.append(",fourthSubKey");
    buf.append('=');
    buf.append(fourthSubKey);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.EntityKey();
  }

}