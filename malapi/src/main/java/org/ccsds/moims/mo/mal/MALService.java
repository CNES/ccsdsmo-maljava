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

import java.util.ArrayList;
import java.util.Vector;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALService} represents the specification of a service.
 */
public class MALService {
  
  /**
   * Service number used when no service is defined.
   */
  public static final UShort NULL_SERVICE_NUMBER = new UShort(0);
  
  private UShort number;
  
  private Identifier name;
  
  private MALArea area;
  
  private Vector<MALOperation> operations;
  
  /**
   * Constructs a service description with the specified parameters.
   * @param number the number of the service
   * @param name the name of the service
   * @throws IllegalArgumentException if the number or the name or the version is {@code null}
   */
  public MALService(
      UShort number, 
      Identifier name) throws IllegalArgumentException {
    if (number == null) throw new IllegalArgumentException("Null number");
    if (name == null) throw new IllegalArgumentException("Null name");
    this.number = number;
    this.name = name;
    operations = new Vector<MALOperation>();
  }
  
  /**
   * Returns the name of the service
   * @return name of the service
   */
  public Identifier getName() {
    return name;
  }
  
  /**
   * Returns the area of the service
   * @return the area of the service
   */
  public MALArea getArea() {
    return area;
  }
  
  /**
   * Sets the area of the service.
   * @param area the area of the service
   * @throws IllegalArgumentException if the area is {@code null}
   */
  void setArea(MALArea area) throws IllegalArgumentException {
    if (area == null) throw new IllegalArgumentException("Null area");
    this.area = area;
  }
  
  /**
   * Returns the number of the service
   * @return the number of the service
   */
  public UShort getNumber() {
    return number;
  }
  
  /**
   * Adds an operation to the service.
   * @param operation operation to be added
   * @throws IllegalArgumentException if the operation is {@code null}
   */
  public void addOperation(MALOperation operation) throws IllegalArgumentException {
    if (operation == null) throw new IllegalArgumentException("Null operation");
    operation.setService(this);
    operations.addElement(operation);
  }
  
  /**
   * Returns the operation which number is specified.
   * @param opNumber the number of the operation
   * @return the operation which number is specified
   */
  public MALOperation getOperationByNumber(UShort opNumber) {
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = operations.elementAt(i);
      if (op.getNumber().getValue() == opNumber.getValue()) return op;
    }
    return null;
  }
  
  /**
   * Returns the operation which name is specified.
   * @param opName the name of the operation
   * @return the operation which name is specified
   */
  public MALOperation getOperationByName(Identifier opName) {
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = operations.elementAt(i);
      if (op.getName().equals(opName)) return op;
    }
    return null;
  }
  
  /**
   * Returns the operations which capability set is specified.
   * @param capabilitySet
   * @return the operations which capability set is specified
   */
  public MALOperation[] getOperationsByCapabilitySet(UShort capabilitySet) {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = operations.elementAt(i);
      if (op.getCapabilitySet().getValue() == capabilitySet.getValue()) {
        res.add(op);
      }
    }
    MALSendOperation[] array = new MALSendOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is SEND.
   * @return the operations which IP is SEND
   */
  public MALSendOperation[] getSendOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALSendOperation) {
        res.add(op);
      }
    }
    MALSendOperation[] array = new MALSendOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is SUBMIT.
   * @return the operations which IP is SUBMIT
   */
  public MALSubmitOperation[] getSubmitOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALSubmitOperation) {
        res.add(op);
      }
    }
    MALSubmitOperation[] array = new MALSubmitOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is REQUEST.
   * @return the operations which IP is REQUEST
   */
  public MALRequestOperation[] getRequestOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALRequestOperation) {
        res.add(op);
      }
    }
    MALRequestOperation[] array = new MALRequestOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is INVOKE.
   * @return the operations which IP is INVOKE
   */
  public MALInvokeOperation[] getInvokeOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALInvokeOperation) {
        res.add(op);
      }
    }
    MALInvokeOperation[] array = new MALInvokeOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is PROGRESS.
   * @return the operations which IP is PROGRESS
   */
  public MALProgressOperation[] getProgressOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALProgressOperation) {
        res.add(op);
      }
    }
    MALProgressOperation[] array = new MALProgressOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
  /**
   * Returns the operations which IP is PUB/SUB.
   * @return the operations which IP is PUB/SUB
   */
  public MALPubSubOperation[] getPubSubOperations() {
    ArrayList<MALOperation> res = new ArrayList<MALOperation>();
    int length = operations.size();
    for (int i = 0; i < length; i++) {
      MALOperation op = (MALOperation) operations.elementAt(i);
      if (op instanceof MALPubSubOperation) {
        res.add(op);
      }
    }
    MALPubSubOperation[] array = new MALPubSubOperation[res.size()];
    res.toArray(array);
    return array;
  }
  
}
