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
 * A {@code MALSubmitOperation} represents the specification
 * of a submit operation.
 */
public class MALSubmitOperation extends MALOperation {
  
  /**
   * Submit interaction initial stage number.
   */
  public static final byte _SUBMIT_STAGE = (byte) 0x01;
  
  /**
   * Submit interaction ack stage number.
   */
  public static final byte _SUBMIT_ACK_STAGE = (byte) 0x02;
  
  /**
   * Submit interaction initial stage number.
   */
  public static final UOctet SUBMIT_STAGE = new UOctet(_SUBMIT_STAGE);
  
  /**
   * Submit interaction ack stage number.
   */
  public static final UOctet SUBMIT_ACK_STAGE = new UOctet(_SUBMIT_ACK_STAGE);
  
  private MALOperationStage submitStage;
  
  private MALOperationStage ackStage;
  
  /**
   * Constructs a {@code MALSubmitOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable the boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param submitStage the initial stage
   */
  public MALSubmitOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet, 
      MALOperationStage submitStage) {
    super(number, name, replayable, InteractionType.SUBMIT, capabilitySet);
    this.submitStage = submitStage;
    this.ackStage = new MALOperationStage(SUBMIT_ACK_STAGE, 
        new Long[0], new Long[0]);
    submitStage.setOperation(this);
    ackStage.setOperation(this);
  }
  
  /**
   * Returns the operation stage which stage number is specified.
   * @param stage the stage number
   * @return the operation stage which stage number is specified
   * @throws IllegalArgumentException if the stage is {@code null}
   */
  public MALOperationStage getOperationStage(UOctet stage) throws IllegalArgumentException {
    if (stage == null) throw new IllegalArgumentException("Null stage");
    switch (stage.getValue()) {
    case _SUBMIT_STAGE:
      return submitStage;
    case _SUBMIT_ACK_STAGE:
      return ackStage;
    default:
      return null;
    }
  }
}
