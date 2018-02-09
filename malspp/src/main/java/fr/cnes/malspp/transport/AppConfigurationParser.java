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
package fr.cnes.malspp.transport;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import fr.cnes.encoding.base.DurationDecoder;
import fr.cnes.encoding.base.DurationEncoder;
import fr.cnes.encoding.base.FineTimeDecoder;
import fr.cnes.encoding.base.FineTimeEncoder;
import fr.cnes.encoding.base.TimeDecoder;
import fr.cnes.encoding.base.TimeEncoder;
import fr.cnes.encoding.binary.BinaryElementStreamFactory;
import fr.cnes.malspp.encoding.CDSFineTimeDecoder;
import fr.cnes.malspp.encoding.CDSFineTimeEncoder;
import fr.cnes.malspp.encoding.CDSTimeCode;
import fr.cnes.malspp.encoding.CDSTimeDecoder;
import fr.cnes.malspp.encoding.CDSTimeEncoder;
import fr.cnes.malspp.encoding.CUCDurationDecoder;
import fr.cnes.malspp.encoding.CUCDurationEncoder;
import fr.cnes.malspp.encoding.CUCFineTimeDecoder;
import fr.cnes.malspp.encoding.CUCFineTimeEncoder;
import fr.cnes.malspp.encoding.CUCTimeCode;
import fr.cnes.malspp.encoding.CUCTimeDecoder;
import fr.cnes.malspp.encoding.CUCTimeEncoder;
import fr.cnes.malspp.encoding.TimeCode;
import fr.cnes.malspp.transport.MALSPPTransport.QualifiedAPID;
import fr.dyade.aaa.common.Debug;

public class AppConfigurationParser extends DefaultHandler {
  
  public static Logger logger = Debug.getLogger(AppConfigurationParser.class.getName());
  
  public static final int TIME_CODE_CUC_CCSDS = 0;
  public static final int TIME_CODE_CUC_AGENCY = 1;
  public static final int TIME_CODE_CDS = 2;

  public static final String CONF_ELT = "conf";
  public static final String APP_ELT = "app";
  public static final String AUTHENTICATION_ID_ELT = "authenticationId";
  public static final String DOMAIN_ELT = "domain";
  public static final String DURATION_ELT = "duration";
  public static final String TIME_ELT = "time";
  public static final String FINE_TIME_ELT = "fineTime";
  public static final String UNIT_ELT = "unit";
  public static final String EPOCH_ELT = "epoch";
  public static final String NETWORK_ZONE_ELT = "networkZone";
  public static final String PACKET_DATA_FIELD_SIZE_ELT = "packetDataFieldSizeLimit";
  public static final String PRIORITY_ELT = "priority";
  public static final String SESSION_NAME_ELT = "sessionName";
  public static final String VARINT_SUPPORTED_ELT = "varintSupported";

  public static final String APID_QUALIFIER_ATT = "apidQualifier";
  public static final String APID_ATT = "apid";
  public static final String CODE_FORMAT_ATT = "codeFormat";
  public static final String VALUE_ATT = "value";
  public static final String TIME_SCALE_ATT = "timeScale";
  
  public static final String SCALE_TAI = "TAI";
  public static final String SCALE_UTC = "UTC";

  private HashMap<QualifiedAPID, AppConfiguration> appTable;
  
  private AppConfiguration defaultApp;

  private AppConfiguration currentApp;
  
  private TimeCode currentTimeCode;
  
  private CUCTimeCode currentCucTimeCode;
  
  private CDSTimeCode currentCdsTimeCode;
  
  private BinaryElementStreamFactory currentElementStreamFactory;
  
  private byte[] pfield;
  
  private int timeCode;

  public AppConfigurationParser(
      HashMap<QualifiedAPID, AppConfiguration> appTable) {
    super();
    this.appTable = appTable;
  }

  public AppConfiguration getDefaultApp() {
    return defaultApp;
  }

  public void parse(String confFile) throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance();

    StreamSource[] schemas = new StreamSource[1];
    schemas[0] = new StreamSource(getClass().getResourceAsStream(
        "/MalSppSchema.xsd"));

    SchemaFactory schemaFactory = SchemaFactory
        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(schemas);

