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

import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

public abstract class TimeCode {
  
  // CCSDS recommended TAI epoch of 1958 January 1
  public static final AbsoluteDate TAI_EPOCH = new AbsoluteDate(1958, 1, 1,
      TimeScalesFactory.getTAI());

  public static final int UNIT_SECOND = 0;
  public static final int UNIT_MILLISECOND = 1;
  public static final int UNIT_MICROSECOND = 2;
  public static final int UNIT_NANOSECOND = 3;
  public static final int UNIT_PICOSECOND = 4;
  
  public static final String UNIT_NAME_SECOND = "second";
  public static final String UNIT_NAME_MILLISECOND = "millisecond";
  public static final String UNIT_NAME_MICROSECOND = "microsecond";
  public static final String UNIT_NAME_NANOSECOND = "nanosecond";
  public static final String UNIT_NAME_PICOSECOND = "picosecond";
  
  public static int parseUnit(String s) throws Exception {
    if (UNIT_NAME_SECOND.equals(s)) return UNIT_SECOND;
    if (UNIT_NAME_MILLISECOND.equals(s)) return UNIT_MILLISECOND;
    if (UNIT_NAME_MICROSECOND.equals(s)) return UNIT_MICROSECOND;
    if (UNIT_NAME_NANOSECOND.equals(s)) return UNIT_NANOSECOND;
    if (UNIT_NAME_PICOSECOND.equals(s)) return UNIT_PICOSECOND;
    throw new Exception("Unknown time unit: " + s);
  }
  
  private AbsoluteDate epoch;
  
  private int unit;
  
  private boolean ccsdsEpoch;
  
  public TimeCode() {
    epoch = TAI_EPOCH;
    unit = UNIT_SECOND;
  }

  public AbsoluteDate getEpoch() {
    return epoch;
  }

  public boolean isCcsdsEpoch() {
    return ccsdsEpoch;
  }

  public void setCcsdsEpoch(boolean ccsdsEpoch) {
    this.ccsdsEpoch = ccsdsEpoch;
  }

  public void setEpoch(AbsoluteDate epoch) {
    this.epoch = epoch;
  }

  public int getUnit() {
    return unit;
  }

  public void setUnit(int unit) {
    this.unit = unit;
  }

  @Override
  public String toString() {
    return "TimeCode [epoch=" + epoch + ", unit=" + unit + "]";
  }

}
