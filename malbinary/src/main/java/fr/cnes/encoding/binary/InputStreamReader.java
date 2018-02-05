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
package fr.cnes.encoding.binary;

import java.io.InputStream;

public class InputStreamReader implements Reader {
  
  private InputStream is;
  
  private byte[] buffer;
  
  private int index;
  
  private int size;
  
  public InputStreamReader(InputStream is, byte[] buffer) {
    this.is = is;
    this.buffer = buffer;
    index = 0;
    size = 0;
  }
  
  private void read(int byteNumber) throws Exception {
    size = 0;
    int i = 0;
    while (i < byteNumber) {
      i += is.read(buffer, size, buffer.length - size);
      size += i;
    }
  }

  public byte getByte() throws Exception {
    if (index == size) {
      read(1);
    }
    return buffer[index++];
  }

  public String getString(int length) throws Exception {
    return new String(getByteArray(length), Encoder.utf8);
  }

  public byte[] getByteArray(int length) throws Exception {
    byte[] res = new byte[length];
    int remaining = size - index;
    int offset = 0;
    while (remaining < length) {
      System.arraycopy(buffer, index, res, offset, remaining);
      index = size;
      length -= remaining;
      offset += remaining;
      if (length > buffer.length) {
        read(buffer.length);
      } else {
        read(length);
      }
      remaining = size - index;
    }
    System.arraycopy(buffer, index, res, offset, length);
    return res;
  }

}
