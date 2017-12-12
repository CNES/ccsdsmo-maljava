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
package org.ccsds.moims.mo.mal;

import java.util.List;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALEncoder} encodes MAL elements. 
 */
public interface MALEncoder {
  
  /**
   * Encodes a blob that cannot be {@code null}.
   * @param value the blob to encode
   * @throws MALException if an error occurs 
   */
  public void encodeBlob(Blob value) throws MALException;
  
  /**
   * Encodes a blob that can be {@code null}.
   * @param value the blob to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableBlob(Blob value) throws MALException;
  
  /**
   * Encodes a boolean that cannot be {@code null}.
   * @param value the boolean to encode
   * @throws MALException if an error occurs 
   */
  public void encodeBoolean(Boolean value) throws MALException;
  
  /**
   * Encodes a boolean that can be {@code null}.
   * @param value the boolean to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableBoolean(Boolean value) throws MALException;
  
  /**
   * Encodes a duration that cannot be {@code null}.
   * @param value the duration to encode
   * @throws MALException if an error occurs 
   */
  public void encodeDuration(Duration value) throws MALException;
  
  /**
   * Encodes a duration that can be {@code null}.
   * @param value the duration to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableDuration(Duration value) throws MALException;

  /**
   * Encodes a float that cannot be {@code null}.
   * @param value the float to encode
   * @throws MALException if an error occurs 
   */
  public void encodeFloat(Float value) throws MALException;
  
  /**
   * Encodes a float that can be {@code null}.
   * @param value the float to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableFloat(Float value) throws MALException;

  /**
   * Encodes a double that cannot be {@code null}.
   * @param value the double to encode
   * @throws MALException if an error occurs 
   */
  public void encodeDouble(Double value) throws MALException;
  
  /**
   * Encodes a double that can be {@code null}.
   * @param value the double to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableDouble(Double value) throws MALException;

  /**
   * Encodes an identifier that cannot be {@code null}.
   * @param value the identifier to encode
   * @throws MALException if an error occurs 
   */
  public void encodeIdentifier(Identifier value) throws MALException;
  
  /**
   * Encodes an identifier that can be {@code null}.
   * @param value the identifier to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableIdentifier(Identifier value) throws MALException;

  /**
   * Encodes a URI that cannot be {@code null}.
   * @param value the URI to encode
   * @throws MALException if an error occurs 
   */
  public void encodeURI(URI value) throws MALException;
  
  /**
   * Encodes a URI that can be {@code null}.
   * @param value the URI to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableURI(URI value) throws MALException;
  
  /**
   * Encodes a signed octet that cannot be {@code null}.
   * @param value the signed octet to encode
   * @throws MALException if an error occurs 
   */
  public void encodeOctet(Byte value) throws MALException;
  
  /**
   * Encodes a signed octet that can be {@code null}.
   * @param value the signed octet to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableOctet(Byte value) throws MALException;
  
  /**
   * Encodes an unsigned octet that cannot be {@code null}.
   * @param value the unsigned octet to encode
   * @throws MALException if an error occurs 
   */
  public void encodeUOctet(UOctet value) throws MALException;
  
  /**
   * Encodes an unsigned octet that can be {@code null}.
   * @param value the unsigned octet to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableUOctet(UOctet value) throws MALException;

  /**
   * Encodes a signed short that cannot be {@code null}.
   * @param value the signed short to encode
   * @throws MALException if an error occurs 
   */
  public void encodeShort(Short value) throws MALException;
  
  /**
   * Encodes a signed short that can be {@code null}.
   * @param value the signed short to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableShort(Short value) throws MALException;
  
  /**
   * Encodes an unsigned short that cannot be {@code null}.
   * @param value the unsigned short to encode
   * @throws MALException if an error occurs 
   */
  public void encodeUShort(UShort value) throws MALException;
  
  /**
   * Encodes an unsigned short that can be {@code null}.
   * @param value the unsigned short to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableUShort(UShort value) throws MALException;

  /**
   * Encodes a signed integer that cannot be {@code null}.
   * @param value the signed integer to encode
   * @throws MALException if an error occurs 
   */
  public void encodeInteger(Integer value) throws MALException;
  
  /**
   * Encodes a signed integer that can be {@code null}.
   * @param value the signed integer to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableInteger(Integer value) throws MALException;
  
  /**
   * Encodes an unsigned integer that cannot be {@code null}.
   * @param value the unsigned integer to encode
   * @throws MALException if an error occurs 
   */
  public void encodeUInteger(UInteger value) throws MALException;
  
  /**
   * Encodes an unsigned integer that can be {@code null}.
   * @param value the unsigned integer to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableUInteger(UInteger value) throws MALException;

  /**
   * Encodes a signed long that cannot be {@code null}.
   * @param value the signed long to encode
   * @throws MALException if an error occurs 
   */
  public void encodeLong(Long value) throws MALException;
  
  /**
   * Encodes a signed long that can be {@code null}.
   * @param value the signed long to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableLong(Long value) throws MALException;
  
  /**
   * Encodes an unsigned long that cannot be {@code null}.
   * @param value the unsigned long to encode
   * @throws MALException if an error occurs 
   */
  public void encodeULong(ULong value) throws MALException;
  
  /**
   * Encodes an unsigned long that can be {@code null}.
   * @param value the unsigned long to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableULong(ULong value) throws MALException;

  /**
   * Encodes a string that cannot be {@code null}.
   * @param value the string to encode
   * @throws MALException if an error occurs 
   */
  public void encodeString(String value) throws MALException;
  
  /**
   * Encodes a string that can be {@code null}.
   * @param value the string to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableString(String value) throws MALException;

  /**
   * Encodes a time that cannot be {@code null}.
   * @param value the string to encode
   * @throws MALException if an error occurs 
   */
  public void encodeTime(Time value) throws MALException;
  
  /**
   * Encodes a time that can be {@code null}.
   * @param value the string to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableTime(Time value) throws MALException;
  
  /**
   * Encodes a fine time that cannot be {@code null}.
   * @param value the fine time to encode
   * @throws MALException if an error occurs 
   */
  public void encodeFineTime(FineTime value) throws MALException;
  
  /**
   * Encodes a fine time that can be {@code null}.
   * @param value the fine time to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableFineTime(FineTime value) throws MALException;

  /**
   * Encodes an element that cannot be {@code null}.
   * @param value the element to encode
   * @throws MALException if an error occurs 
   */
  public void encodeElement(Element value) throws MALException;
  
  /**
   * Encodes an element that can be {@code null}.
   * @param value the element to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableElement(Element value) throws MALException;

  /**
   * Creates a list encoder.
   * @param list the list to encode
   * @return the newly created list encoder
   * @throws IllegalArgumentException if the list is {@code null}
   * @throws MALException if an error occurs
   */
  public MALListEncoder createListEncoder(List value) 
    throws IllegalArgumentException, MALException;
  
  /**
   * Encodes an attribute that cannot be {@code null}.
   * @param value the attribute to encode
   * @throws MALException if an error occurs 
   */
  public void encodeAttribute(Attribute value) throws MALException;
  
  /**
   * Encodes an attribute that can be {@code null}.
   * @param value the attribute to encode
   * @throws MALException if an error occurs 
   */
  public void encodeNullableAttribute(Attribute value) throws MALException;

}
