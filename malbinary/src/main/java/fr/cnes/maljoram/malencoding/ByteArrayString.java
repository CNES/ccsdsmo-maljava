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
package fr.cnes.maljoram.malencoding;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Union;

public class ByteArrayString extends Union {
  
  private static final String EMPTY_STRING = "";
  
  private byte[] bytes;
  
  private String value;
  
  public ByteArrayString(byte[] bytes) {
    super(EMPTY_STRING);
    this.bytes = bytes;
  }
  
  public byte[] getBytes() {
    return bytes;
  }

  private void resolveString() {
    if (value == null) {
      value = new String(bytes);
    }
  }
  
  public String getStringValue() {
    resolveString();
    return value;
  }
  
  public Object getValue() {
    resolveString();
    return value;
  }
  
  public int hashCode() {
    resolveString();
    return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof Union) {
      Union u = (Union) obj;
      if (u.getStringValue() == null) return false;
      resolveString();
      return u.getStringValue().equals(value);
    } else {
      return false;
    }
  }
  
  public String toString() {
    resolveString();
    return value.toString();
  }
  
  public void encode(MALEncoder encoder) throws MALException {
    resolveString();
    encoder.encodeString((String) value);
  }

  public Element decode(MALDecoder decoder) throws MALException {
    return new Union(decoder.decodeString());
  }
  
  public Element createElement() {
    return new Union(EMPTY_STRING);
  }
}
