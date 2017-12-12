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

/** UpdateType is an enumeration holding the possible Update types. */
public final class UpdateType extends org.ccsds.moims.mo.mal.structures.Enumeration {

  public static final Integer TYPE_SHORT_FORM = new Integer(22);

  public static final Long SHORT_FORM = new Long(281474993487894L);

  public static final int _CREATION_INDEX = 0;

  public static final org.ccsds.moims.mo.mal.structures.UInteger CREATION_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(1);

  /** Update is notification of the creation of the item. */
  public static final UpdateType CREATION = new UpdateType(_CREATION_INDEX);

  public static final int _UPDATE_INDEX = 1;

  public static final org.ccsds.moims.mo.mal.structures.UInteger UPDATE_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(2);

  /** Update is just a periodic update of the item and has not changed its value. */
  public static final UpdateType UPDATE = new UpdateType(_UPDATE_INDEX);

  public static final int _MODIFICATION_INDEX = 2;

  public static final org.ccsds.moims.mo.mal.structures.UInteger MODIFICATION_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(3);

  /** Update is for a changed value or modification of the item. */
  public static final UpdateType MODIFICATION = new UpdateType(_MODIFICATION_INDEX);

  public static final int _DELETION_INDEX = 3;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DELETION_NUM_VALUE = new org.ccsds.moims.mo.mal.structures.UInteger(4);

  /** Update is notification of the removal of the item. */
  public static final UpdateType DELETION = new UpdateType(_DELETION_INDEX);

  private static final UpdateType[] enumerations = {
      CREATION,
      UPDATE,
      MODIFICATION,
      DELETION
  };

  private static final String[] enumerationNames = {
      "CREATION",
      "UPDATE",
      "MODIFICATION",
      "DELETION"
  };

  private static final org.ccsds.moims.mo.mal.structures.UInteger[] enumerationNumericValues = {
      CREATION_NUM_VALUE,
      UPDATE_NUM_VALUE,
      MODIFICATION_NUM_VALUE,
      DELETION_NUM_VALUE
  };

  private UpdateType(Integer ordinal) {
    super(ordinal);
  }

  public static UpdateType fromOrdinal(int ordinal) {
    return enumerations[ordinal];
  }

  public static UpdateType fromNumericValue(org.ccsds.moims.mo.mal.structures.UInteger numericValue) {
    for (int i = 1; i < enumerationNumericValues.length; i++) {
      if (enumerationNumericValues[i].equals(numericValue)) {
        return enumerations[i];
      }
    }
    return null;
  }

  public static UpdateType fromString(String s) {
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
    return CREATION;
  }

}