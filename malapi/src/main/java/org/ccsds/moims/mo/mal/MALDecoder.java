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
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALDecoder} decodes MAL elements. 
 */
public interface MALDecoder {
  
  /**
   * Decodes a blob that cannot be {@code null}.
   * @return the decoded blob
   * @throws MALException if an error occurs 
   */
  public Blob decodeBlob() throws MALException;
  
  /**
   * Decodes a blob that can be {@code null}.
   * @return the decoded blob
   * @throws MALException if an error occurs 
   */
  public Blob decodeNullableBlob() throws MALException;

  /**
   * Decodes a boolean that cannot be {@code null}.
   * @return the decoded boolean
   * @throws MALException if an error occurs 
   */
  public Boolean decodeBoolean() throws MALException;
  
  /**
   * Decodes a boolean that can be {@code null}.
   * @return the decoded boolean
   * @throws MALException if an error occurs 
   */
  public Boolean decodeNullableBoolean() throws MALException;

  /**
   * Decodes a duration that cannot be {@code null}.
   * @return the decoded duration
   * @throws MALException if an error occurs 
   */
  public Duration decodeDuration() throws MALException;
  
  /**
   * Decodes a duration that can be {@code null}.
   * @return the decoded duration
   * @throws MALException if an error occurs 
   */
  public Duration decodeNullableDuration() throws MALException;

  /**
   * Decodes a float that cannot be {@code null}.
   * @return the decoded float
   * @throws MALException if an error occurs 
   */
  public Float decodeFloat() throws MALException;
  
  /**
   * Decodes a float that can be {@code null}.
   * @return the decoded float
   * @throws MALException if an error occurs 
   */
  public Float decodeNullableFloat() throws MALException;

  /**
   * Decodes a double that cannot be {@code null}.
   * @return the decoded double
   * @throws MALException if an error occurs 
   */
  public Double decodeDouble() throws MALException;
  
  /**
   * Decodes a double that can be {@code null}.
   * @return the decoded double
   * @throws MALException if an error occurs 
   */
  public Double decodeNullableDouble() throws MALException;

  /**
   * Decodes an identifier that cannot be {@code null}.
   * @return the decoded identifier
   * @throws MALException if an error occurs 
   */
  public Identifier decodeIdentifier() throws MALException;
  
  /**
   * Decodes an identifier that can be {@code null}.
   * @return the decoded identifier
   * @throws MALException if an error occurs 
   */
  public Identifier decodeNullableIdentifier() throws MALException;
  
  /**
   * Decodes a URI that cannot be {@code null}.
   * @return the decoded URI
   * @throws MALException if an error occurs 
   */
  public URI decodeURI() throws MALException;
  
  /**
   * Decodes a URI that can be {@code null}.
   * @return the decoded URI
   * @throws MALException if an error occurs 
   */
  public URI decodeNullableURI() throws MALException;

  /**
   * Decodes a signed octet that cannot be {@code null}.
   * @return the decoded signed octet
   * @throws MALException if an error occurs 
   */
  public Byte decodeOctet() throws MALException;
  
  /**
   * Decodes a signed octet that can be {@code null}.
   * @return the decoded signed octet
   * @throws MALException if an error occurs 
   */
  public Byte decodeNullableOctet() throws MALException;
  
  /**
   * Decodes an unsigned octet that cannot be {@code null}.
   * @return the decoded unsigned octet
   * @throws MALException if an error occurs 
   */
  public UOctet decodeUOctet() throws MALException;
  
  /**
   * Decodes an unsigned octet that can be {@code null}.
   * @return the decoded unsigned octet
   * @throws MALException if an error occurs 
   */
  public UOctet decodeNullableUOctet() throws MALException;

  /**
   * Decodes a signed short that cannot be {@code null}.
   * @return the decoded signed short
   * @throws MALException if an error occurs 
   */
  public Short decodeShort() throws MALException;

  /**
   * Decodes a signed short that can be {@code null}.
   * @return the decoded signed short
   * @throws MALException if an error occurs 
   */
  public Short decodeNullableShort() throws MALException;
  
