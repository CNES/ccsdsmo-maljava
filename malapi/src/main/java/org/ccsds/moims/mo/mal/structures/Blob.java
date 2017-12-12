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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

/**
 * A {@code Blob} represents a MAL Blob.
 */
public class Blob implements Attribute {

  /**
   * Absolute short form.
   */
  public static final Long SHORT_FORM = BLOB_SHORT_FORM;
  
  /**
   * Type short form.
   */
  public static final Integer TYPE_SHORT_FORM = BLOB_TYPE_SHORT_FORM;
  
  private static int getByteArrayLength(byte[] bytes) {
    if (bytes == null) return 0;
    else return bytes.length;
  }

  private byte[] blobValue;
  
  private int offset;
  
  private int length;
  
  private URL url;
  
  private boolean attached = false;

  /**
   * Constructs a {@code Blob} without any parameters.
   */
  public Blob() {
  }

  /**
   * Constructs a {@code Blob} with a byte array.
   * @param blobValue the value of this {@code Blob}
   * @throws IllegalArgumentException if the array is {@code null}
   */
  public Blob(byte[] blobValue) throws IllegalArgumentException {
    this(blobValue, 0, getByteArrayLength(blobValue));
  }
  
 /**
  * Constructs a {@code Blob} with a byte array delimited
  * by an offset and a length.
  * @param blobValue the value of this {@code Blob}
  * @param offset offset within the array of the first byte
  * @param length number of bytes belonging to this {@code Blob}
  * @throws IllegalArgumentException if the array is {@code null}
  */
  public Blob(byte[] blobValue, int offset, int length) throws IllegalArgumentException {
    if (blobValue == null) throw new IllegalArgumentException("Null value");
    this.blobValue = blobValue;
    this.offset = offset;
    this.length = length;
    this.url = null;
  }

  /**
   * Constructs a {@code Blob} with a URL.
   * @param url URL which designated content shall be loaded in this {@code Blob}
   * @throws MALException if the URL is {@code null}
   */
  public Blob(String url) throws MALException {
    if (url == null) throw new IllegalArgumentException("Null value");
    this.blobValue = null;
    try {
      this.url = new URL(url);
    } catch (MalformedURLException e) {
      throw new MALException("Malformed URL:" + url, e);
    }
    attached = true;
  }

  /**
   * Returns the byte array offset.
   * @return the byte array offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns the byte array length.
   * @return the byte array length
   */
  public int getLength() {
    return length;
  }

  /**
   * Indicates whether the {@code Blob} contains a URL or not.
   * @return {@code true} if the {@code Blob} contains a URL
   */
  public boolean isURLBased() {
    return url != null;
  }

  /**
   * Returns the {@code Blob} value as a byte array.
   * @return the {@code Blob} value as a byte array
   */
  public byte[] getValue() throws MALException {
    if (! isURLBased()) {
      return blobValue;
    } else {
      byte[] buf;
      try {
        URLConnection connection = url.openConnection();
        InputStream is = new BufferedInputStream(connection.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int readByte;
        while ((readByte = is.read()) != -1) {
          baos.write(readByte);
        }
        buf = baos.toByteArray();
        is.close();
      } catch (IOException exc) {
        throw new MALException("Read failed", exc);
      }
      return buf;
    }
  }

  /**
   * Returns the URL.
   * @return the URL
   */
  public String getURL() {
    return url.toString();
  }

  /**
   * Detach the {@code Blob}. Every resource that is attached
   * to this {@code Blob} can be deleted.
   */
  public void detach() {
    attached = false;
  }

  public int hashCode() {
    if (! isURLBased()) {
      if (blobValue == null)
        return 0;
      else
        return blobValue.length;
    } else {
      return (int) url.hashCode();
    }
  }

  public boolean equals(Object obj) {
    if (obj instanceof Blob) {
      Blob b = (Blob) obj;
      try {
        byte[] bytes = getValue();
        byte[] toCompare = b.getValue();
        if (bytes.length != toCompare.length)
          return false;
        for (int i = 0; i < toCompare.length; i++) {
          if (toCompare[i] != bytes[i])
            return false;
        }
        return true;
      } catch (MALException e) {
        return false;
      }
    }
    return false;
  }
  
  /**
   * Deletes the resources that have been detached.
   * @throws MALException if an error occurs
   */
  public void delete() throws MALException {
    if (attached && (null != url)) {
      attached = false;
      if (url.getProtocol().equals("file")) {
        try {
          new File(url.toURI()).delete();
        } catch (URISyntaxException e) {
          throw new MALException("", e);
        }
      }
    }
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("(");
    buf.append(super.toString());
    buf.append(",url=");
    buf.append(url);
    buf.append(",blobValue=");
    try {
      byte[] bytes  = getValue();
      if (bytes != null) {
        buf.append(new String(getValue()));
      } else {
        buf.append("NULL");
      }
    } catch (MALException e) {
      buf.append(e.toString());
    }
    buf.append(",attached=");
    buf.append(attached);
    buf.append(",offset=");
    buf.append(offset);
    buf.append(",length=");
    buf.append(length);
    buf.append(")");
    return buf.toString();
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
    encoder.encodeBlob(this);
  }

  public Element decode(MALDecoder decoder) throws MALException {
    return decoder.decodeBlob();
  }

  public Element createElement() {
    return new Blob();
  }
}
