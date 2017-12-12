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

/** The UpdateHeader structure is used by updates using the PUBSUB interaction pattern. It holds information that identifies a single update. */
public final class UpdateHeader implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(26);

  public static final Long SHORT_FORM = new Long(281474993487898L);

  /** Creation timestamp of the update. **/
  private org.ccsds.moims.mo.mal.structures.Time timestamp;

  /** URI of the source of the update, usually a PUBSUB provider. **/
  private org.ccsds.moims.mo.mal.structures.URI sourceURI;

  /** Type of update being reported. **/
  private org.ccsds.moims.mo.mal.structures.UpdateType updateType;

  /** The key of the entity; shall not contain the wildcard value. **/
  private org.ccsds.moims.mo.mal.structures.EntityKey key;

  public UpdateHeader() { }

  public UpdateHeader(org.ccsds.moims.mo.mal.structures.Time timestamp, org.ccsds.moims.mo.mal.structures.URI sourceURI, org.ccsds.moims.mo.mal.structures.UpdateType updateType, org.ccsds.moims.mo.mal.structures.EntityKey key) {
    if (timestamp == null) throw new IllegalArgumentException("Null field 'timestamp'");
    if (sourceURI == null) throw new IllegalArgumentException("Null field 'sourceURI'");
    if (updateType == null) throw new IllegalArgumentException("Null field 'updateType'");
    if (key == null) throw new IllegalArgumentException("Null field 'key'");
    this.timestamp = timestamp;
    this.sourceURI = sourceURI;
    this.updateType = updateType;
    this.key = key;
  }

  /** Creation timestamp of the update. **/
  public org.ccsds.moims.mo.mal.structures.Time getTimestamp() {
    return timestamp;
  }

  /** Creation timestamp of the update. **/
  public void setTimestamp(org.ccsds.moims.mo.mal.structures.Time timestamp) {
    if (timestamp == null) throw new IllegalArgumentException("Null field 'timestamp'");
    this.timestamp = timestamp;
  }

  /** URI of the source of the update, usually a PUBSUB provider. **/
  public org.ccsds.moims.mo.mal.structures.URI getSourceURI() {
    return sourceURI;
  }

  /** URI of the source of the update, usually a PUBSUB provider. **/
  public void setSourceURI(org.ccsds.moims.mo.mal.structures.URI sourceURI) {
    if (sourceURI == null) throw new IllegalArgumentException("Null field 'sourceURI'");
    this.sourceURI = sourceURI;
  }

  /** Type of update being reported. **/
  public org.ccsds.moims.mo.mal.structures.UpdateType getUpdateType() {
    return updateType;
  }

  /** Type of update being reported. **/
  public void setUpdateType(org.ccsds.moims.mo.mal.structures.UpdateType updateType) {
    if (updateType == null) throw new IllegalArgumentException("Null field 'updateType'");
    this.updateType = updateType;
  }

  /** The key of the entity; shall not contain the wildcard value. **/
  public org.ccsds.moims.mo.mal.structures.EntityKey getKey() {
    return key;
  }

  /** The key of the entity; shall not contain the wildcard value. **/
  public void setKey(org.ccsds.moims.mo.mal.structures.EntityKey key) {
    if (key == null) throw new IllegalArgumentException("Null field 'key'");
    this.key = key;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeTime(timestamp);
    encoder.encodeURI(sourceURI);
    encoder.encodeElement(updateType);
    encoder.encodeElement(key);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    timestamp = decoder.decodeTime();
    sourceURI = decoder.decodeURI();
    updateType = (org.ccsds.moims.mo.mal.structures.UpdateType) decoder.decodeElement(org.ccsds.moims.mo.mal.structures.UpdateType.fromOrdinal(1));
    key = (org.ccsds.moims.mo.mal.structures.EntityKey) decoder.decodeElement(new org.ccsds.moims.mo.mal.structures.EntityKey());
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
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((sourceURI == null) ? 0 : sourceURI.hashCode());
    result = prime * result + ((updateType == null) ? 0 : updateType.hashCode());
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof UpdateHeader) {
      UpdateHeader other = (UpdateHeader) obj;
      if (timestamp == null) {
        if (other.timestamp != null) return false;
      } else {
        if (! timestamp.equals(other.timestamp)) return false;
      }
      if (sourceURI == null) {
        if (other.sourceURI != null) return false;
      } else {
        if (! sourceURI.equals(other.sourceURI)) return false;
      }
      if (updateType == null) {
        if (other.updateType != null) return false;
      } else {
        if (! updateType.equals(other.updateType)) return false;
      }
      if (key == null) {
        if (other.key != null) return false;
      } else {
        if (! key.equals(other.key)) return false;
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
    buf.append(",timestamp");
    buf.append('=');
    buf.append(timestamp);
    buf.append(",sourceURI");
    buf.append('=');
    buf.append(sourceURI);
    buf.append(",updateType");
    buf.append('=');
    buf.append(updateType);
    buf.append(",key");
    buf.append('=');
    buf.append(key);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.UpdateHeader();
  }

}