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

import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;

/**
 * A {@code MALPubSubOperation} represents the specification
 * of a publish-subscribe operation.
 */
public class MALPubSubOperation extends MALOperation {
  
  /**
   * Publish-subscribe interaction register stage number.
   */
  public static final byte _REGISTER_STAGE = 0x01;
  
  /**
   * Publish-subscribe interaction register ack stage number.
   */
  public static final byte _REGISTER_ACK_STAGE = 0x02;
  
  /**
   * Publish-subscribe interaction publish register stage number.
   */
  public static final byte _PUBLISH_REGISTER_STAGE = 0x03;
  
  /**
   * Publish-subscribe interaction publish register stage number.
   */
  public static final byte _PUBLISH_REGISTER_ACK_STAGE = 0x04;
  
  /**
   * Publish-subscribe interaction publish stage number.
   */
  public static final byte _PUBLISH_STAGE = 0x05;
  
  /**
   * Publish-subscribe interaction notify stage number.
   */
  public static final byte _NOTIFY_STAGE = 0x06;
  
  /**
   * Publish-subscribe interaction deregister stage number.
   */
  public static final byte _DEREGISTER_STAGE = 0x07;
  
  /**
   * Publish-subscribe interaction deregister stage number.
   */
  public static final byte _DEREGISTER_ACK_STAGE = 0x08;
  
  /**
   * Publish-subscribe interaction publish deregister stage number.
   */
  public static final byte _PUBLISH_DEREGISTER_STAGE = 0x09;
  
  /**
   * Publish-subscribe interaction publish deregister ack stage number.
   */
  public static final byte _PUBLISH_DEREGISTER_ACK_STAGE = 0x0A;
  
  /**
   * Publish-subscribe interaction register stage number.
   */
  public static final UOctet REGISTER_STAGE = new UOctet(_REGISTER_STAGE);
  
  /**
   * Publish-subscribe interaction register ack stage number.
   */
  public static final UOctet REGISTER_ACK_STAGE = new UOctet(_REGISTER_ACK_STAGE);
  
  /**
   * Publish-subscribe interaction publish register stage number.
   */
  public static final UOctet PUBLISH_REGISTER_STAGE = new UOctet(_PUBLISH_REGISTER_STAGE);
  
  /**
   * Publish-subscribe interaction publish register ack stage number.
   */
  public static final UOctet PUBLISH_REGISTER_ACK_STAGE = new UOctet(_PUBLISH_REGISTER_ACK_STAGE);
  
  /**
   * Publish-subscribe interaction publish stage number.
   */
  public static final UOctet PUBLISH_STAGE = new UOctet(_PUBLISH_STAGE);
  
  /**
   * Publish-subscribe interaction notify stage number.
   */
  public static final UOctet NOTIFY_STAGE = new UOctet(_NOTIFY_STAGE);
  
  /**
   * Publish-subscribe interaction deregister stage number.
   */
  public static final UOctet DEREGISTER_STAGE = new UOctet(_DEREGISTER_STAGE);
  
  /**
   * Publish-subscribe interaction deregister ack stage number.
   */
  public static final UOctet DEREGISTER_ACK_STAGE = new UOctet(_DEREGISTER_ACK_STAGE);
  
  /**
   * Publish-subscribe interaction publish deregister stage number.
   */
  public static final UOctet PUBLISH_DEREGISTER_STAGE = new UOctet(_PUBLISH_DEREGISTER_STAGE);
  
  /**
   * Publish-subscribe interaction publish deregister ack stage number.
   */
  public static final UOctet PUBLISH_DEREGISTER_ACK_STAGE = new UOctet(_PUBLISH_DEREGISTER_ACK_STAGE);
  
  private MALOperationStage registerStage;
  
  private MALOperationStage registerAckStage;
  
  private MALOperationStage publishRegisterStage;
  
  private MALOperationStage publishRegisterAckStage;
  
  private MALOperationStage publishStage;
  
  private MALOperationStage notifyStage;
  
  private MALOperationStage deregisterStage;
  
  private MALOperationStage deregisterAckStage;
  
  private MALOperationStage publishDeregisterStage;
  
  private MALOperationStage publishDeregisterAckStage;
  
