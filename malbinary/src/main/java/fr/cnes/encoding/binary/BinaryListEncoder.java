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
package fr.cnes.encoding.binary;

import java.util.List;

import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListEncoder;
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

public class BinaryListEncoder implements MALListEncoder {
  
  private MALEncoder malEncoder;
  
  BinaryListEncoder(MALEncoder malEncoder) {
    this.malEncoder = malEncoder;
  }

  public void close() {
    // Do nothing
  }

  public void encodeBlob(Blob value) throws MALException {
    malEncoder.encodeBlob(value);
  }

  public void encodeBoolean(Boolean value) throws MALException {
    malEncoder.encodeBoolean(value);
  }

  public void encodeDuration(Duration value) throws MALException {
    malEncoder.encodeDuration(value);
  }

  public void encodeFloat(Float value) throws MALException {
    malEncoder.encodeFloat(value);
  }

  public void encodeDouble(Double value) throws MALException {
    malEncoder.encodeDouble(value);
  }

  public void encodeIdentifier(Identifier value) throws MALException {
    malEncoder.encodeIdentifier(value);
  }

  public void encodeOctet(Byte value) throws MALException {
    malEncoder.encodeOctet(value);
  }

  public void encodeShort(Short value) throws MALException {
    malEncoder.encodeShort(value);
  }

  public void encodeInteger(Integer value) throws MALException {
    malEncoder.encodeInteger(value);
  }

  public void encodeLong(Long value) throws MALException {
    malEncoder.encodeLong(value);
  }

  public void encodeString(String value) throws MALException {
    malEncoder.encodeString(value);
  }
  
  public void encodeURI(URI value) throws MALException {
    malEncoder.encodeURI(value);
  }

  public void encodeTime(Time value) throws MALException {
    malEncoder.encodeTime(value);
  }
  
  public void encodeFineTime(FineTime value) throws MALException {
    malEncoder.encodeFineTime(value);
  }

  public void encodeElement(Element value) throws MALException {
    malEncoder.encodeElement(value);
  }
/*
  public void encodeEnumeration(Enumeration value) throws MALException {
    malEncoder.encodeEnumeration(value);
  }
*/
  public MALListEncoder createListEncoder(List value) throws MALException {
    return malEncoder.createListEncoder(value);
  }
/*
  public void encodeComposite(Composite composite) throws MALException {
    malEncoder.encodeComposite(composite);
  }
*/
  public void encodeAttribute(Attribute element) throws MALException {
    malEncoder.encodeAttribute(element);
  }

  public void encodeNullableAttribute(Attribute element) throws MALException {
    malEncoder.encodeNullableAttribute(element);
  }

  public void encodeNullableBlob(Blob element) throws MALException {
    malEncoder.encodeNullableBlob(element);
  }

  public void encodeNullableBoolean(Boolean element) throws MALException {
    malEncoder.encodeNullableBoolean(element);
  }

  public void encodeNullableDouble(Double element) throws MALException {
    malEncoder.encodeNullableDouble(element);
  }

  public void encodeNullableDuration(Duration element) throws MALException {
    malEncoder.encodeNullableDuration(element);
  }

  public void encodeNullableElement(Element element) throws MALException {
    malEncoder.encodeNullableElement(element);
  }

  public void encodeNullableFineTime(FineTime element) throws MALException {
    malEncoder.encodeNullableFineTime(element);
  }

  public void encodeNullableFloat(Float element) throws MALException {
    malEncoder.encodeNullableFloat(element);
  }

  public void encodeNullableIdentifier(Identifier element) throws MALException {
    malEncoder.encodeNullableIdentifier(element);
  }

  public void encodeNullableInteger(Integer element) throws MALException {
    malEncoder.encodeNullableInteger(element);
  }

  public void encodeNullableLong(Long element) throws MALException {
    malEncoder.encodeNullableLong(element);
  }

  public void encodeNullableOctet(Byte element) throws MALException {
    malEncoder.encodeNullableOctet(element);
  }

  public void encodeNullableShort(Short element) throws MALException {
    malEncoder.encodeNullableShort(element);
  }

  public void encodeNullableString(String element) throws MALException {
    malEncoder.encodeNullableString(element);
  }

  public void encodeNullableTime(Time element) throws MALException {
    malEncoder.encodeNullableTime(element);
  }

  public void encodeNullableUInteger(UInteger element) throws MALException {
    malEncoder.encodeNullableUInteger(element);
  }

  public void encodeNullableULong(ULong element) throws MALException {
    malEncoder.encodeNullableULong(element);
  }

  public void encodeNullableUOctet(UOctet element) throws MALException {
    malEncoder.encodeNullableUOctet(element);
  }

  public void encodeNullableURI(URI element) throws MALException {
    malEncoder.encodeNullableURI(element);
  }

  public void encodeNullableUShort(UShort element) throws MALException {
    malEncoder.encodeNullableUShort(element);
  }

  public void encodeUInteger(UInteger element) throws MALException {
    malEncoder.encodeUInteger(element);
  }

  public void encodeULong(ULong element) throws MALException {
    malEncoder.encodeULong(element);
  }

  public void encodeUOctet(UOctet element) throws MALException {
    malEncoder.encodeUOctet(element);
  }

  public void encodeUShort(UShort element) throws MALException {
    malEncoder.encodeUShort(element);
  }
}
