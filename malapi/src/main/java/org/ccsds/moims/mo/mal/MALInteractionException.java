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

/**
 * A {@code MALInteractionException} signals a MAL standard error.
 */
public class MALInteractionException extends Exception {
  
  private static String getExtraInformation(Object extraInformation) {
    if (extraInformation != null) {
      return extraInformation.toString();
    } else {
      return null;
    }
  }
  
  private MALStandardError standardError;

  /**
   * Constructs a {@code MALInteractionException} with the specified MAL standard error.
   * @param standardError a MAL standard error
   */
  public MALInteractionException(MALStandardError standardError) {
    super(getExtraInformation(standardError.getExtraInformation()));
    this.standardError = standardError;
  }
  
  /**
   * Returns the MAL standard error.
   * @return the MAL standard error
   */
  public MALStandardError getStandardError() {
    return standardError;
  }

  public String toString() {
    return "MALInteractionException [standardError=" + standardError + "]";
  }
}
