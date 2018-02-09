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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.Encoder;
import fr.cnes.encoding.base.VarintHelper;

public class OutputStreamEncoder implements Encoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(OutputStreamEncoder.class.getName());
  
  // Note: It is no longer possible to write encoded value directly on the output stream.
  // The SplitBinary encoding needs to buffer all encoded datas.
  
  private static int BF_BLOCK_SIZE = 32;
  private byte bf[] = new byte[BF_BLOCK_SIZE];
  // Index of next bit to use in bitfield.
  private int bitidx = 0;
  // Number of significant byte used in bitfield
  private int bflen = 0;
  
  private ByteArrayOutputStream baos = null;
  
  private final boolean varintSupported = true;
  
  public OutputStreamEncoder() {
    baos = new ByteArrayOutputStream();
  }

  public boolean isVarintSupported() {
	  return true;
  }

  public void setVarintSupported(boolean varintSupported) {
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
		int idx = bitidx / 8;
		if ((idx +1) > bf.length) {
		  bf = java.util.Arrays.copyOf(bf, (((idx +1) / BF_BLOCK_SIZE) + 1) * BF_BLOCK_SIZE);
		}
		bflen = idx +1;
		bf[idx] |= (1 << (bitidx % 8));
	  }
	  bitidx += 1;
  }
  
  public void writeByte(byte b) throws IOException {
    baos.write(b);
  }
  
  public void writeVarInt(int value) throws IOException {
    while (true) {
      if ((value & ~0x7F) == 0) {
        baos.write(value);
        return;
      } else {
        baos.write((value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }
  
  public void writeVarLong(long value) throws IOException {
    while (true) {
      if ((value & ~0x7FL) == 0) {
        baos.write((int) value);
        return;
      } else {
        baos.write(((int) value & 0x7F) | 0x80);
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
    baos.write((byte) (s >>>  8));
    baos.write((byte) (s >>>  0));
  }
  
  public void write24(int i) throws IOException {
    baos.write((byte) (i >>>  16));
    baos.write((byte) (i >>>  8));
    baos.write((byte) (i >>>  0));
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
    baos.write((byte) (i >>>  24));
    baos.write((byte) (i >>>  16));
    baos.write((byte) (i >>>  8));
    baos.write((byte) (i >>>  0));
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
    baos.write((byte) (l >>>  56));
    baos.write((byte) (l >>>  48));
    baos.write((byte) (l >>>  40));
    baos.write((byte) (l >>>  32));
    baos.write((byte) (l >>>  24));
    baos.write((byte) (l >>>  16));
    baos.write((byte) (l >>>  8));
    baos.write((byte) (l >>>  0));
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
      byte[] buf = str.getBytes(SplitBinary.utf8);
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
      baos.write(tab, offset, length);
    }
  }

  // TODO (AF): Not needed, suppress in interface
  public void flush() throws IOException {
  }

  // TODO (AF): Not needed, suppress in interface
  public void close() throws IOException {
  }

  public void writeTo(OutputStream os) throws IOException {
	  VarintHelper.writeRawVarint32(bflen, os);
	  os.write(bf, 0, bflen);
	  baos.writeTo(os);
  }
}