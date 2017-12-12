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
package fr.cnes.maljoram.encoding;

public class BufferReader implements Reader {
  
  private byte[] buf;
  
  private int index;

  public BufferReader(byte[] buf) {
    this(buf, 0);
  }
  
  public BufferReader(byte[] buf, int offset) {
    super();
    this.buf = buf;
    index = offset;
  }

  public byte getByte() {
    return buf[index++];
  }

  public String getString(int length) {
    String s = new String(buf, index, length);
    index += length;
    return s;
  }

  public byte[] getByteArray(int length) {
    byte[] res = new byte[length];
    System.arraycopy(buf, index, res, 0, length);
    index += length;
    return res;
  }
  
  public int getIndex() {
    return index;
  }
  
}
