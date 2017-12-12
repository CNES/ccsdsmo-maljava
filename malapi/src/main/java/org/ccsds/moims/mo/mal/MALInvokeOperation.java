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
 * A {@code MALInvokeOperation} represents the specification
 * of an invoke operation.
 */
public class MALInvokeOperation extends MALOperation {
  
  /**
   * Invoke interaction initial stage number.
   */
  public static final byte _INVOKE_STAGE = 0x01;
  
  /**
   * Invoke interaction ack stage number.
   */
  public static final byte _INVOKE_ACK_STAGE = 0x02;
  
  /**
   * Invoke interaction response stage number.
   */
  public static final byte _INVOKE_RESPONSE_STAGE = 0x03;
  
  /**
   * Invoke interaction initial stage number.
   */
  public static final UOctet INVOKE_STAGE = new UOctet(_INVOKE_STAGE);
  
  /**
   * Invoke interaction ack stage number.
   */
  public static final UOctet INVOKE_ACK_STAGE = new UOctet(_INVOKE_ACK_STAGE);
  
  /**
   * Invoke interaction response stage number.
   */
  public static final UOctet INVOKE_RESPONSE_STAGE = new UOctet(_INVOKE_RESPONSE_STAGE);
  
  private MALOperationStage invokeStage;
  
  private MALOperationStage ackStage;
  
  private MALOperationStage responseStage;

  /**
   * Constructs a {@code MALInvokeOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param invokeStage the initial stage
   * @param ackStage the ack stage
   * @param responseStage the response stage
   */
  public MALInvokeOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet,
      MALOperationStage invokeStage,
      MALOperationStage ackStage,
      MALOperationStage responseStage) {
    super(number, name, replayable, InteractionType.INVOKE, capabilitySet);
    this.invokeStage = invokeStage;
    this.ackStage = ackStage;
    this.responseStage = responseStage;
  }
  
  /**
   * Returns the operation stage which stage number is specified.
   * @param stage the stage number
   * @return the operation stage which stage number is specified
   * @throws IllegalArgumentException if the stage is {@code null}
   */
  public MALOperationStage getOperationStage(UOctet stage) {
    if (stage == null) throw new IllegalArgumentException("Null stage");
    switch (stage.getValue()) {
    case _INVOKE_STAGE:
      return invokeStage;
    case _INVOKE_ACK_STAGE:
      return ackStage;
    case _INVOKE_RESPONSE_STAGE:
      return responseStage;
    default:
      return null;
    }
  }
}
