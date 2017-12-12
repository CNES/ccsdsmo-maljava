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
import org.orekit.time.DateComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScalesFactory;

import fr.cnes.maljoram.encoding.Decoder;
import fr.cnes.maljoram.encoding.TimeDecoder;

public class CDSTimeDecoder implements TimeDecoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(CDSTimeDecoder.class.getName());
  
  public static double decodeCDS(Decoder decoder, CDSTimeCode timeCode,
      AbsoluteDate javaEpoch, DateComponents epochDate) throws Exception {
    int daySegmentedLength = timeCode.getDaySegmentLength();
    int submillisecondsLength = timeCode.getSubMillisecondLength();
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "daySegmentedLength=" + daySegmentedLength);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "submillisecondsLength=" + submillisecondsLength);
    
    byte[] timeField = decoder.readByteArray(daySegmentedLength
        + CDSTimeEncoder.MILLISECOND_FIELD_LENGTH + submillisecondsLength);
    double elapsedDuration = parseSegmentedTimeCode(daySegmentedLength,
        submillisecondsLength, timeField, javaEpoch, epochDate);
    return elapsedDuration;
  }
  
  public static double parseSegmentedTimeCode(int daySegmentLength,
      int submillisecondsLength, byte[] timeField, AbsoluteDate javaEpoch,
      DateComponents epochDate) throws Exception {
    int i = 0;
    int day = 0;
    while (i < daySegmentLength) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "byte=" + (timeField[i] & 0xFF));
      day = day * 256 + toUnsigned(timeField[i++]);
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "day=" + day);
    
    long milliInDay = 0l;
    while (i < daySegmentLength + CDSTimeEncoder.MILLISECOND_FIELD_LENGTH) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "byte=" + (timeField[i] & 0xFF));
      milliInDay = milliInDay * 256 + toUnsigned(timeField[i++]);
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "milliInDay=" + milliInDay);
    
    final int milli   = (int) (milliInDay % 1000l);
    final int seconds = (int) ((milliInDay - milli) / 1000l);
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "milli=" + milli);

    double subMilli = 0;
    double divisor  = 1;
    while (i < timeField.length) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "byte=" + (timeField[i] & 0xFF));
      subMilli = subMilli * 256 + toUnsigned(timeField[i++]);
      divisor *= 1000;
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "subMilli=" + subMilli);

    final DateComponents date = new DateComponents(epochDate, day);
    final TimeComponents time = new TimeComponents(seconds);
    
    AbsoluteDate resultDate = new AbsoluteDate(date, time,
        TimeScalesFactory.getUTC()).shiftedBy(milli * 1.0e-3 + subMilli
        / divisor);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "resultDate=" + resultDate);
    double resultTime = resultDate.offsetFrom(javaEpoch, TimeScalesFactory.getTAI());
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "resultTime=" + resultTime);
    
    return resultTime * 1000;
  }
  
  private AbsoluteDate javaEpoch;
  
  private DateComponents epochDate;
  
  private CDSTimeCode timeCode;
  
  public CDSTimeDecoder() {
    javaEpoch = CUCTimeEncoder.getJavaEpoch();
  }
  
  private DateComponents getEpochdate() throws Exception {
    if (epochDate == null) {
      epochDate = timeCode.getEpoch().getDate()
        .getComponents(CDSTimeEncoder.getTimeScale()).getDate();
    }
    return epochDate;
  }
  
  public CDSTimeDecoder(CDSTimeCode timeCode) throws Exception {
    this();
    setTimeCode(timeCode);
  }
  
  public CDSTimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(CDSTimeCode timeCode) throws Exception {
    this.timeCode = timeCode;
  }

  public long decode(Decoder decoder) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CDSTimeDecoder.decode()");
    double elapsedDuration = decodeCDS(decoder, timeCode, javaEpoch, getEpochdate());
    return Math.round(elapsedDuration);
  }
  
  private static int toUnsigned(final byte b) {
    final int i = (int) b;
    return (i < 0) ? 256 + i : i;
  }

  @Override
  public String toString() {
    return "CDSTimeDecoder [javaEpoch=" + javaEpoch + ", epochDate="
        + epochDate + ", timeCode=" + timeCode + "]";
  }

}
