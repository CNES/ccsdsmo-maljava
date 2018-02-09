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
package fr.cnes.encoding.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VarintHelper {
  
  /**
   * Encode and write a varint.  {@code value} is treated as
   * unsigned, so it won't be sign-extended if negative.
   */
  public static void writeRawVarint32(int value, OutputStream os) throws IOException {
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
  
  /**
   * Read a raw Varint from the stream.  If larger than 32 bits, discard the
   * upper bits.
   */
  public static int readRawVarint32(InputStream is) throws IOException {
    byte tmp = (byte) is.read();
    if (tmp >= 0) {
      return tmp;
    }
    int result = tmp & 0x7f;
    if ((tmp = (byte) is.read()) >= 0) {
      result |= tmp << 7;
    } else {
      result |= (tmp & 0x7f) << 7;
      if ((tmp = (byte) is.read()) >= 0) {
        result |= tmp << 14;
      } else {
        result |= (tmp & 0x7f) << 14;
        if ((tmp = (byte) is.read()) >= 0) {
          result |= tmp << 21;
        } else {
          result |= (tmp & 0x7f) << 21;
          result |= (tmp = (byte) is.read()) << 28;
          if (tmp < 0) {
            // Discard upper 32 bits.
            for (int i = 0; i < 5; i++) {
              if ((byte) is.read() >= 0) {
                return result;
              }
            }
            throw new IOException("malformedVarint");
          }
        }
      }
    }
    return result;
  }
  
  /** Encode and write a varint. */
  public static void writeRawVarint64(long value, OutputStream os) throws IOException {
    while (true) {
      if ((value & ~0x7FL) == 0) {
        os.write((int)value);
        return;
      } else {
        os.write(((int)value & 0x7F) | 0x80);
        value >>>= 7;
      }
    }
  }
  
  /** Read a raw Varint from the stream. */
  public static long readRawVarint64(InputStream is) throws IOException {
    int shift = 0;
    long result = 0;
    while (shift < 64) {
      final byte b = (byte) is.read();
      result |= (long)(b & 0x7F) << shift;
      if ((b & 0x80) == 0) {
        return result;
      }
      shift += 7;
    }
    throw new IOException("malformedVarint");
  }
}
