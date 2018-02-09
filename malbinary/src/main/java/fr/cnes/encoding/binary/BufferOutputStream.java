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

public class BufferOutputStream extends OutputStream {

  private byte[] buffer;

  private int index;

  public BufferOutputStream(byte[] buffer) throws Exception {
    super();
    this.buffer = buffer;
    index = 0;
  }

  @Override
  public void write(int b) throws IOException {
    buffer[index++] = (byte) b;
  }

  @Override
  public void write(byte[] bytes, int offset, int length) throws IOException {
    System.arraycopy(bytes, offset, buffer, index, length);
    index += length;
  }

  @Override
  public void write(byte[] bytes) throws IOException {
    write(bytes, 0, bytes.length);
  }

  @Override
  public void flush() throws IOException {
    // nothing to do
  }
  
  public int getIndex() {
    return index;
  }
  
}
