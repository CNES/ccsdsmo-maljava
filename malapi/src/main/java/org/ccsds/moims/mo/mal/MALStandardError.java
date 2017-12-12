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

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;

/**
 * A {@code MALStandardError} represents a MAL standard error.
 */
public final class MALStandardError {

  private UInteger errorNumber;

  private Object extraInformation;

  /**
   * Constructs a {@code MALStandardError} with the specified
   * parameters.
   * @param errorNumber the number of the error
   * @param extraInformation the extra information of the error
   */
  public MALStandardError(UInteger errorNumber, Object extraInformation) {
    this.errorNumber = errorNumber;
    this.extraInformation = extraInformation;
  }

  /**
   * Returns the number of the error.
   * @return Returns the number of the error
   */
  public UInteger getErrorNumber() {
    return errorNumber;
  }

  /**
   * Sets the number of the error.
   * @param errorNumber the number of the error
   */
  public void setErrorNumber(UInteger errorNumber) {
    this.errorNumber = errorNumber;
  }

  /**
   * Returns the extra information of the error.
   * @return Returns the extra information of the error
   */
  public Object getExtraInformation() {
    return extraInformation;
  }

  /**
   * Sets the extra information of the error.
   * @param extraInformation the extra information of the error
   */
  public void setExtraInformation(Object extraInformation) {
    this.extraInformation = extraInformation;
  }
  
  public Identifier getErrorName() {
    return MALContextFactory.lookupError(errorNumber);
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((errorNumber == null) ? 0 : errorNumber.hashCode());
    result = prime * result + ((extraInformation == null) ? 0 : extraInformation.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof MALStandardError) {
      MALStandardError other = (MALStandardError) obj;
      if (errorNumber == null) {
        if (other.errorNumber != null) return false;
      } else {
        if (! errorNumber.equals(other.errorNumber)) return false;
      }
      if (extraInformation == null) {
        if (other.extraInformation != null) return false;
      } else {
        if (! extraInformation.equals(other.extraInformation)) return false;
      }
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append('(');
    buf.append(super.toString());
    buf.append(",errorNumber");
    buf.append('=');
    buf.append(errorNumber);
    buf.append(",errorName");
    buf.append('=');
    buf.append(MALContextFactory.lookupError(errorNumber));
    buf.append(",extraInformation");
    buf.append('=');
    buf.append(extraInformation);
    buf.append(')');
    return buf.toString();
  }
}
