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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class Encoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(Encoder.class.getName());
  
  public final static Charset utf8 = Charset.forName("UTF-8");
  
  public static final byte NULL = 0x00;
  public static final byte NOT_NULL = 0x01;
  
  public static final byte FALSE = 0x00;
  public static final byte TRUE = 0x01;
  
  private OutputStream os;
  
  private boolean varintSupported;
  
  public Encoder(OutputStream os) {
    this.os = os;
    varintSupported = true;
  }
  
  public boolean isVarintSupported() {
    return varintSupported;
  }

  public void setVarintSupported(boolean varintSupported) {
    this.varintSupported = varintSupported;
  }

  public void flush() throws IOException {
    os.flush();
  }
  
  public void writeNull() throws IOException {
    os.write(NULL);
  }
  
  public void writeNotNull() throws IOException {
    os.write(NOT_NULL);
  }
  
  public void writeBoolean(boolean bool) throws IOException {
    if (bool) {
      os.write(TRUE);
    } else {
      os.write(FALSE);
    }
  }
  
  public void writeByte(byte b) throws IOException {
    os.write(b);
  }
  
  public void writeVarInt(int value) throws IOException {
    while (true) {
      if ((value & ~0x7F) == 0) {
        os.write(value);
        return;
      } else {
        os.write((value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }
  
  public void writeVarLong(long value) throws IOException {
    while (true) {
      if ((value & ~0x7FL) == 0) {
        os.write((int) value);
        return;
      } else {
        os.write(((int) value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }
  
  public void writeSignedShort(short s) throws IOException {
    if (varintSupported) {
      writeVarInt((s << 1 ^ s >> 15) & 0xFFFF);
    } else {
      write16(s);
    }
  }
  
  public void writeUnsignedShort(short s) throws IOException {
    if (varintSupported) {
      writeVarInt(s & 0xFFFF);
    } else {
      write16(s);
    }
  }
  
  public void write16(short s) throws IOException {
    os.write((byte) (s >>>  8));
    os.write((byte) (s >>>  0));
  }
  
  public void write24(int i) throws IOException {
    os.write((byte) (i >>>  16));
    os.write((byte) (i >>>  8));
    os.write((byte) (i >>>  0));
  }
  
  public void writeSignedInt(int i) throws IOException {
    if (varintSupported) {
      writeVarInt(i << 1 ^ i >> 31);
    } else {
      write32(i);
    }
  }
  
  public void writeUnsignedInt(int i) throws IOException {
    if (varintSupported) {
      writeVarInt(i);
    } else {
      write32(i);
    }
  }
  
  public void write32(int i) throws IOException {
    os.write((byte) (i >>>  24));
    os.write((byte) (i >>>  16));
    os.write((byte) (i >>>  8));
    os.write((byte) (i >>>  0));
  }
  
  public void writeSignedLong(long l) throws IOException {
    if (varintSupported) {
      writeVarLong(l << 1 ^ l >> 63);
    } else {
      write64(l);
    }
  }
  
  public void writeUnsignedLong(BigInteger bigInt) throws IOException {
    if (varintSupported) {
      writeVarLong(bigInt.longValue());
    } else {
      write64(bigInt.longValue());
    }
  }
  
  public void write64(long l) throws IOException {
    os.write((byte) (l >>>  56));
    os.write((byte) (l >>>  48));
    os.write((byte) (l >>>  40));
    os.write((byte) (l >>>  32));
    os.write((byte) (l >>>  24));
    os.write((byte) (l >>>  16));
    os.write((byte) (l >>>  8));
    os.write((byte) (l >>>  0));
  }
  
  public void writeNullableString(String str) throws Exception {
    if (str == null) {
      writeNull();
    } else {
      writeNotNull();
      writeString(str);
    }
  }
  
  public void writeString(String str) throws Exception {
    if (str.length() == 0) {
      writeUnsignedInt(0);
    } else {
      byte[] buf = str.getBytes(utf8);
      writeByteArray(buf);
    }
  }
  
  public void writeNullableByteArray(byte[] tab) throws Exception {
    if (tab == null) {
      writeNull();
    } else {
      writeNotNull();
      writeByteArray(tab);
    }
  }
  
  public void writeByteArray(byte[] tab) throws Exception {
    writeByteArray(tab, 0, tab.length);
  }
  
  public void writeNullableByteArray(byte[] tab, int offset, int length) throws Exception {
    if (tab == null) {
      writeNull();
    } else {
      writeNotNull();
      writeByteArray(tab, offset, length);
    }
  }
    
  public void writeByteArray(byte[] tab, int offset, int length) throws Exception {
    writeUnsignedInt(length);
    if (length > 0) {
      try {
        os.write(tab, offset, length);
      } catch (IOException exc) {
        throw new Exception(exc.toString());
      }
    }
  }
  
  public void close() throws IOException {
    os.close();
  }
}
