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
 * A {@code MALProgressOperation} represents the specification
 * of a progress operation.
 */
public class MALProgressOperation extends MALOperation {
  
  /**
   * Progress interaction initial stage number.
   */
  public static final byte _PROGRESS_STAGE = 0x01;
  
  /**
   * Progress interaction ack stage number.
   */
  public static final byte _PROGRESS_ACK_STAGE = 0x02;
  
  /**
   * Progress interaction update stage number.
   */
  public static final byte _PROGRESS_UPDATE_STAGE = 0x03;
  
  /**
   * Progress interaction response stage number.
   */
  public static final byte _PROGRESS_RESPONSE_STAGE = 0x04;
  
  /**
   * Progress interaction initial stage number.
   */
  public static final UOctet PROGRESS_STAGE = new UOctet(_PROGRESS_STAGE);
  
  /**
   * Progress interaction ack stage number.
   */
  public static final UOctet PROGRESS_ACK_STAGE = new UOctet(_PROGRESS_ACK_STAGE);
  
  /**
   * Progress interaction update stage number.
   */
  public static final UOctet PROGRESS_UPDATE_STAGE = new UOctet(_PROGRESS_UPDATE_STAGE);
  
  /**
   * Progress interaction response stage number.
   */
  public static final UOctet PROGRESS_RESPONSE_STAGE = new UOctet(_PROGRESS_RESPONSE_STAGE);
  
  private MALOperationStage progressStage;
  
  private MALOperationStage ackStage;
  
  private MALOperationStage updateStage;
  
  private MALOperationStage responseStage;

  /**
   * Constructs a {@code MALProgressOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param progressStage the initial stage
   * @param ackStage the ack stage
   * @param updateStage the update stage
   * @param responseStage the response stage
   */
  public MALProgressOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet,
      MALOperationStage progressStage,
      MALOperationStage ackStage,
      MALOperationStage updateStage,
      MALOperationStage responseStage) {
    super(number, name, replayable, InteractionType.PROGRESS, capabilitySet);
    this.progressStage = progressStage;
    this.ackStage = ackStage;
    this.updateStage = updateStage;
    this.responseStage = responseStage;
    progressStage.setOperation(this);
    ackStage.setOperation(this);
    updateStage.setOperation(this);
    responseStage.setOperation(this);
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
    case _PROGRESS_STAGE:
      return progressStage;
    case _PROGRESS_ACK_STAGE:
      return ackStage;
    case _PROGRESS_UPDATE_STAGE:
      return updateStage;
    case _PROGRESS_RESPONSE_STAGE:
      return responseStage;
    default:
      return null;
    }
  }
}
