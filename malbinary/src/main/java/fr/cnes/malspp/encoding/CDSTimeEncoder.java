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
import org.orekit.time.DateTimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

import fr.cnes.encoding.base.Encoder;
import fr.cnes.encoding.base.TimeEncoder;
import fr.cnes.encoding.binary.OutputStreamEncoder;

public class CDSTimeEncoder implements TimeEncoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CDSTimeEncoder.class.getName());
  
  public static final long[] SEGMENT_COUNT = new long[] {0xff, 0xffff, 0xffffff, 0xffffffffL};
  
  public static final int MILLISECOND_FIELD_LENGTH = 4;
  
  private static TimeScale timeScale;

  public static TimeScale getTimeScale() throws Exception {
    if (timeScale == null) {
      timeScale = TimeScalesFactory.getUTC();
    }
    return timeScale;
  }
  
  private static void encodeSegmentCount(Encoder encoder, double value,
      int segmentLength) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "segmentLength=" + segmentLength);
    long segmentCount = SEGMENT_COUNT[segmentLength - 1];
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "segmentCount=" + segmentCount);
    if (value > segmentCount)
      throw new Exception("Not enough segments for encoding value: " + value
          + '>' + segmentCount);
    double fraction = value / segmentCount;

    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "fraction=" + fraction);
    
    for (int i = 0; i < segmentLength; i++) {
      fraction = fraction * 256;
      byte b = (byte) fraction;
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "encode byte=" + (b & 0xFF));
      encoder.writeByte(b);
      fraction -= b;
    }
  }
  
  
  // Milliseconds expected
  public static void encodeCDS(double timeToEncode, Encoder encoder,
      CDSTimeCode timeCode, AbsoluteDate javaEpoch) throws Exception {
    AbsoluteDate absoluteDate;
    if (javaEpoch != null) {
      // Orekit takes the elapseDuration in seconds
      double elapsedDuration = ((double) timeToEncode) / 1000d;
      absoluteDate = new AbsoluteDate(javaEpoch, elapsedDuration);
    } else {
      throw new Exception("Missing Java epoch");
    }

    TimeScale timeScale = getTimeScale();
    
    DateTimeComponents dtc = absoluteDate.getComponents(timeScale);
    
    int timeDayCount = dtc.getDate().getMJD();
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "Epoch: " + timeCode.getEpoch());
    
    int epochDayCount = timeCode.getEpoch().getComponents(timeScale).getDate().getMJD();
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "timeDayCount=" + timeDayCount);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "epochDayCount=" + epochDayCount);
    
    int dayCount = timeDayCount - epochDayCount;
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "dayCount=" + dayCount);
    
    encodeSegmentCount(encoder, dayCount, timeCode.getDaySegmentLength());
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "secondsInDay=" + dtc.getTime().getSecondsInDay());
    
    // getSecondsInDay precision is not reliable
    long millisecondCount = ((long) (dtc.getTime().getSecondsInDay())) * 1000;
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "millisecondsInDay=" + millisecondCount);
    
    millisecondCount += ((long) timeToEncode) % 1000;
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "milliseconds=" + millisecondCount);
    
    encodeSegmentCount(encoder, millisecondCount, MILLISECOND_FIELD_LENGTH);
    
    double submilliseconds = timeToEncode - ((long) timeToEncode);
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "submilliseconds=" + submilliseconds);
    
    int submillisecondsLength = timeCode.getSubMillisecondLength();
    for (int i = 0; i < submillisecondsLength; i++) {
      submilliseconds = submilliseconds * 256;
      byte b = (byte) submilliseconds;
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "byte=" + (b & 0xFF));
      encoder.writeByte(b);
      submilliseconds -= b;
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
  
  private CDSTimeCode timeCode;
  
  public CDSTimeEncoder() {
    javaEpoch = getJavaEpoch();
  }

  public CDSTimeEncoder(CDSTimeCode timeCode) {
    this();
    this.timeCode = timeCode;
  }

  public CDSTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CDSTimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public void encode(long timeToEncode, Encoder encoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CDSTimeEncoder.encode(" + timeToEncode
          + ')');
    // Orekit takes the elapseDuration in seconds
    //double elapsedDuration = ((double) timeToEncode) / 1000d;
    encodeCDS(timeToEncode, encoder, timeCode, javaEpoch);
  }

  @Override
  public String toString() {
    return "CDSTimeEncoder [javaEpoch=" + javaEpoch + ", timeCode=" + timeCode
        + "]";
  }
  
}