  /**
   * Decodes an unsigned short that cannot be {@code null}.
   * @return the decoded unsigned short
   * @throws MALException if an error occurs 
   */
  public UShort decodeUShort() throws MALException;
  
  /**
   * Decodes an unsigned short that can be {@code null}.
   * @return the decoded unsigned short
   * @throws MALException if an error occurs 
   */
  public UShort decodeNullableUShort() throws MALException;

  /**
   * Decodes a signed integer that cannot be {@code null}.
   * @return the decoded signed integer
   * @throws MALException if an error occurs 
   */
  public Integer decodeInteger() throws MALException;
  
  /**
   * Decodes a signed integer that can be {@code null}.
   * @return the decoded signed integer
   * @throws MALException if an error occurs 
   */
  public Integer decodeNullableInteger() throws MALException;
  
  /**
   * Decodes an unsigned integer that cannot be {@code null}.
   * @return the decoded unsigned integer
   * @throws MALException if an error occurs 
   */
  public UInteger decodeUInteger() throws MALException;
  
  /**
   * Decodes an unsigned integer that can be {@code null}.
   * @return the decoded unsigned integer
   * @throws MALException if an error occurs 
   */
  public UInteger decodeNullableUInteger() throws MALException;

  /**
   * Decodes a signed long that cannot be {@code null}.
   * @return the decoded signed long
   * @throws MALException if an error occurs 
   */
  public Long decodeLong() throws MALException;
  
  /**
   * Decodes a signed long that can be {@code null}.
   * @return the decoded signed long
   * @throws MALException if an error occurs 
   */
  public Long decodeNullableLong() throws MALException;
  
  /**
   * Decodes an unsigned long that cannot be {@code null}.
   * @return the decoded unsigned long
   * @throws MALException if an error occurs 
   */
  public ULong decodeULong() throws MALException;
  
  /**
   * Decodes an unsigned long that can be {@code null}.
   * @return the decoded unsigned long
   * @throws MALException if an error occurs 
   */
  public ULong decodeNullableULong() throws MALException;

  /**
   * Decodes a string that cannot be {@code null}.
   * @return the decoded string
   * @throws MALException if an error occurs 
   */
  public String decodeString() throws MALException;
  
  /**
   * Decodes a string that can be {@code null}.
   * @return the decoded string
   * @throws MALException if an error occurs 
   */
  public String decodeNullableString() throws MALException;

  /**
   * Decodes a time that cannot be {@code null}.
   * @return the decoded time
   * @throws MALException if an error occurs 
   */
  public Time decodeTime() throws MALException;
  
  /**
   * Decodes a time that can be {@code null}.
   * @return the decoded time
   * @throws MALException if an error occurs 
   */
  public Time decodeNullableTime() throws MALException;
  
  /**
   * Decodes a fine time that cannot be {@code null}.
   * @return the decoded fine time
   * @throws MALException if an error occurs 
   */
  public FineTime decodeFineTime() throws MALException;
  
  /**
   * Decodes a fine time that can be {@code null}.
   * @return the decoded fine time
   * @throws MALException if an error occurs 
   */
  public FineTime decodeNullableFineTime() throws MALException;

  /**
   * Decodes an element that cannot be {@code null}.
   * @return the decoded element
   * @throws MALException if an error occurs 
   */
  public Element decodeElement(Element element) throws MALException;
  
  /**
   * Decodes an element that can be {@code null}.
   * @return the decoded element
   * @throws MALException if an error occurs 
   */
  public Element decodeNullableElement(Element element) throws MALException;

  /**
   * Creates a list decoder.
   * @param list the list to decode
   * @return the newly created list decoder
   * @throws IllegalArgumentException if the list is {@code null}
   * @throws MALException if an error occurs
   */
  public MALListDecoder createListDecoder(List list) throws IllegalArgumentException, MALException;
  
  /**
   * Decodes an attribute that cannot be {@code null}.
   * @return the decoded attribute
   * @throws MALException if an error occurs 
   */
  public Attribute decodeAttribute() throws MALException;
  
  /**
   * Decodes an attribute that can be {@code null}.
   * @return the decoded attribute
   * @throws MALException if an error occurs 
   */
  public Attribute decodeNullableAttribute() throws MALException;
}
