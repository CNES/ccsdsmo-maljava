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
package org.ccsds.moims.mo.mal.provider;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;

/**
 * The {@code MALInteractionHandler} interface is to be implemented 
 * by any MAL clients in order to handle interactions on the provider side.
 */
public interface MALInteractionHandler {
  
  /**
   * Called when a {@code MALProvider} has been created for that handler.
   * @param provider the created {@code MALProvider}
   * @throws MALException if an error occurs
   */
  public void malInitialize(MALProvider provider) 
      throws MALException;

  /**
   * Handles a SEND interaction.
   * @param interaction the interaction context
   * @param body the received message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleSend(MALInteraction interaction, 
      MALMessageBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles a SUBMIT interaction.
   * @param interaction the interaction context
   * @param body the received message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleSubmit(MALSubmit interaction, 
      MALMessageBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles a REQUEST interaction.
   * @param interaction the interaction context
   * @param body the received message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleRequest(MALRequest interaction, 
      MALMessageBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles a INVOKE interaction.
   * @param interaction the interaction context
   * @param body the received message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleInvoke(MALInvoke interaction, 
      MALMessageBody body) throws MALInteractionException, MALException;
  
  /**
   * Handles a PROGRESS interaction.
   * @param interaction the interaction context
   * @param body the received message body
   * @throws MALInteractionException if a MAL standard error occurs
   * @throws MALException if a non-MAL error occurs
   */
  public void handleProgress(MALProgress interaction, 
      MALMessageBody body) throws MALInteractionException, MALException;
  
  /**
   * Called when a {@code MALProvider} has been closed for that handler.
   * @param provider the closed {@code MALProvider}
   * @throws MALException if an error occurs
   */
  public void malFinalize(MALProvider provider)
      throws MALException;

}
