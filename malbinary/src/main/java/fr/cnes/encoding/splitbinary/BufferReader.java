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
package fr.cnes.encoding.splitbinary;

import fr.cnes.encoding.base.Reader;

public class BufferReader implements Reader {
  private byte[] buf;
  // Offset of bitfield in buffer.
  private int bfoff;
  // Index of current decoded bit in bitfield.
  private int bfidx;
  // Number of bits in bitfield.
  private int bflen;
  // Index of current decoded byte in buffer.
  private int index;
  
  public BufferReader(byte[] buf) throws Exception {
    this(buf, 0);
  }
  
  public BufferReader(byte[] buf, int offset) throws Exception {
    this.buf = buf;
    index = offset;
	bflen = getUnsignedVarInt();
	bfoff = index;
	bfidx = 0;
    index = bfoff + bflen;
  }

  public int getUnsignedVarInt() throws Exception {
	  int value = 0;
	  int i;
	  int b;
	  for (i = 0; ((b = getByte()) & 0x80) != 0; i += 7) {
		  value |= (b & 0x7f) << i;
	  }
	  return value | b << i;
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

  public boolean getBoolean() throws Exception {
	  if (bflen <= bfidx) return false;
	  
	  boolean value = (((buf[bfoff + (bfidx >> 3)] >> (bfidx%8)) & 1) == SplitBinary.TRUE);
	  bfidx += 1;
	  return value;
  }

}