    factory.setNamespaceAware(true);
    factory.setSchema(schema);
    // Validating is false as the validation is done with a schema
    factory.setValidating(false);
    SAXParser parser = factory.newSAXParser();
    XMLReader reader = parser.getXMLReader();
    reader.setContentHandler(this);
    reader.setErrorHandler(this);
    reader.parse(new InputSource(confFile));
  }
  
  private byte[] parseBlob(String toParse) throws SAXException {
    if (toParse.length() % 2 != 0)
      throw new SAXException("Missing 4 bits to make a Blob");
    int byteNumber = toParse.length() / 2;
    byte[] bytes = new byte[byteNumber];
    for (int i = 0; i < byteNumber; i++) {
      int start = i * 2;
      String hex = toParse.substring(start, start + 2);
      // Parse hexadecimal
      bytes[i] = (byte) Integer.parseInt(hex, 16);
    }
    return bytes;
  }
  
  private void parseTimeCode() throws SAXException {
    // Mask (bit 1-3): 0111 0000
    int mask = 0x70;
    int tc = pfield[0] & mask;
    switch (tc) {
    // Mask (bit 1-3): 0001 0000
    case 0x10:
      timeCode = TIME_CODE_CUC_CCSDS;
      break;
    // Mask (bit 1-3): 0010 0000
    case 0x20:
      timeCode = TIME_CODE_CUC_AGENCY;
      break;
    // Mask (bit 1-3): 0100 0000
    case 0x40:
      timeCode = TIME_CODE_CDS;
      break;
    default:
      throw new SAXException("Unexpected time code: " + tc);
    }
  }
  
  private void parsePfield(Attributes atts) throws SAXException {
    String codeFormat = atts.getValue(CODE_FORMAT_ATT);
    
    pfield = parseBlob(codeFormat);
    
    parseTimeCode();
    
    if (timeCode == TIME_CODE_CUC_CCSDS ||
        timeCode == TIME_CODE_CUC_AGENCY) {
      currentCucTimeCode = new CUCTimeCode();
      currentTimeCode = currentCucTimeCode;
      configureCucTimeCode();
    } else if (timeCode == TIME_CODE_CDS) {
      currentCdsTimeCode = new CDSTimeCode();
      currentTimeCode = currentCdsTimeCode;
      configureCdsTimeCode();
    }
    
    if (timeCode == TIME_CODE_CUC_CCSDS) {
      currentTimeCode.setCcsdsEpoch(true);
    }
  }
  
  private void configureCucTimeCode() {
    int basicTimeLength = 0;
    int fractionalTimeLength = 0;
    int pfieldIndex = 0;
    pfieldLoop: while (pfieldIndex < pfield.length) {
      int mask;
      if (pfieldIndex == 0) {
        // Mask (bit 4-5): 0000 1100
        mask = 0x0C;
        basicTimeLength += (pfield[pfieldIndex] & mask) >>> 2;
        basicTimeLength += 1;
      } else {
        // Mask (bit 1-2): 0110 0000
        mask = 0x6F;
        basicTimeLength += (pfield[pfieldIndex] & mask) >>> 5;
      }
      
      if (pfieldIndex == 0) {
        // Mask (bit 6-7): 0000 0011
        mask = 0x03;
        fractionalTimeLength += (pfield[pfieldIndex] & mask);
      } else {
        // Mask (bit 3-5): 0001 1100
        mask = 0x1C;
        fractionalTimeLength += (pfield[pfieldIndex] & mask) >>> 2;
      }
      
      // Mask (bit 0): 1000 0000
      mask = 0x80;
      int continuationBit = (pfield[pfieldIndex] & mask) >>> 7;
      if (continuationBit == 0) {
        break pfieldLoop;
      }
      pfieldIndex++;
    }
    currentCucTimeCode.setBasicTimeLength(basicTimeLength);
    currentCucTimeCode.setFractionalTimeLength(fractionalTimeLength);
  }
  
  private void configureCdsTimeCode() {
    int epochId = pfield[0] & 0x08;
    currentCdsTimeCode.setCcsdsEpoch(epochId == 0);
    
    int daySegmentLength = ((pfield[0] & 0x04) == 0x0) ? 2 : 3;
    int subMillisecondLength = (pfield[0] & 0x03) << 1;
    currentCdsTimeCode.setDaySegmentLength(daySegmentLength);
    currentCdsTimeCode.setSubMillisecondLength(subMillisecondLength);
  }

  public void startElement(String uri, String localName, String rawName,
      Attributes atts) throws SAXException {
    if (localName.equals(APP_ELT)) {
      int apidQualifier = Integer.parseInt(atts.getValue(APID_QUALIFIER_ATT));
      int apid = Integer.parseInt(atts.getValue(APID_ATT));
      QualifiedAPID qapid = new QualifiedAPID(apidQualifier, apid);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "App: " + qapid);
      currentElementStreamFactory = new BinaryElementStreamFactory();
      currentElementStreamFactory.setEncodedUpdate(true);
      currentApp = new AppConfiguration(currentElementStreamFactory);
      if (defaultApp == null) {
        defaultApp = currentApp;
      }
      appTable.put(qapid, currentApp);
    } else if (localName.equals(AUTHENTICATION_ID_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      currentApp.setAuthenticationId(new Blob(parseBlob(value)));
    } else if (localName.equals(DOMAIN_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      StringTokenizer st = new StringTokenizer(value, ".");
      IdentifierList domainId = new IdentifierList();
      while (st.hasMoreTokens()) {
        domainId.add(new Identifier(st.nextToken()));
      }
      currentApp.setDomain(domainId);
    } else if (localName.equals(DURATION_ELT)) {
      parsePfield(atts);
      DurationEncoder durationEncoder;
      DurationDecoder durationDecoder;
      switch (timeCode) {
      case TIME_CODE_CUC_CCSDS:
      case TIME_CODE_CUC_AGENCY:
        durationEncoder = new CUCDurationEncoder(currentCucTimeCode);
        durationDecoder = new CUCDurationDecoder(currentCucTimeCode);
        break;
      case TIME_CODE_CDS:
        throw new SAXException("CDS not allowed for MAL::Duration");
      default:
        throw new SAXException("Unexpected time code: " + timeCode);
      }
      currentApp.setDurationEncoder(durationEncoder);
      currentApp.setDurationDecoder(durationDecoder);
      currentElementStreamFactory.setDurationEncoder(durationEncoder);
      currentElementStreamFactory.setDurationDecoder(durationDecoder);
    } else if (localName.equals(TIME_ELT)) {
      parsePfield(atts);
      TimeEncoder timeEncoder;
      TimeDecoder timeDecoder;
      switch (timeCode) {
      case TIME_CODE_CUC_CCSDS:
      case TIME_CODE_CUC_AGENCY:
        timeEncoder = new CUCTimeEncoder(currentCucTimeCode);
        timeDecoder = new CUCTimeDecoder(currentCucTimeCode);
        break;
      case TIME_CODE_CDS:
        timeEncoder = new CDSTimeEncoder(currentCdsTimeCode);
        try {
          timeDecoder = new CDSTimeDecoder(currentCdsTimeCode);
        } catch (Exception e) {
          if (logger.isLoggable(BasicLevel.ERROR))
            logger.log(BasicLevel.ERROR, "", e);
          throw new SAXException(e);
        }
        break;
      default:
        throw new SAXException("Unexpected time code: " + timeCode);
      }
      currentApp.setTimeEncoder(timeEncoder);
      currentApp.setTimeDecoder(timeDecoder);
      currentElementStreamFactory.setTimeEncoder(timeEncoder);
      currentElementStreamFactory.setTimeDecoder(timeDecoder);
    } else if (localName.equals(FINE_TIME_ELT)) {
      parsePfield(atts);
      FineTimeEncoder fineTimeEncoder;
      FineTimeDecoder fineTimeDecoder;
      switch (timeCode) {
      case TIME_CODE_CUC_CCSDS:
      case TIME_CODE_CUC_AGENCY:
        fineTimeEncoder = new CUCFineTimeEncoder(currentCucTimeCode);
        fineTimeDecoder = new CUCFineTimeDecoder(currentCucTimeCode);
        break;
      case TIME_CODE_CDS:
        fineTimeEncoder = new CDSFineTimeEncoder(currentCdsTimeCode);
        try {
          fineTimeDecoder = new CDSFineTimeDecoder(currentCdsTimeCode);
        } catch (Exception e) {
          if (logger.isLoggable(BasicLevel.ERROR))
            logger.log(BasicLevel.ERROR, "", e);
          throw new SAXException(e);
        }
        break;
      default:
        throw new SAXException("Unexpected time code: " + timeCode);
      }
      currentApp.setFineTimeEncoder(fineTimeEncoder);
      currentApp.setFineTimeDecoder(fineTimeDecoder);
      currentElementStreamFactory.setFineTimeEncoder(fineTimeEncoder);
      currentElementStreamFactory.setFineTimeDecoder(fineTimeDecoder);
    } else if (localName.equals(UNIT_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      if (value.length() == 0) {
        currentTimeCode.setUnit(TimeCode.UNIT_SECOND);
      } else {
        try {
          int unit = TimeCode.parseUnit(value);
          currentTimeCode.setUnit(unit);
        } catch (Exception e) {
          throw new SAXException(e);
        }
      }
    } else if (localName.equals(EPOCH_ELT)) {
      if (currentTimeCode.isCcsdsEpoch())
        throw new SAXException("Using CCSDS recommended epoch for: "
            + currentApp);

      String value = atts.getValue(VALUE_ATT);
      String timeScale = atts.getValue(TIME_SCALE_ATT);
      TimeScale ts;
      if (SCALE_TAI.equals(timeScale)) {
        ts = TimeScalesFactory.getTAI();
      } else if (SCALE_UTC.equals(timeScale)) {
        try {
          ts = TimeScalesFactory.getUTC();
        } catch (OrekitException e) {
          throw new SAXException(e);
        }
      } else {
        throw new SAXException("Unexpected time scale: " + timeScale);
      }
      AbsoluteDate epoch = new AbsoluteDate(value, ts);
      currentTimeCode.setEpoch(epoch);
    } else if (localName.equals(NETWORK_ZONE_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      currentApp.setNetworkZone(new Identifier(value));
    } else if (localName.equals(PACKET_DATA_FIELD_SIZE_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      currentApp.setPacketDataFieldSizeLimit(Integer.parseInt(value));
    } else if (localName.equals(PRIORITY_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      currentApp.setPriority(new UInteger(Long.parseLong(value)));
    } else if (localName.equals(SESSION_NAME_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      currentApp.setSessionName(new Identifier(value));
    } else if (localName.equals(VARINT_SUPPORTED_ELT)) {
      String value = atts.getValue(VALUE_ATT);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "varintSupported=" + value);
      currentApp.setVarintSupported(Boolean.parseBoolean(value));
    }
  }
  
}
 