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

/**
 * An {@code Attribute} represents a MAL attribute.
 */
public interface Attribute extends Element {
  
  /**
   * MAL area number shifted 6 bytes on the left added to
   * MAL area version number shifted 3 bytes on the left.
   */
  public static final long ABSOLUTE_AREA_SERVICE_NUMBER = 0x1000001000000L;

  /**
   * Relative short form of the type Blob.
   */
  public static final int _BLOB_TYPE_SHORT_FORM = 1;
  
  /**
   * Relative short form of the type Boolean.
   */
  public static final int _BOOLEAN_TYPE_SHORT_FORM = 2;
  
  /**
   * Relative short form of the type Duration.
   */
  public static final int _DURATION_TYPE_SHORT_FORM = 3;
  
  /**
   * Relative short form of the type Float.
   */
  public static final int _FLOAT_TYPE_SHORT_FORM = 4;
  
  /**
   * Relative short form of the type Double.
   */
  public static final int _DOUBLE_TYPE_SHORT_FORM = 5;
  
  /**
   * Relative short form of the type Identifier.
   */
  public static final int _IDENTIFIER_TYPE_SHORT_FORM = 6;
  
  /**
   * Relative short form of the type Octet.
   */
  public static final int _OCTET_TYPE_SHORT_FORM = 7;
  
  /**
   * Relative short form of the type UOctet.
   */
  public static final int _UOCTET_TYPE_SHORT_FORM = 8;
  
  /**
   * Relative short form of the type Short.
   */
  public static final int _SHORT_TYPE_SHORT_FORM = 9;
  
  /**
   * Relative short form of the type UShort.
   */
  public static final int _USHORT_TYPE_SHORT_FORM = 10;
  
  /**
   * Relative short form of the type Integer.
   */
  public static final int _INTEGER_TYPE_SHORT_FORM = 11;
  
  /**
   * Relative short form of the type UInteger.
   */
  public static final int _UINTEGER_TYPE_SHORT_FORM = 12;
  
  /**
   * Relative short form of the type Long.
   */
  public static final int _LONG_TYPE_SHORT_FORM = 13;
  
  /**
   * Relative short form of the type ULong.
   */
  public static final int _ULONG_TYPE_SHORT_FORM = 14;
  
  /**
   * Relative short form of the type String.
   */
  public static final int _STRING_TYPE_SHORT_FORM = 15;
  
  /**
   * Relative short form of the type Time.
   */
  public static final int _TIME_TYPE_SHORT_FORM = 16;
  
  /**
   * Relative short form of the type FineTime.
   */
  public static final int _FINETIME_TYPE_SHORT_FORM = 17;
  
  /**
   * Relative short form of the type URI.
   */
  public static final int _URI_TYPE_SHORT_FORM = 18;
  
  

  /**
   * Relative short form of the type Blob.
   */
  public static final Integer BLOB_TYPE_SHORT_FORM = _BLOB_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Boolean.
   */
  public static final Integer BOOLEAN_TYPE_SHORT_FORM = _BOOLEAN_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Duration.
   */
  public static final Integer DURATION_TYPE_SHORT_FORM = _DURATION_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Float.
   */
  public static final Integer FLOAT_TYPE_SHORT_FORM = _FLOAT_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Double.
   */
  public static final Integer DOUBLE_TYPE_SHORT_FORM = _DOUBLE_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Identifier.
   */
  public static final Integer IDENTIFIER_TYPE_SHORT_FORM = _IDENTIFIER_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Octet.
   */
  public static final Integer OCTET_TYPE_SHORT_FORM = _OCTET_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type UOctet.
   */
  public static final Integer UOCTET_TYPE_SHORT_FORM = _UOCTET_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Short.
   */
  public static final Integer SHORT_TYPE_SHORT_FORM = _SHORT_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type UShort.
   */
  public static final Integer USHORT_TYPE_SHORT_FORM = _USHORT_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Integer.
   */
  public static final Integer INTEGER_TYPE_SHORT_FORM = _INTEGER_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type UInteger.
   */
  public static final Integer UINTEGER_TYPE_SHORT_FORM = _UINTEGER_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Long.
   */
  public static final Integer LONG_TYPE_SHORT_FORM = _LONG_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type ULong.
   */
  public static final Integer ULONG_TYPE_SHORT_FORM = _ULONG_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type String.
   */
  public static final Integer STRING_TYPE_SHORT_FORM = _STRING_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type Time.
   */
  public static final Integer TIME_TYPE_SHORT_FORM = _TIME_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type FineTime.
   */
  public static final Integer FINETIME_TYPE_SHORT_FORM = _FINETIME_TYPE_SHORT_FORM;
  
  /**
   * Relative short form of the type URI.
   */
  public static final Integer URI_TYPE_SHORT_FORM = _URI_TYPE_SHORT_FORM;

  
  
  /**
   * Absolute short form of the type Blob.
   */
  public static final Long BLOB_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _BLOB_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Boolean.
   */
  public static final Long BOOLEAN_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _BOOLEAN_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Duration.
   */
  public static final Long DURATION_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _DURATION_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Float.
   */
  public static final Long FLOAT_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _FLOAT_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Double.
   */
  public static final Long DOUBLE_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _DOUBLE_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Identifier.
   */
  public static final Long IDENTIFIER_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _IDENTIFIER_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Octet.
   */
  public static final Long OCTET_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _OCTET_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type UOctet.
   */
  public static final Long UOCTET_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _UOCTET_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Short.
   */
  public static final Long SHORT_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _SHORT_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type UShort.
   */
  public static final Long USHORT_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _USHORT_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Integer.
   */
  public static final Long INTEGER_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _INTEGER_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type UInteger.
   */
  public static final Long UINTEGER_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _UINTEGER_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Long.
   */
  public static final Long LONG_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _LONG_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type ULong.
   */
  public static final Long ULONG_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _ULONG_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type String.
   */
  public static final Long STRING_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _STRING_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type Time.
   */
  public static final Long TIME_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _TIME_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type FineTime.
   */
  public static final Long FINETIME_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _FINETIME_TYPE_SHORT_FORM;
  
  /**
   * Absolute short form of the type URI.
   */
  public static final Long URI_SHORT_FORM = ABSOLUTE_AREA_SERVICE_NUMBER + _URI_TYPE_SHORT_FORM;

}
