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

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALService;

/**
 * A {@code Union} represents a MAL attribute that
 * is mapped to one of the following Java type:
 * Boolean, Float, Double, Byte, Short, Integer, Long,
 * String.
 */
public class Union implements Attribute {

  // Note (AF): May be we should use TYPE_SHORT_FORM from Attribute.
  private static final byte BOOLEAN_TYPE = 1;
  private static final byte FLOAT_TYPE = 2;
  private static final byte DOUBLE_TYPE = 3;
  private static final byte OCTET_TYPE = 4;
  private static final byte SHORT_TYPE = 5;
  private static final byte INTEGER_TYPE = 6;
  private static final byte LONG_TYPE = 7;
  private static final byte STRING_TYPE = 8;
  
  private static final String EMPTY_STRING = "";
  
  private Object value;

  private byte type;

  /**
   * Constructs a {@code Union} that represents a MAL Boolean.
   * @param value the value of this {@code Union}
   */
  public Union(Boolean value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = BOOLEAN_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Float.
   * @param value the value of this {@code Union}
   */
  public Union(Float value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = FLOAT_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Double.
   * @param value the value of this {@code Union}
   */
  public Union(Double value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = DOUBLE_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Octet.
   * @param value the value of this {@code Union}
   */
  public Union(Byte value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = OCTET_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Short.
   * @param value the value of this {@code Union}
   */
  public Union(Short value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = SHORT_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Integer.
   * @param value the value of this {@code Union}
   */
  public Union(Integer value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = INTEGER_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL Long.
   * @param value the value of this {@code Union}
   */
  public Union(Long value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = LONG_TYPE;
  }

  /**
   * Constructs a {@code Union} that represents a MAL String.
   * @param value the value of this {@code Union}
   */
  public Union(String value) {
    if (value == null) throw new IllegalArgumentException("Null value");
    this.value = value;
    type = STRING_TYPE;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Boolean.
   * @return the {@code Union} value
   */
  public Boolean getBooleanValue() {
    return (Boolean) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Double.
   * @return the {@code Union} value
   */
  public Double getDoubleValue() {
    return (Double) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Float.
   * @return the {@code Union} value
   */
  public Float getFloatValue() {
    return (Float) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Integer.
   * @return the {@code Union} value
   */
  public Integer getIntegerValue() {
    return (Integer) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Long.
   * @return the {@code Union} value
   */
  public Long getLongValue() {
    return (Long) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Octet.
   * @return the {@code Union} value
   */
  public Byte getOctetValue() {
    return (Byte) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL Short.
   * @return the {@code Union} value
   */
  public Short getShortValue() {
    return (Short) value;
  }

  /**
   * Returns the value of a {@code Union} that represents a MAL String.
   * @return the {@code Union} value
   */
  public String getStringValue() {
    return (String) value;
  }
  
  // Note (AF): The generic getValue method is not part of API.
//  public Object getValue() {
//    return value;
//  }
  
  public int hashCode() {
    if (value == null) return 0;
    else return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof Union) {
      Union u = (Union) obj;
      if (u.type != type) return false;
      return u.value.equals(value);
    } else {
      return false;
    }
  }
  
  public String toString() {
    if (value == null) return "";
    else return value.toString();
  }

  public UShort getAreaNumber() {
    return MALHelper.MAL_AREA_NUMBER;
  }

  public UOctet getAreaVersion() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_VERSION;
  }

  public UShort getServiceNumber() {
    return MALService.NULL_SERVICE_NUMBER;
  }

  public Integer getTypeShortForm() {
    switch (type) {
    case BOOLEAN_TYPE:
      return BOOLEAN_TYPE_SHORT_FORM;
    case FLOAT_TYPE:
      return FLOAT_TYPE_SHORT_FORM;
    case DOUBLE_TYPE:
      return DOUBLE_TYPE_SHORT_FORM;
    case OCTET_TYPE:
      return OCTET_TYPE_SHORT_FORM;
    case SHORT_TYPE:
      return SHORT_TYPE_SHORT_FORM;
    case INTEGER_TYPE:
      return INTEGER_TYPE_SHORT_FORM;
    case LONG_TYPE:
      return LONG_TYPE_SHORT_FORM;
    case STRING_TYPE:
      return STRING_TYPE_SHORT_FORM;
    default:
      return null;
    }
  }
  
  public Long getShortForm() {
    switch (type) {
    case BOOLEAN_TYPE:
      return BOOLEAN_SHORT_FORM;
    case FLOAT_TYPE:
      return FLOAT_SHORT_FORM;
    case DOUBLE_TYPE:
      return DOUBLE_SHORT_FORM;
    case OCTET_TYPE:
      return OCTET_SHORT_FORM;
    case SHORT_TYPE:
      return SHORT_SHORT_FORM;
    case INTEGER_TYPE:
      return INTEGER_SHORT_FORM;
    case LONG_TYPE:
      return LONG_SHORT_FORM;
    case STRING_TYPE:
      return STRING_SHORT_FORM;
    default:
      return null;
    }
  }
  
  public void encode(MALEncoder encoder) throws MALException {
    switch (type) {
    case BOOLEAN_TYPE:
      encoder.encodeBoolean((Boolean) value);
      break;
    case FLOAT_TYPE:
      encoder.encodeFloat((Float) value);
      break;
    case DOUBLE_TYPE:
      encoder.encodeDouble((Double) value);
      break;
    case OCTET_TYPE:
      encoder.encodeOctet((Byte) value);
      break;
    case SHORT_TYPE:
      encoder.encodeShort((Short) value);
      break;
    case INTEGER_TYPE:
      encoder.encodeInteger((Integer) value);
      break;
    case LONG_TYPE:
      encoder.encodeLong((Long) value);
      break;
    case STRING_TYPE:
      encoder.encodeString((String) value);
      break;
    default:
      throw new MALException();
    }
  }

  public Element decode(MALDecoder decoder) throws MALException {
    switch (type) {
    case BOOLEAN_TYPE:
      return new Union(decoder.decodeBoolean());
    case FLOAT_TYPE:
      return new Union(decoder.decodeFloat());
    case DOUBLE_TYPE:
      return new Union(decoder.decodeDouble());
    case OCTET_TYPE:
      return new Union(decoder.decodeOctet());
    case SHORT_TYPE:
      return new Union(decoder.decodeShort());
    case INTEGER_TYPE:
      return new Union(decoder.decodeInteger());
    case LONG_TYPE:
      return new Union(decoder.decodeLong());
    case STRING_TYPE:
      return new Union(decoder.decodeString());
    default:
      throw new MALException();
    }
  }
  
  public Element createElement() {
    switch (type) {
    case BOOLEAN_TYPE:
      return new Union(Boolean.FALSE);
    case FLOAT_TYPE:
      return new Union(Float.MAX_VALUE);
    case DOUBLE_TYPE:
      return new Union(Double.MAX_VALUE);
    case OCTET_TYPE:
      return new Union(Byte.MAX_VALUE);
    case SHORT_TYPE:
      return new Union(Byte.MAX_VALUE);
    case INTEGER_TYPE:
      return new Union(Integer.MAX_VALUE);
    case LONG_TYPE:
      return new Union(Long.MAX_VALUE);
    case STRING_TYPE:
      return new Union(EMPTY_STRING);
    default:
      return null;
    }
  }
}
