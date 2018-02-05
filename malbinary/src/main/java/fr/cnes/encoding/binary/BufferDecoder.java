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

import java.io.IOException;
import java.math.BigInteger;

public class BufferDecoder implements Decoder {

  private Reader buffer;
  
  private boolean varintSupported;

  public BufferDecoder(Reader buffer) {
    this.buffer = buffer;
    varintSupported = true;
  }

  public boolean isVarintSupported() {
    return varintSupported;
  }

  public void setVarintSupported(boolean varintSupported) {
    this.varintSupported = varintSupported;
  }

  public byte readByte() throws Exception{
    return buffer.getByte();
  }
  
  public short readSignedShort() throws Exception {
    if (varintSupported) {
      int i = readUnsignedVarInt();
      return (short) ((i >>> 1) ^ -(i & 1));
    } else {
      return read16();
    }
  }
  
  public short readUnsignedShort() throws Exception {
    if (varintSupported) {
      return (short) readUnsignedVarInt();
    } else {
      return read16();
    }
  }
  
  public int readSignedInt() throws Exception {
    if (varintSupported) {
      int i = readUnsignedVarInt();
      return ((i >>> 1) ^ -(i & 1));
    } else {
      return read32();
    }
  }

  public int readUnsignedInt() throws Exception {
    if (varintSupported) {
      return readUnsignedVarInt();
    } else {
      return read32();
    }
  }

  public int readUnsignedVarInt() throws Exception {
    int value = 0;
    int i;
    int b;
    for (i = 0; ((b = buffer.getByte()) & 0x80) != 0; i += 7) {
      value |= (b & 0x7f) << i;
    }
    return value | b << i;
  }

  public long readUnsignedVarLong() throws Exception {
    long value = 0L;
    int i;
    long b;
    for (i = 0; ((b = buffer.getByte()) & 128L) != 0L; i += 7) {
      value |= (b & 127L) << i;
    }
    return value | b << i;
  }
  
  public String readNullableString() throws Exception{
    if (isNull()) return null;
    else return readString();
  }

  public String readString() throws Exception{
    int length = readUnsignedInt();
    return readString(length);
  }
  
  public byte[] readNullableByteArray() throws Exception {
    if (isNull()) return null;
    else return readByteArray();
  }

  public byte[] readByteArray() throws Exception {
    int length = readUnsignedInt();
    return readByteArray(length);
  }
  
  public long readSignedLong() throws Exception {
    if (varintSupported) {
      long l = readUnsignedVarLong();
      return ((l >>> 1) ^ -(l & 1));
    } else {
      return read64();
    }
  }

  public BigInteger readUnsignedLong() throws Exception {
    if (varintSupported) {
      long l = readUnsignedVarLong();
      byte[] bigIntegerBytes = new byte[9];
      bigIntegerBytes[0] = 0x00;
      bigIntegerBytes[1] = ((byte) (l >>>  56));
      bigIntegerBytes[2] = ((byte) (l >>> 48));
      bigIntegerBytes[3] = ((byte) (l >>> 40));
      bigIntegerBytes[4] = ((byte) (l >>> 32));
      bigIntegerBytes[5] = ((byte) (l >>> 24));
      bigIntegerBytes[6] = ((byte) (l >>> 16));
      bigIntegerBytes[7] = ((byte) (l >>> 8));
      bigIntegerBytes[8] = ((byte) (l >>> 0));
      return new BigInteger(bigIntegerBytes);
    } else {
      byte[] bigIntegerBytes = new byte[9];
      bigIntegerBytes[0] = 0x00;
      bigIntegerBytes[1] = buffer.getByte();
      bigIntegerBytes[2] = buffer.getByte();
      bigIntegerBytes[3] = buffer.getByte();
      bigIntegerBytes[4] = buffer.getByte();
      bigIntegerBytes[5] = buffer.getByte();
      bigIntegerBytes[6] = buffer.getByte();
      bigIntegerBytes[7] = buffer.getByte();
      bigIntegerBytes[8] = buffer.getByte();
      return new BigInteger(bigIntegerBytes);
    }
  }
  
  public short read16() throws Exception {
    return (short) (((buffer.getByte() &0xFF) << 8) | (buffer.getByte() &0xFF));
  }
  
  public int read24() throws Exception {
    return ((buffer.getByte() &0xFF) << 16) |
        ((buffer.getByte() &0xFF) << 8) | (buffer.getByte() &0xFF);
  }
  
  public int read32() throws Exception {
    return ((buffer.getByte() &0xFF) << 24) | ((buffer.getByte() &0xFF) << 16) |
            ((buffer.getByte() &0xFF) << 8) | (buffer.getByte() &0xFF);
  }
  
  public long read64() throws Exception {
    return ((((long) buffer.getByte()) &0xFFL) << 56) | ((((long) buffer.getByte()) &0xFFL) << 48) |
        ((((long) buffer.getByte()) &0xFFL) << 40) | ((((long) buffer.getByte()) &0xFFL) << 32) |
        ((((long) buffer.getByte()) &0xFFL) << 24) | ((((long) buffer.getByte()) &0xFFL) << 16) |
        ((((long) buffer.getByte()) &0xFFL) << 8) | (((long) buffer.getByte()) &0xFFL);
  }
  
  public boolean readBoolean() throws Exception {
      return 1 != buffer.getByte() ? Boolean.FALSE : Boolean.TRUE;
  }

  public BigInteger readULong() throws Exception {
    return BigInteger.valueOf(readUnsignedVarLong());
  }

  public String readString(int length) throws Exception {
    if (length > 0) {
      return buffer.getString(length);
    } else if (length == 0) {
      return "";
    } else {
      return null;
    }
  }
  
  public byte[] readByteArray(int length) throws Exception {
    if (length == 0) {
      return new byte[0];
    } else {
      return buffer.getByteArray(length);
    }
  }

  public boolean isNull() throws Exception {
    byte b = readByte();
    return (b == Encoder.NULL);
  }

  public void close() throws IOException {
    // Do nothing
  }
}