  /**
   * Constructs a {@code MALPubSubOperation} with the specified
   * parameters.
   * @param number the number of the operation
   * @param name the name of the operation
   * @param replayable boolean indicating whether the operation
   * can be replayed or not
   * @param capabilitySet the number of the capability set which the
   * operation belongs to
   * @param updateListTypeShortForms the short forms of the update lists
   * @param lastUpdateListShortForms the short forms of the update lists 
   * that can be assigned to the last element of the message body
   */
  public MALPubSubOperation(UShort number,
      Identifier name, Boolean replayable, UShort capabilitySet,
      Object[] updateListTypeShortForms,
      Object[] lastUpdateListShortForms) {
    super(number, name, replayable, InteractionType.PUBSUB, capabilitySet);

    registerStage = new MALOperationStage(REGISTER_STAGE, 
        new Long[] { Subscription.SHORT_FORM },
        new Long[0]);
    
    registerAckStage = new MALOperationStage(REGISTER_ACK_STAGE, 
        new Long[0],
        new Long[0]);

    publishRegisterStage = new MALOperationStage(PUBLISH_REGISTER_STAGE, 
        new Long[] { EntityKeyList.SHORT_FORM },
        new Long[0]);
    
    publishRegisterAckStage = new MALOperationStage(PUBLISH_REGISTER_ACK_STAGE, 
        new Long[0],
        new Long[0]);
    
    Object[] publishShortForms = new Long[updateListTypeShortForms.length + 1];
    publishShortForms[0] = UpdateHeaderList.SHORT_FORM;
    for (int i = 0; i < updateListTypeShortForms.length; i++) 
    {
      publishShortForms[i + 1] = updateListTypeShortForms[i];
    };
    
    publishStage = new MALOperationStage(PUBLISH_STAGE, 
        publishShortForms,
        lastUpdateListShortForms);
    
    Object[] notifyShortForms = new Long[updateListTypeShortForms.length + 2];
    notifyShortForms[0] =  Identifier.SHORT_FORM;
    notifyShortForms[1] =  UpdateHeaderList.SHORT_FORM;
    for (int i = 0; i < updateListTypeShortForms.length; i++) 
    {
      notifyShortForms[i + 2] = updateListTypeShortForms[i];
    };
    
    notifyStage = new MALOperationStage(NOTIFY_STAGE, 
        notifyShortForms,
        lastUpdateListShortForms);
    
    deregisterStage = new MALOperationStage(DEREGISTER_STAGE, 
        new Long[] { IdentifierList.SHORT_FORM },
        new Long[0]);
    
    deregisterAckStage = new MALOperationStage(DEREGISTER_ACK_STAGE, 
        new Long[0],
        new Long[0]);
    
    publishDeregisterStage = new MALOperationStage(PUBLISH_DEREGISTER_STAGE, 
        new Long[] { EntityKeyList.SHORT_FORM },
        new Long[0]);
    
    publishDeregisterAckStage = new MALOperationStage(PUBLISH_DEREGISTER_ACK_STAGE, 
        new Long[0],
        new Long[0]);
    
    registerStage.setOperation(this);
    registerAckStage.setOperation(this);
    publishRegisterStage.setOperation(this);
    publishRegisterAckStage.setOperation(this);
    publishStage.setOperation(this);
    notifyStage.setOperation(this);
    deregisterStage.setOperation(this);
    deregisterAckStage.setOperation(this);
    publishDeregisterStage.setOperation(this);
    publishDeregisterAckStage.setOperation(this);
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
    case _REGISTER_STAGE:
      return registerStage;
    case _REGISTER_ACK_STAGE:
      return registerAckStage;
    case _PUBLISH_REGISTER_STAGE:
      return publishRegisterStage;
    case _PUBLISH_REGISTER_ACK_STAGE:
      return publishRegisterAckStage;
    case _PUBLISH_STAGE:
      return publishStage;
    case _NOTIFY_STAGE:
      return notifyStage;
    case _DEREGISTER_STAGE:
      return deregisterStage;
    case _DEREGISTER_ACK_STAGE:
      return deregisterAckStage;
    case _PUBLISH_DEREGISTER_STAGE:
      return publishDeregisterStage;
    case _PUBLISH_DEREGISTER_ACK_STAGE:
      return publishDeregisterAckStage;
    default:
      return null;
    }
  }
}
