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

public class MALHelper {

  public static final int _MAL_AREA_NUMBER = 1;
  public static final org.ccsds.moims.mo.mal.structures.UShort MAL_AREA_NUMBER = new org.ccsds.moims.mo.mal.structures.UShort(_MAL_AREA_NUMBER);
  public static final org.ccsds.moims.mo.mal.structures.Identifier MAL_AREA_NAME = new org.ccsds.moims.mo.mal.structures.Identifier("MAL");
  public static final org.ccsds.moims.mo.mal.structures.UOctet MAL_AREA_VERSION = new org.ccsds.moims.mo.mal.structures.UOctet((short) 1);
  public static final org.ccsds.moims.mo.mal.MALArea MAL_AREA = new org.ccsds.moims.mo.mal.MALArea(MAL_AREA_NUMBER, MAL_AREA_NAME,MAL_AREA_VERSION);

  /**
   * The error number for the DELIVERY_FAILED error.
   * Details: Confirmed communication error.
   */
  public static final long _DELIVERY_FAILED_ERROR_NUMBER = 65536L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DELIVERY_FAILED_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DELIVERY_FAILED_ERROR_NUMBER);

  /**
   * The error number for the DELIVERY_TIMEDOUT error.
   * Details: Unconfirmed communication error.
   */
  public static final long _DELIVERY_TIMEDOUT_ERROR_NUMBER = 65537L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DELIVERY_TIMEDOUT_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DELIVERY_TIMEDOUT_ERROR_NUMBER);

  /**
   * The error number for the DELIVERY_DELAYED error.
   * Details: Message queued somewhere awaiting contact.
   */
  public static final long _DELIVERY_DELAYED_ERROR_NUMBER = 65538L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DELIVERY_DELAYED_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DELIVERY_DELAYED_ERROR_NUMBER);

  /**
   * The error number for the DESTINATION_UNKNOWN error.
   * Details: Destination cannot be contacted.
   */
  public static final long _DESTINATION_UNKNOWN_ERROR_NUMBER = 65539L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DESTINATION_UNKNOWN_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DESTINATION_UNKNOWN_ERROR_NUMBER);

  /**
   * The error number for the DESTINATION_TRANSIENT error.
   * Details: Destination middleware reports destination application does not exist.
   */
  public static final long _DESTINATION_TRANSIENT_ERROR_NUMBER = 65540L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DESTINATION_TRANSIENT_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DESTINATION_TRANSIENT_ERROR_NUMBER);

  /**
   * The error number for the DESTINATION_LOST error.
   * Details: Destination lost halfway through conversation.
   */
  public static final long _DESTINATION_LOST_ERROR_NUMBER = 65541L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger DESTINATION_LOST_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_DESTINATION_LOST_ERROR_NUMBER);

  /**
   * The error number for the AUTHENTICATION_FAIL error.
   * Details: A failure to authenticate the message correctly.
   */
  public static final long _AUTHENTICATION_FAIL_ERROR_NUMBER = 65542L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger AUTHENTICATION_FAIL_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_AUTHENTICATION_FAIL_ERROR_NUMBER);

  /**
   * The error number for the AUTHORISATION_FAIL error.
   * Details: A failure in the MAL to authorise the message.
   */
  public static final long _AUTHORISATION_FAIL_ERROR_NUMBER = 65543L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger AUTHORISATION_FAIL_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_AUTHORISATION_FAIL_ERROR_NUMBER);

  /**
   * The error number for the ENCRYPTION_FAIL error.
   * Details: A failure in the MAL to encrypt/decrypt the message.
   */
  public static final long _ENCRYPTION_FAIL_ERROR_NUMBER = 65544L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger ENCRYPTION_FAIL_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_ENCRYPTION_FAIL_ERROR_NUMBER);

  /**
   * The error number for the UNSUPPORTED_AREA error.
   * Details: The destination does not support the service area.
   */
  public static final long _UNSUPPORTED_AREA_ERROR_NUMBER = 65545L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger UNSUPPORTED_AREA_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_UNSUPPORTED_AREA_ERROR_NUMBER);

  /**
   * The error number for the UNSUPPORTED_OPERATION error.
   * Details: The destination does not support the operation.
   */
  public static final long _UNSUPPORTED_OPERATION_ERROR_NUMBER = 65546L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger UNSUPPORTED_OPERATION_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_UNSUPPORTED_OPERATION_ERROR_NUMBER);

  /**
   * The error number for the UNSUPPORTED_VERSION error.
   * Details: The destination does not support the service version.
   */
  public static final long _UNSUPPORTED_VERSION_ERROR_NUMBER = 65547L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger UNSUPPORTED_VERSION_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_UNSUPPORTED_VERSION_ERROR_NUMBER);

  /**
   * The error number for the BAD_ENCODING error.
   * Details: The destination was unable to decode the message.
   */
  public static final long _BAD_ENCODING_ERROR_NUMBER = 65548L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger BAD_ENCODING_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_BAD_ENCODING_ERROR_NUMBER);

  /**
   * The error number for the INTERNAL error.
   * Details: An internal error has occurred.
   */
  public static final long _INTERNAL_ERROR_NUMBER = 65549L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger INTERNAL_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_INTERNAL_ERROR_NUMBER);

  /**
   * The error number for the UNKNOWN error.
   * Details: Operation specific.
   */
  public static final long _UNKNOWN_ERROR_NUMBER = 65550L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger UNKNOWN_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_UNKNOWN_ERROR_NUMBER);

  /**
   * The error number for the INCORRECT_STATE error.
   * Details: The destination was not in the correct state for the received message.
   */
  public static final long _INCORRECT_STATE_ERROR_NUMBER = 65551L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger INCORRECT_STATE_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_INCORRECT_STATE_ERROR_NUMBER);

  /**
   * The error number for the TOO_MANY error.
   * Details: Maximum number of subscriptions or providers of a broker has been exceeded.
   */
  public static final long _TOO_MANY_ERROR_NUMBER = 65552L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger TOO_MANY_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_TOO_MANY_ERROR_NUMBER);

  /**
   * The error number for the SHUTDOWN error.
   * Details: The component is being shutdown.
   */
  public static final long _SHUTDOWN_ERROR_NUMBER = 65553L;

  public static final org.ccsds.moims.mo.mal.structures.UInteger SHUTDOWN_ERROR_NUMBER =new org.ccsds.moims.mo.mal.structures.UInteger(_SHUTDOWN_ERROR_NUMBER);

  private static boolean initialized;

  public static synchronized void init(org.ccsds.moims.mo.mal.MALElementFactoryRegistry elementFactoryRegistry) throws org.ccsds.moims.mo.mal.MALException {
    if (! initialized) {
      org.ccsds.moims.mo.mal.MALContextFactory.registerArea(MAL_AREA);
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Blob.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.BlobFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.BlobList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.BlobListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.BOOLEAN_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.BooleanFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.BooleanList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.BooleanListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Duration.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.DurationFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.DurationList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.DurationListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.FLOAT_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FloatFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.FloatList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FloatListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.DOUBLE_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.DoubleFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.DoubleList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.DoubleListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Identifier.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IdentifierFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.IdentifierList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IdentifierListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.OCTET_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.OctetFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.OctetList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.OctetListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UOctet.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UOctetFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UOctetList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UOctetListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.SHORT_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.ShortFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.ShortList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.ShortListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UShort.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UShortFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UShortList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UShortListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.INTEGER_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IntegerFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.IntegerList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IntegerListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UInteger.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UIntegerFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UIntegerList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UIntegerListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.LONG_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.LongFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.LongList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.LongListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.ULong.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.ULongFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.ULongList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.ULongListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Union.STRING_SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.StringFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.StringList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.StringListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Time.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.TimeFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.TimeList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.TimeListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.FineTime.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FineTimeFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.FineTimeList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FineTimeListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.URI.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.URIFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.URIList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.URIListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.InteractionType.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.InteractionTypeFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.InteractionTypeList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.InteractionTypeListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.SessionType.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.SessionTypeFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.SessionTypeList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.SessionTypeListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.QoSLevel.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.QoSLevelFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.QoSLevelList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.QoSLevelListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UpdateType.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UpdateTypeFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UpdateTypeList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UpdateTypeListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Subscription.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.SubscriptionFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.SubscriptionList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.SubscriptionListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.EntityRequest.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.EntityRequestFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.EntityRequestList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.EntityRequestListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.EntityKey.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.EntityKeyFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.EntityKeyList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.EntityKeyListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UpdateHeader.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UpdateHeaderFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.UpdateHeaderList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.UpdateHeaderListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.IdBooleanPair.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IdBooleanPairFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.IdBooleanPairList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.IdBooleanPairListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.Pair.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.PairFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.PairList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.PairListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.NamedValue.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.NamedValueFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.NamedValueList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.NamedValueListFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.File.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FileFactory());
      elementFactoryRegistry.registerElementFactory(org.ccsds.moims.mo.mal.structures.FileList.SHORT_FORM, new org.ccsds.moims.mo.mal.structures.factory.FileListFactory());
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65536), new org.ccsds.moims.mo.mal.structures.Identifier("DELIVERY_FAILED"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65537), new org.ccsds.moims.mo.mal.structures.Identifier("DELIVERY_TIMEDOUT"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65538), new org.ccsds.moims.mo.mal.structures.Identifier("DELIVERY_DELAYED"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65539), new org.ccsds.moims.mo.mal.structures.Identifier("DESTINATION_UNKNOWN"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65540), new org.ccsds.moims.mo.mal.structures.Identifier("DESTINATION_TRANSIENT"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65541), new org.ccsds.moims.mo.mal.structures.Identifier("DESTINATION_LOST"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65542), new org.ccsds.moims.mo.mal.structures.Identifier("AUTHENTICATION_FAIL"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65543), new org.ccsds.moims.mo.mal.structures.Identifier("AUTHORISATION_FAIL"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65544), new org.ccsds.moims.mo.mal.structures.Identifier("ENCRYPTION_FAIL"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65545), new org.ccsds.moims.mo.mal.structures.Identifier("UNSUPPORTED_AREA"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65546), new org.ccsds.moims.mo.mal.structures.Identifier("UNSUPPORTED_OPERATION"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65547), new org.ccsds.moims.mo.mal.structures.Identifier("UNSUPPORTED_VERSION"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65548), new org.ccsds.moims.mo.mal.structures.Identifier("BAD_ENCODING"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65549), new org.ccsds.moims.mo.mal.structures.Identifier("INTERNAL"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65550), new org.ccsds.moims.mo.mal.structures.Identifier("UNKNOWN"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65551), new org.ccsds.moims.mo.mal.structures.Identifier("INCORRECT_STATE"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65552), new org.ccsds.moims.mo.mal.structures.Identifier("TOO_MANY"));
      org.ccsds.moims.mo.mal.MALContextFactory.registerError(new org.ccsds.moims.mo.mal.structures.UInteger(65553), new org.ccsds.moims.mo.mal.structures.Identifier("SHUTDOWN"));
      initialized = true;
    }
  }

  public static synchronized void deepInit(org.ccsds.moims.mo.mal.MALElementFactoryRegistry elementFactoryRegistry) throws org.ccsds.moims.mo.mal.MALException {
    init(elementFactoryRegistry);
  }

}