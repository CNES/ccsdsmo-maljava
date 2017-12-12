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
package org.ccsds.moims.mo.mal.structures;

/** QoSLevel is an enumeration holding the possible QoS levels. */
public final class QoSLevel extends org.ccsds.moims.mo.mal.structures.Enumeration {

  public static final Integer TYPE_SHORT_FORM = new Integer(21);

  public static final Long SHORT_FORM = new Long(281474993487893L);

  public static final int _BESTEFFORT_INDEX = 0;

  public static final org.ccsds.moims.mo.mal.structures.UInteger BESTEFFORT_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(1);

  /** Used for Best Effort QoS Level. */
  public static final QoSLevel BESTEFFORT = new QoSLevel(_BESTEFFORT_INDEX);

  public static final int _ASSURED_INDEX = 1;

  public static final org.ccsds.moims.mo.mal.structures.UInteger ASSURED_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(2);

  /** Used for Assured QoS Level. */
  public static final QoSLevel ASSURED = new QoSLevel(_ASSURED_INDEX);

  public static final int _QUEUED_INDEX = 2;

  public static final org.ccsds.moims.mo.mal.structures.UInteger QUEUED_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(3);

  /** Used for Queued QoS Level. */
  public static final QoSLevel QUEUED = new QoSLevel(_QUEUED_INDEX);

  public static final int _TIMELY_INDEX = 3;

  public static final org.ccsds.moims.mo.mal.structures.UInteger TIMELY_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(4);

  /** Used for Timely QoS Level. */
  public static final QoSLevel TIMELY = new QoSLevel(_TIMELY_INDEX);

  private static final QoSLevel[] enumerations = {
      BESTEFFORT,
      ASSURED,
      QUEUED,
      TIMELY
  };

  private static final String[] enumerationNames = {
      "BESTEFFORT",
      "ASSURED",
      "QUEUED",
      "TIMELY"
  };

  private static final org.ccsds.moims.mo.mal.structures.UInteger[] enumerationNumericValues = {
      BESTEFFORT_NUM_VALUE,
      ASSURED_NUM_VALUE,
      QUEUED_NUM_VALUE,
      TIMELY_NUM_VALUE
  };

  private QoSLevel(Integer ordinal) {
    super(ordinal);
  }

  public static QoSLevel fromOrdinal(int ordinal) {
    return enumerations[ordinal];
  }

  public static QoSLevel fromNumericValue(org.ccsds.moims.mo.mal.structures.UInteger numericValue) {
    for (int i = 1; i < enumerationNumericValues.length; i++) {
      if (enumerationNumericValues[i].equals(numericValue)) {
        return enumerations[i];
      }
    }
    return null;
  }

  public static QoSLevel fromString(String s) {
    for (int i = 1; i < enumerationNames.length; i++) {
      if (enumerationNames[i].equals(s)) {
        return enumerations[i];
      }
    }
    return null;
  }

  public String toString() {
    return enumerationNames[getOrdinal()];
  }

  public org.ccsds.moims.mo.mal.structures.UInteger getNumericValue() {
    return enumerationNumericValues[getOrdinal()];
  }

  public org.ccsds.moims.mo.mal.structures.UShort getAreaNumber() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_NUMBER;
  }

  public org.ccsds.moims.mo.mal.structures.UOctet getAreaVersion() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_VERSION;
  }

  public org.ccsds.moims.mo.mal.structures.UShort getServiceNumber() {
    return org.ccsds.moims.mo.mal.MALService.NULL_SERVICE_NUMBER;
  }

  public Long getShortForm() {
    return SHORT_FORM;
  }

  public Integer getTypeShortForm() {
    return TYPE_SHORT_FORM;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeUOctet(new org.ccsds.moims.mo.mal.structures.UOctet((short) getOrdinal()));
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    int ordinal;
    ordinal = decoder.decodeUOctet().getValue();
    return fromOrdinal(ordinal);
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return BESTEFFORT;
  }

}