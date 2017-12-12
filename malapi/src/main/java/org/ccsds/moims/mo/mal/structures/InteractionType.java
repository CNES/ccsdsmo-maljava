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

/** InteractionType is an enumeration holding the possible interaction pattern types. */
public final class InteractionType extends org.ccsds.moims.mo.mal.structures.Enumeration {

  public static final Integer TYPE_SHORT_FORM = new Integer(19);

  public static final Long SHORT_FORM = new Long(281474993487891L);

  public static final int _SEND_INDEX = 0;

  public static final org.ccsds.moims.mo.mal.structures.UInteger SEND_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(1);

  /** Used for Send interactions. */
  public static final InteractionType SEND = new InteractionType(_SEND_INDEX);

  public static final int _SUBMIT_INDEX = 1;

  public static final org.ccsds.moims.mo.mal.structures.UInteger SUBMIT_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(2);

  /** Used for Submit interactions. */
  public static final InteractionType SUBMIT = new InteractionType(_SUBMIT_INDEX);

  public static final int _REQUEST_INDEX = 2;

  public static final org.ccsds.moims.mo.mal.structures.UInteger REQUEST_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(3);

  /** Used for Request interactions. */
  public static final InteractionType REQUEST = new InteractionType(_REQUEST_INDEX);

  public static final int _INVOKE_INDEX = 3;

  public static final org.ccsds.moims.mo.mal.structures.UInteger INVOKE_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(4);

  /** Used for Invoke interactions. */
  public static final InteractionType INVOKE = new InteractionType(_INVOKE_INDEX);

  public static final int _PROGRESS_INDEX = 4;

  public static final org.ccsds.moims.mo.mal.structures.UInteger PROGRESS_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(5);

  /** Used for Progress interactions. */
  public static final InteractionType PROGRESS = new InteractionType(_PROGRESS_INDEX);

  public static final int _PUBSUB_INDEX = 5;

  public static final org.ccsds.moims.mo.mal.structures.UInteger PUBSUB_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(6);

  /** Used for Publish/Subscribe interactions. */
  public static final InteractionType PUBSUB = new InteractionType(_PUBSUB_INDEX);

  private static final InteractionType[] enumerations = {
      SEND,
      SUBMIT,
      REQUEST,
      INVOKE,
      PROGRESS,
      PUBSUB
  };

  private static final String[] enumerationNames = {
      "SEND",
      "SUBMIT",
      "REQUEST",
      "INVOKE",
      "PROGRESS",
      "PUBSUB"
  };

  private static final org.ccsds.moims.mo.mal.structures.UInteger[] enumerationNumericValues = {
      SEND_NUM_VALUE,
      SUBMIT_NUM_VALUE,
      REQUEST_NUM_VALUE,
      INVOKE_NUM_VALUE,
      PROGRESS_NUM_VALUE,
      PUBSUB_NUM_VALUE
  };

  private InteractionType(Integer ordinal) {
    super(ordinal);
  }

  public static InteractionType fromOrdinal(int ordinal) {
    return enumerations[ordinal];
  }

  public static InteractionType fromNumericValue(org.ccsds.moims.mo.mal.structures.UInteger numericValue) {
    for (int i = 1; i < enumerationNumericValues.length; i++) {
      if (enumerationNumericValues[i].equals(numericValue)) {
        return enumerations[i];
      }
    }
    return null;
  }

  public static InteractionType fromString(String s) {
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
    return SEND;
  }

}