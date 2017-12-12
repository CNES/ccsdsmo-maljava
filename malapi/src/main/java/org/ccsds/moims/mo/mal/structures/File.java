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

/** The File structure represents a File and holds details about a File. It can also, optionally, hold a BLOB of the file data. The file type is denoted using the internet MIME media types, the list of official MIME types is held at http://www.iana.org/assignments/media-types/index.html. */
public final class File implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(30);

  public static final Long SHORT_FORM = new Long(281474993487902L);

  /** The file name. **/
  private org.ccsds.moims.mo.mal.structures.Identifier name;

  /** The MIME type of the file, NULL if not known. **/
  private String mimeType;

  /** The creation timestamp of the file, NULL if not known. **/
  private org.ccsds.moims.mo.mal.structures.Time creationDate;

  /** The last modification tiestamp of the file, NULL if not known. **/
  private org.ccsds.moims.mo.mal.structures.Time modificationDate;

  /** The size of the file, NULL if not known. **/
  private org.ccsds.moims.mo.mal.structures.ULong size;

  /** The contents of the file, NULL if not supplied. **/
  private org.ccsds.moims.mo.mal.structures.Blob content;

  /** A list of extra metadata for the file. **/
  private org.ccsds.moims.mo.mal.structures.NamedValueList metaData;

  public File() { }

  public File(org.ccsds.moims.mo.mal.structures.Identifier name, String mimeType, org.ccsds.moims.mo.mal.structures.Time creationDate, org.ccsds.moims.mo.mal.structures.Time modificationDate, org.ccsds.moims.mo.mal.structures.ULong size, org.ccsds.moims.mo.mal.structures.Blob content, org.ccsds.moims.mo.mal.structures.NamedValueList metaData) {
    if (name == null) throw new IllegalArgumentException("Null field 'name'");
    this.name = name;
    this.mimeType = mimeType;
    this.creationDate = creationDate;
    this.modificationDate = modificationDate;
    this.size = size;
    this.content = content;
    this.metaData = metaData;
  }

  /** The file name. **/
  public org.ccsds.moims.mo.mal.structures.Identifier getName() {
    return name;
  }

  /** The file name. **/
  public void setName(org.ccsds.moims.mo.mal.structures.Identifier name) {
    if (name == null) throw new IllegalArgumentException("Null field 'name'");
    this.name = name;
  }

  /** The MIME type of the file, NULL if not known. **/
  public String getMimeType() {
    return mimeType;
  }

  /** The MIME type of the file, NULL if not known. **/
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /** The creation timestamp of the file, NULL if not known. **/
  public org.ccsds.moims.mo.mal.structures.Time getCreationDate() {
    return creationDate;
  }

  /** The creation timestamp of the file, NULL if not known. **/
  public void setCreationDate(org.ccsds.moims.mo.mal.structures.Time creationDate) {
    this.creationDate = creationDate;
  }

  /** The last modification tiestamp of the file, NULL if not known. **/
  public org.ccsds.moims.mo.mal.structures.Time getModificationDate() {
    return modificationDate;
  }

  /** The last modification tiestamp of the file, NULL if not known. **/
  public void setModificationDate(org.ccsds.moims.mo.mal.structures.Time modificationDate) {
    this.modificationDate = modificationDate;
  }

  /** The size of the file, NULL if not known. **/
  public org.ccsds.moims.mo.mal.structures.ULong getSize() {
    return size;
  }

  /** The size of the file, NULL if not known. **/
  public void setSize(org.ccsds.moims.mo.mal.structures.ULong size) {
    this.size = size;
  }

  /** The contents of the file, NULL if not supplied. **/
  public org.ccsds.moims.mo.mal.structures.Blob getContent() {
    return content;
  }

  /** The contents of the file, NULL if not supplied. **/
  public void setContent(org.ccsds.moims.mo.mal.structures.Blob content) {
    this.content = content;
  }

  /** A list of extra metadata for the file. **/
  public org.ccsds.moims.mo.mal.structures.NamedValueList getMetaData() {
    return metaData;
  }

  /** A list of extra metadata for the file. **/
  public void setMetaData(org.ccsds.moims.mo.mal.structures.NamedValueList metaData) {
    this.metaData = metaData;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeIdentifier(name);
    encoder.encodeNullableString(mimeType);
    encoder.encodeNullableTime(creationDate);
    encoder.encodeNullableTime(modificationDate);
    encoder.encodeNullableULong(size);
    encoder.encodeNullableBlob(content);
    encoder.encodeNullableElement(metaData);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    name = decoder.decodeIdentifier();
    mimeType = decoder.decodeNullableString();
    creationDate = decoder.decodeNullableTime();
    modificationDate = decoder.decodeNullableTime();
    size = decoder.decodeNullableULong();
    content = decoder.decodeNullableBlob();
    metaData = (org.ccsds.moims.mo.mal.structures.NamedValueList) decoder.decodeNullableElement(new org.ccsds.moims.mo.mal.structures.NamedValueList());
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
    result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
    result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
    result = prime * result + ((modificationDate == null) ? 0 : modificationDate.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result + ((content == null) ? 0 : content.hashCode());
    result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof File) {
      File other = (File) obj;
      if (name == null) {
        if (other.name != null) return false;
      } else {
        if (! name.equals(other.name)) return false;
      }
      if (mimeType == null) {
        if (other.mimeType != null) return false;
      } else {
        if (! mimeType.equals(other.mimeType)) return false;
      }
      if (creationDate == null) {
        if (other.creationDate != null) return false;
      } else {
        if (! creationDate.equals(other.creationDate)) return false;
      }
      if (modificationDate == null) {
        if (other.modificationDate != null) return false;
      } else {
        if (! modificationDate.equals(other.modificationDate)) return false;
      }
      if (size == null) {
        if (other.size != null) return false;
      } else {
        if (! size.equals(other.size)) return false;
      }
      if (content == null) {
        if (other.content != null) return false;
      } else {
        if (! content.equals(other.content)) return false;
      }
      if (metaData == null) {
        if (other.metaData != null) return false;
      } else {
        if (! metaData.equals(other.metaData)) return false;
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
    buf.append(",mimeType");
    buf.append('=');
    buf.append(mimeType);
    buf.append(",creationDate");
    buf.append('=');
    buf.append(creationDate);
    buf.append(",modificationDate");
    buf.append('=');
    buf.append(modificationDate);
    buf.append(",size");
    buf.append('=');
    buf.append(size);
    buf.append(",content");
    buf.append('=');
    buf.append(content);
    buf.append(",metaData");
    buf.append('=');
    buf.append(metaData);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.File();
  }

}