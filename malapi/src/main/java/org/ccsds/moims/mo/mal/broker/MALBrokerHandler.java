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
package org.ccsds.moims.mo.mal.broker;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.transport.MALDeregisterBody;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;
import org.ccsds.moims.mo.mal.transport.MALPublishRegisterBody;
import org.ccsds.moims.mo.mal.transport.MALRegisterBody;

/**
 * A {@code MALBrokerHandler} enables a MAL client
 * to handle Pub/Sub interactions on the broker side.
 */
public interface MALBrokerHandler {
  
  /**
   * Called at a <code>MALBrokerBinding</code> creation.
   * @param brokerBinding the created <code>MALBrokerBinding</code>.
   */
  public void malInitialize(MALBrokerBinding brokerBinding);
  
  /**
   * Handles the REGISTER stage of a PUBLISH-SUBSCRIBE interaction.
   * @param interaction the context of the interaction
   * @param body the message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleRegister(MALInteraction interaction, 
      MALRegisterBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles the PUBLISH REGISTER stage of a PUBLISH-SUBSCRIBE interaction.
   * @param interaction the context of the interaction
   * @param body the message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handlePublishRegister(MALInteraction interaction, 
      MALPublishRegisterBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles the PUBLISH stage of a PUBLISH-SUBSCRIBE interaction.
   * @param interaction the context of the interaction
   * @param body the message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handlePublish(MALInteraction interaction, 
      MALPublishBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles the DEREGISTER stage of a PUBLISH-SUBSCRIBE interaction.
   * @param interaction the context of the interaction
   * @param body the message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleDeregister(MALInteraction interaction, 
      MALDeregisterBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles the PUBLISH DEREGISTER stage of a PUBLISH-SUBSCRIBE interaction.
   * @param interaction the context of the interaction
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handlePublishDeregister(MALInteraction interaction) 
      throws MALInteractionException, MALException;
  
  /**
   * Called at a <code>MALBrokerBinding</code> closure.
   * @param brokerBinding the closed <code>MALBrokerBinding</code>.
   */
  public void malFinalize(MALBrokerBinding brokerBinding);

}
