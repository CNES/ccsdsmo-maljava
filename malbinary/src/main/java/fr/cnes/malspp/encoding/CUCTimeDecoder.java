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

import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;
import org.orekit.time.AbsoluteDate;

import fr.cnes.encoding.base.Decoder;
import fr.cnes.encoding.base.TimeDecoder;

public class CUCTimeDecoder implements TimeDecoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CUCTimeDecoder.class.getName());
  
  public static double decodeCUC(Decoder decoder,
      CUCTimeCode timeCode, AbsoluteDate javaEpoch) throws Exception {
    int preambleField1 = timeCode.getBasicTimeLength();
    int preambleField2 = timeCode.getFractionalTimeLength();
    byte[] timeField = decoder.readByteArray(preambleField1 + preambleField2);
    double elapsedDuration = parseCCSDSUnsegmentedTimeCode(preambleField1,
        preambleField2, timeField);
    if (javaEpoch != null) {
      AbsoluteDate absoluteDate = new AbsoluteDate(timeCode.getEpoch(), elapsedDuration);
      // Orekit returns the duration in seconds
      return absoluteDate.durationFrom(javaEpoch);
    } else {
      return elapsedDuration;
    }
  }
  
  private AbsoluteDate javaEpoch;
  
  private CUCTimeCode timeCode;
  
  public CUCTimeDecoder() {
    javaEpoch = CUCTimeEncoder.getJavaEpoch();
  }
  
  public CUCTimeDecoder(CUCTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }
  
  public CUCTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CUCTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public long decode(Decoder decoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CUCTimeDecoder.decode()");
    double elapsedDuration = decodeCUC(decoder, timeCode, javaEpoch);
    return Math.round(elapsedDuration * 1000);
  }
  
  public static double parseCCSDSUnsegmentedTimeCode(int coarseTimeLength,
      int fineTimeLength, byte[] timeField) throws Exception {
    double seconds = 0;
    for (int i = 0; i < coarseTimeLength; ++i) {
      seconds = seconds * 256 + toUnsigned(timeField[i]);
    }
    double subseconds = 0;
    for (int i = timeField.length - 1; i >= coarseTimeLength; --i) {
      subseconds = (subseconds + toUnsigned(timeField[i])) / 256;
    }
    return seconds + subseconds;
    //return new AbsoluteDate(epoch, seconds).shiftedBy(subseconds);
  }
  
  private static int toUnsigned(final byte b) {
    final int i = (int) b;
    return (i < 0) ? 256 + i : i;
  }

  @Override
  public String toString() {
    return "CUCTimeDecoder [javaEpoch=" + javaEpoch + ", timeCode=" + timeCode
        + "]";
  }

}
