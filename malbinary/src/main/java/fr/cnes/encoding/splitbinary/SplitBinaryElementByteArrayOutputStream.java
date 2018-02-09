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

import java.io.IOException;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Element;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.base.DurationEncoder;
import fr.cnes.encoding.base.FineTimeEncoder;
import fr.cnes.encoding.base.OpenByteArrayOutputStream;
import fr.cnes.encoding.base.TimeEncoder;

// TODO (AF): It seems to be identical to BinaryElementByteArrayOutputStream except the superclass.
public class SplitBinaryElementByteArrayOutputStream extends SplitBinaryElementOutputStream {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(SplitBinaryElementByteArrayOutputStream.class.getName());

  private OpenByteArrayOutputStream baos;
  
  public SplitBinaryElementByteArrayOutputStream(OpenByteArrayOutputStream baos, 
      boolean encodedUpdate, boolean byteArrayString, 
      TimeEncoder timeEncoder, FineTimeEncoder fineTimeEncoder,
      DurationEncoder durationEncoder) {
    super(baos, encodedUpdate, byteArrayString, timeEncoder, 
        fineTimeEncoder, durationEncoder);
    this.baos = baos;
  }
  
  protected void encode(List<Element> updateList) throws MALException {
    try {
      encoder.getEncoder().writeUnsignedInt(updateList.size());
      for (Element update : updateList) {
        baos.write(PAD);
        int startIndex = baos.getIndex();
        encoder.encodeNullableElement(update);
        int stopIndex = baos.getIndex();
        int updateLength = stopIndex - startIndex;
        byte[] internalArray = baos.getInternalArray();
        internalArray[startIndex - 4] = (byte) (updateLength >>> 24);
        internalArray[startIndex - 3] = (byte) (updateLength >>> 16);
        internalArray[startIndex - 2] = (byte) (updateLength >>> 8);
        internalArray[startIndex - 1] = (byte) (updateLength >>> 0);
      }
    } catch (IOException e) {
      throw new MALException("", e);
    }
  }
}
