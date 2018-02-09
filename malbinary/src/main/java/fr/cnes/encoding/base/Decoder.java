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
import java.math.BigInteger;

public interface Decoder {
  
  public boolean isVarintSupported();

  public void setVarintSupported(boolean varintSupported);
  
  public byte readByte() throws Exception;
  
  public boolean isNull() throws Exception;
  
  public boolean readBoolean() throws Exception;
  
  public short readSignedShort() throws Exception;
  
  public short readUnsignedShort() throws Exception;
  
  public int readSignedInt() throws Exception;
  
  public int readUnsignedInt() throws Exception;
  
  public BigInteger readUnsignedLong() throws Exception;
  
  public long readSignedLong() throws Exception;
  
  public short read16() throws Exception;
  
  public int read24() throws Exception;
  
  public int read32() throws Exception;
  
  public long read64() throws Exception;
  
  public String readNullableString() throws Exception;
  
  public String readString() throws Exception;
  
  public String readString(int length) throws Exception;
  
  public byte[] readNullableByteArray() throws Exception;
  
  public byte[] readByteArray() throws Exception;
  
  public byte[] readByteArray(int length) throws Exception;
  
  public void close() throws IOException;

}
