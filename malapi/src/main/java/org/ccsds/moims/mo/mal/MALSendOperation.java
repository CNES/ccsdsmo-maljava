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
 * A {@code MALSendOperation} represents the specification
 * of a send operation.
 */
public class MALSendOperation extends MALOperation {
  
  private MALOperationStage sendStage;

  /**
   * Constructs a {@code MALSendOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param sendStage the initial stage
   */
  public MALSendOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet, 
      MALOperationStage sendStage) {
    super(number, name, replayable, InteractionType.SEND, capabilitySet);
    this.sendStage = sendStage;
    sendStage.setOperation(this);
  }
  
  /**
   * Returns the initial stage whatever stage number is specified.
   * @param stage the stage number
   * @return the initial stage
   */
  public MALOperationStage getOperationStage(UOctet stage) {
    return sendStage;
  }
}
