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

/** SessionType is an enumeration holding the session types. */
public final class SessionType extends org.ccsds.moims.mo.mal.structures.Enumeration {

  public static final Integer TYPE_SHORT_FORM = new Integer(20);

  public static final Long SHORT_FORM = new Long(281474993487892L);

  public static final int _LIVE_INDEX = 0;

  public static final org.ccsds.moims.mo.mal.structures.UInteger LIVE_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(1);

  /** Used for Live sessions. */
  public static final SessionType LIVE = new SessionType(_LIVE_INDEX);

  public static final int _SIMULATION_INDEX = 1;

  public static final org.ccsds.moims.mo.mal.structures.UInteger SIMULATION_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(2);

  /** Used for Simulation sessions. */
  public static final SessionType SIMULATION = new SessionType(_SIMULATION_INDEX);

  public static final int _REPLAY_INDEX = 2;

  public static final org.ccsds.moims.mo.mal.structures.UInteger REPLAY_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(3);

  /** Used for Replay sessions. */
  public static final SessionType REPLAY = new SessionType(_REPLAY_INDEX);

  private static final SessionType[] enumerations = {
      LIVE,
      SIMULATION,
      REPLAY
  };

  private static final String[] enumerationNames = {
      "LIVE",
      "SIMULATION",
      "REPLAY"
  };

  private static final org.ccsds.moims.mo.mal.structures.UInteger[] enumerationNumericValues = {
      LIVE_NUM_VALUE,
      SIMULATION_NUM_VALUE,
      REPLAY_NUM_VALUE
  };

  private SessionType(Integer ordinal) {
    super(ordinal);
  }

  public static SessionType fromOrdinal(int ordinal) {
    return enumerations[ordinal];
  }

  public static SessionType fromNumericValue(org.ccsds.moims.mo.mal.structures.UInteger numericValue) {
    for (int i = 1; i < enumerationNumericValues.length; i++) {
      if (enumerationNumericValues[i].equals(numericValue)) {
        return enumerations[i];
      }
    }
    return null;
  }

  public static SessionType fromString(String s) {
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
    return LIVE;
  }

}