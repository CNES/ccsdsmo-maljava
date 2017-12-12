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
 * A {@code MALRequestOperation} represents the specification
 * of a request operation.
 */
public class MALRequestOperation extends MALOperation {
  
  /**
   * Request interaction initial stage number.
   */
  public static final byte _REQUEST_STAGE = 0x01;
  
  /**
   * Request interaction response stage number.
   */
  public static final byte _REQUEST_RESPONSE_STAGE = 0x02;
  
  /**
   * Request interaction initial stage number.
   */
  public static final UOctet REQUEST_STAGE = new UOctet(_REQUEST_STAGE);
  
  /**
   * Request interaction response stage number.
   */
  public static final UOctet REQUEST_RESPONSE_STAGE = new UOctet(_REQUEST_RESPONSE_STAGE);
  
  private MALOperationStage requestStage;
  
  private MALOperationStage responseStage;

  /**
   * Constructs a {@code MALRequestOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param requestStage the initial stage
   * @param responseStage the response stage
   */
  public MALRequestOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet, 
      MALOperationStage requestStage,
      MALOperationStage responseStage) {
    super(number, name, replayable, InteractionType.REQUEST, capabilitySet);
    this.requestStage = requestStage;
    this.responseStage = responseStage;
    requestStage.setOperation(this);
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
    case _REQUEST_STAGE:
      return requestStage;
    case _REQUEST_RESPONSE_STAGE:
      return responseStage;
    default:
      return null;
    }
  }

}
