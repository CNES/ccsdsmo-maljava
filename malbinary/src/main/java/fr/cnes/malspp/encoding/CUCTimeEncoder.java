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

import fr.cnes.encoding.binary.Encoder;
import fr.cnes.encoding.binary.TimeEncoder;

public class CUCTimeEncoder implements TimeEncoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CUCTimeEncoder.class.getName());
  
  public static void encodeCUC(double elapsedDuration, Encoder encoder,
      CUCTimeCode timeCode, AbsoluteDate javaEpoch) throws Exception {
    double offset;
    if (javaEpoch != null) {
      AbsoluteDate absoluteDate = new AbsoluteDate(javaEpoch, elapsedDuration);
      offset = absoluteDate.durationFrom(timeCode.getEpoch());
    } else {
      offset = elapsedDuration;
    }
    
    long seconds = (long) offset;
    for (int i = timeCode.getBasicTimeLength(); i > 0; i--) {
      int shift = (i - 1) * 8;
      encoder.writeByte((byte) (seconds >>> shift));
    }

    double subseconds = offset - seconds;
    for (int i = 0; i < timeCode.getFractionalTimeLength(); i++) {
      subseconds = subseconds * 256;
      byte b = (byte) subseconds;
      encoder.writeByte(b);
      subseconds -= b;
    }
  }
  
  public static AbsoluteDate getJavaEpoch() {
    //String epochAsString = System.getProperty(TimeEncoder.TIME_EPOCH,
    //    "1970-01-01T00:00:00.000");
    // MAL Java API time scale is TAI
    //AbsoluteDate epoch = new AbsoluteDate(epochAsString,
    //    TimeScalesFactory.getTAI());
    return AbsoluteDate.JAVA_EPOCH;
  }
  
  private AbsoluteDate javaEpoch;
  
  private CUCTimeCode timeCode;
  
  public CUCTimeEncoder() {
    javaEpoch = getJavaEpoch();
  }

  public CUCTimeEncoder(CUCTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }

  public CUCTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CUCTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public void encode(long timeToEncode, Encoder encoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CUCTimeEncoder.encode(" + timeToEncode
          + ')');
    // Orekit takes the elapseDuration in seconds
    double elapsedDuration = ((double) timeToEncode) / 1000d;
    encodeCUC(elapsedDuration, encoder, timeCode, javaEpoch);
  }

  @Override
  public String toString() {
    return "CUCTimeEncoder [javaEpoch=" + javaEpoch + ", timeCode=" + timeCode
        + "]";
  }
  
}
