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
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALOperation} represents the specification
 * of an operation.
 */
public abstract class MALOperation {
  
  private MALService service;
  
  private UShort number;
  
  private Identifier name;
  
  private Boolean replayable;
  
  private InteractionType interactionType;
  
  private UShort capabilitySet;

  /**
   * Constructs an operation description with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable the boolean indicating whether the operation
   * can be replayed or not
   * @param interactionType the type of interaction pattern
   * used by this operation
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @throws IllegalArgumentException
   */
  public MALOperation(UShort number,
      Identifier name, Boolean replayable,
      InteractionType interactionType,
      UShort capabilitySet) throws IllegalArgumentException {
    if (number == null) throw new IllegalArgumentException("Null number");
    if (name == null) throw new IllegalArgumentException("Null name");
    if (replayable == null) throw new IllegalArgumentException("Null replayable");
    if (interactionType == null) throw new IllegalArgumentException("Null interaction type");
    if (capabilitySet == null) throw new IllegalArgumentException("Null capability set");
    this.number = number;
    this.name = name;
    this.replayable = replayable;
    this.interactionType = interactionType;
    this.capabilitySet = capabilitySet;
  }
  
  /**
   * Sets the service of this operation.
   * @param service the service of this operation.
   */
  void setService(MALService service) {
    if (service == null) throw new IllegalArgumentException("Null service");
    this.service = service;
  }
  
  /**
   * Returns the interaction type.
   * @return the interaction type
   */
  public InteractionType getInteractionType() {
    return interactionType;
  }
  
  /**
   * Returns the name of the operation.
   * @return the name of the operation
   */
  public Identifier getName() {
    return name;
  }
  
  /**
   * Returns the number of the operation.
   * @return the number of the operation
   */
  public UShort getNumber() {
    return number;
  }
  
  /**
   * Returns the boolean indicating whether the operation
   * can be replayed or not.
   * @return the boolean indicating whether the operation
   * can be replayed or not
   */
  public Boolean isReplayable() {
    return replayable;
  }
  
  /**
   * Returns the service which the operation belongs to.
   * @return the service which the operation belongs to
   */
  public MALService getService() {
    return service;
  }
  
  /**
   * Returns the capability set which the operation belongs to.
   * @return Returns the capabilitySet.
   */
  public UShort getCapabilitySet() {
    return capabilitySet;
  }
  
  /**
   * Returns the operation stage which stage number is specified.
   * @param stage the stage number
   * @return the operation stage which stage number is specified
   */
  public abstract MALOperationStage getOperationStage(UOctet stage);

}
