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
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.Encoder;

public class OutputStreamEncoder implements Encoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(OutputStreamEncoder.class.getName());
  
  private OutputStream os;
  
  private boolean varintSupported;
  
  public OutputStreamEncoder(OutputStream os) {
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
	// IsPresent false => writeBoolean(false)
    writeBoolean(false);
  }
  
  public void writeNotNull() throws IOException {
	// IsPresent true => writeBoolean(true)
    writeBoolean(true);
  }
  
  public void writeBoolean(boolean bool) throws IOException {
    if (bool) {
      os.write(Binary.TRUE);
    } else {
      os.write(Binary.FALSE);
    }
  }
  
  public void writeByte(byte b) throws IOException {
    os.write(b);
  }
  
  protected void writeVarInt(int value) throws IOException {
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
  
  protected void writeVarLong(long value) throws IOException {
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
      byte[] buf = str.getBytes(Binary.utf8);
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
