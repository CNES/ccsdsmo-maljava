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

import org.ccsds.moims.mo.mal.structures.UOctet;

/**
 * A {@code MALOperationStage} represents the stage
 * of an operation.
 */
public class MALOperationStage {
  
  private UOctet number;
  
  private Object[] elementShortForms;
  
  private Object[] lastElementShortForms;

  private MALOperation operation;

  /**
   * Constructs an operation stage description with the specified
   * parameters.
   * @param number the stage number
   * @param elementShortForms short forms of the body element types
   * @param lastElementShortForms short forms of the types that can be used 
   * for the last element in case of polymorphism
   * @throws IllegalArgumentException if the number or the short forms are {@code null}
   */
  public MALOperationStage(UOctet number, 
      Object[] elementShortForms,
      Object[] lastElementShortForms) throws IllegalArgumentException {
    if (number == null) throw new IllegalArgumentException("Null stage number");
    if (elementShortForms == null) throw new IllegalArgumentException("Null short forms");
    if (lastElementShortForms == null) throw new IllegalArgumentException("Null last element short forms");
    this.number = number;
    this.elementShortForms = elementShortForms;
    this.lastElementShortForms = lastElementShortForms;
  }

  /**
   * Returns the stage number.
   * @return the stage number
   */
  public UOctet getNumber() {
    return number;
  }

  /**
   * Returns the short forms of the body element types.
   * @return the short forms of the body element types
   */
  public Object[] getElementShortForms() {
    return elementShortForms;
  }

  /**
   * Returns the short forms of the types that can be used 
   * for the last element in case of polymorphism.
   * @return the short forms of the types that can be used 
   * for the last element in case of polymorphism
   */
  public Object[] getLastElementShortForms() {
    return lastElementShortForms;
  }

  /**
   * Returns the operation that owns this stage.
   * @return the operation that owns this stage.
   */
  public MALOperation getOperation() {
    return operation;
  }
  
  /**
   * Sets the operation of the stage.
   * @param operation the operation of the stage
   * @throws IllegalArgumentException if the operation is {@code null}
   */
  void setOperation(MALOperation operation) throws IllegalArgumentException {
    if (operation == null) throw new IllegalArgumentException("Null operation");
    this.operation = operation;
  }

  /**
   * Sets the short forms of the types that can be used 
   * for the last element in case of polymorphism.
   * @param lastElementShortForms the short forms of the types that can be used 
   * for the last element in case of polymorphism
   */
  public void setLastElementShortForms(Object[] lastElementShortForms) {
    this.lastElementShortForms = lastElementShortForms;
  }
}
