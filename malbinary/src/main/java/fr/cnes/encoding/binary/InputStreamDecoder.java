/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 - 2018 CNES
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
import java.io.InputStream;
import java.math.BigInteger;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.Decoder;

public class InputStreamDecoder implements Decoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(InputStreamDecoder.class.getName());

  public static final String EMPTY_STRING = "";
  
  public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

  private byte[] buf;
  
  private InputStream is;
  
  private boolean varintSupported;
  
  public InputStreamDecoder(InputStream is) {
    this.is = is;
    buf = new byte[8];
    varintSupported = true;
  }
  
  public boolean isVarintSupported() {
    return varintSupported;
  }

  public void setVarintSupported(boolean varintSupported) {
    this.varintSupported = varintSupported;
  }
  
  public byte readByte() throws Exception {
    return (byte) is.read();
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
    for (i = 0; ((b = is.read()) & 0x80) != 0; i += 7) {
      value |= (b & 0x7f) << i;
    }
    return value | b << i;
  }

  public long readUnsignedVarLong() throws Exception {
    long value = 0L;
    int i;
    long b;
    for (i = 0; ((b = is.read()) & 128L) != 0L; i += 7) {
      value |= (b & 127L) << i;
    }
    return value | b << i;
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
      bigIntegerBytes[1] = (byte) is.read();
      bigIntegerBytes[2] = (byte) is.read();
      bigIntegerBytes[3] = (byte) is.read();
      bigIntegerBytes[4] = (byte) is.read();
      bigIntegerBytes[5] = (byte) is.read();
      bigIntegerBytes[6] = (byte) is.read();
      bigIntegerBytes[7] = (byte) is.read();
      bigIntegerBytes[8] = (byte) is.read();
      return new BigInteger(bigIntegerBytes);
    }
  }

  public String readNullableString() throws Exception {
    if (isNull()) return null;
    else return readString();
  }
  
  public String readString() throws Exception {
    int length = readUnsignedInt();
    return readString(length);
  }
  
  public String readString(int length) throws Exception {
    if (length == 0) {
      return EMPTY_STRING;
    } else if (length > 0) {
      byte[] tab = readFully(length);
      return new String(tab, 0, length, Binary.utf8);
    } else {
      throw new Exception("bad string length: " + length);
    }
  }
  
  private byte[] readFully(int length) throws Exception {
    int count = 0;
    if (length > buf.length) buf = new byte[length];
    
    int nb = -1;
    do {
      nb = is.read(buf, count, length-count);
      if (nb < 0) throw new Exception("Unexpected end of stream");
      count += nb;
    } while (count != length);
    return buf;
  }

  public byte[] readNullableByteArray() throws Exception {
    if (isNull()) return null;
    else return readByteArray();
  }
  
  public byte[] readByteArray() throws Exception {
    int length = readUnsignedInt();
    return readByteArray(length);
  }
  
  public byte[] readByteArray(int length) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "read byte array length=" + length);
    if (length == 0) {
      return EMPTY_BYTE_ARRAY;
    } else {
      byte[] tab = new byte[length];
      readFully(tab);
      return tab;
    }
  }
    
  public void readFully(byte[] buf) throws Exception {
    int count = 0;
    
    int nb = -1;
    do {
      nb = is.read(buf, count, buf.length-count);
      if (nb < 0) throw new Exception("Unexpected end of stream");
      count += nb;
    } while (count != buf.length);
  }

  public short read16() throws Exception {
    return (short) (((is.read() &0xFF) << 8) | (is.read() &0xFF));
  }
  
  public int read24() throws Exception {
    return ((is.read() &0xFF) << 16) |
            ((is.read() &0xFF) << 8) | (is.read() &0xFF);
  }
  
  public int read32() throws Exception {
    return ((is.read() &0xFF) << 24) | ((is.read() &0xFF) << 16) |
            ((is.read() &0xFF) << 8) | (is.read() &0xFF);
  }
  
  public long read64() throws Exception {
    return ((((long) is.read()) &0xFFL) << 56) | ((((long) is.read()) &0xFFL) << 48) |
      ((((long) is.read()) &0xFFL) << 40) | ((((long) is.read()) &0xFFL) << 32) |
      ((((long) is.read()) &0xFFL) << 24) | ((((long) is.read()) &0xFFL) << 16) |
      ((((long) is.read()) &0xFFL) << 8) | (((long) is.read()) &0xFFL);
  }
  
  public boolean isNull() throws Exception {
	// isNull => isPresent = false
    return ! readBoolean();
  }
  
  public boolean readBoolean() throws Exception {
    byte b = (byte) is.read();
    return (b == Binary.TRUE);
  }
  
  public void close() throws IOException {
    is.close();
  }
}
