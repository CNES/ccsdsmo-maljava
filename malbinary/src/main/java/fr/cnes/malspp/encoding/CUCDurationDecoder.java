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
package fr.cnes.malspp.encoding;

import fr.cnes.encoding.base.Decoder;
import fr.cnes.encoding.base.DurationDecoder;

public class CUCDurationDecoder implements DurationDecoder {
  
  private CUCTimeCode timeCode;
  
  public CUCDurationDecoder() {}
  
  public CUCDurationDecoder(CUCTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }

  public CUCTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CUCTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public double decode(Decoder decoder) throws Exception {
    return CUCTimeDecoder.decodeCUC(decoder, timeCode, null);
  }

  @Override
  public String toString() {
    return "CUCDurationDecoder [timeCode=" + timeCode + "]";
  }

}
