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

public interface Encoder {
	  
	  public boolean isVarintSupported();

	  public void setVarintSupported(boolean varintSupported);
	  	  
	  public void writeByte(byte b) throws IOException;

	  public void writeNull() throws IOException;
	  
	  public void writeNotNull() throws IOException;
	  
	  public void writeBoolean(boolean bool) throws IOException;
	  
	  public void writeSignedShort(short s) throws IOException;
	  
	  public void writeUnsignedShort(short s) throws IOException;
	  
	  public void writeSignedInt(int i) throws IOException;
	  
	  public void writeUnsignedInt(int i) throws IOException;
	  
	  public void writeSignedLong(long l) throws IOException;
	  
	  public void writeUnsignedLong(BigInteger bigInt) throws IOException;
	  
	  public void write16(short s) throws IOException;
	  
	  public void write24(int i) throws IOException;
	  
	  public void write32(int i) throws IOException;
	  
	  public void write64(long l) throws IOException;
	  
	  public void writeNullableString(String str) throws Exception;
	  
	  public void writeString(String str) throws Exception;
	  
	  public void writeNullableByteArray(byte[] tab) throws Exception;
	  
	  public void writeByteArray(byte[] tab) throws Exception;
	  
	  public void writeNullableByteArray(byte[] tab, int offset, int length) throws Exception;
	    
	  public void writeByteArray(byte[] tab, int offset, int length) throws Exception;
	  
	  public void flush() throws IOException;

	  public void close() throws IOException;

}
