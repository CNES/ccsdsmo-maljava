package fr.cnes.encoding;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALEncoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.junit.Test;

import fr.cnes.encoding.base.BinaryFineTimeDecoder;
import fr.cnes.encoding.base.BinaryFineTimeEncoder;
import fr.cnes.encoding.base.BinaryTimeDecoder;
import fr.cnes.encoding.base.BinaryTimeEncoder;
import fr.cnes.encoding.base.Decoder;
import fr.cnes.encoding.base.DurationDecoder;
import fr.cnes.encoding.base.DurationEncoder;
import fr.cnes.encoding.base.FineTimeDecoder;
import fr.cnes.encoding.base.FineTimeEncoder;
import fr.cnes.encoding.base.JavaDurationDecoder;
import fr.cnes.encoding.base.JavaDurationEncoder;
import fr.cnes.encoding.base.JavaTimeDecoder;
import fr.cnes.encoding.base.JavaTimeEncoder;
import fr.cnes.encoding.base.TimeDecoder;
import fr.cnes.encoding.base.TimeEncoder;
import fr.cnes.encoding.binary.BinaryDecoder;
import fr.cnes.encoding.binary.BinaryEncoder;
import fr.cnes.encoding.splitbinary.SplitBinaryDecoder;
import fr.cnes.encoding.splitbinary.SplitBinaryEncoder;
import junit.framework.Assert;

public class Test1 {
	final static UOctet UOCTET1 = new UOctet((short) 0);
	final static UOctet UOCTET2 = new UOctet((short) 255);
	final static Byte OCTET1 = new Byte((byte) -128);
	final static Byte OCTET2 = new Byte((byte) 0);
	final static Byte OCTET3 = new Byte((byte) 127);
	final static UShort USHORT1 = new UShort(0);
	final static UShort USHORT2 = new UShort(256);
	final static UShort USHORT3 = new UShort(65535);
	final static Short SHORT1 = new Short((short) -32768);
	final static Short SHORT2 = new Short((short) -256);
	final static Short SHORT3 = new Short((short) 0);
	final static Short SHORT4 = new Short((short) 256);
	final static Short SHORT5 = new Short((short) 32767);
	final static UInteger UINT1 = new UInteger(0);
	final static UInteger UINT2 = new UInteger(256);
	final static UInteger UINT3 = new UInteger(65536);
	final static UInteger UINT4 = new UInteger(4294967295L);
	final static Integer INT1 = new Integer(-2147483648);
	final static Integer INT2 = new Integer(-32768);
	final static Integer INT3 = new Integer(-256);
	final static Integer INT4 = new Integer(0);
	final static Integer INT5 = new Integer(256);
	final static Integer INT6 = new Integer(32767);
	final static Integer INT7 = new Integer(2147483647);
	final static ULong ULONG1 = new ULong(new BigInteger("0"));
	final static ULong ULONG2 = new ULong(new BigInteger("65536"));
	final static ULong ULONG3 = new ULong(new BigInteger("4294967295"));
	final static Long LONG1 = new Long(-2147483648);
	final static Long LONG2 = new Long(0);
	final static Long LONG3 = new Long(2147483647);
	final static Float FLOAT1 = (float) 1.25E6;
	final static Float FLOAT2 = (float) -5.8E-2;
	final static Double DOUBLE1 = (double) 1.25E6;
	final static Double DOUBLE2 = (double) -5.8E-2;
	final static byte[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	final static Blob BLOB1 = new Blob(b);
	final static String STRING1 = "Hello world";
	// TIME1     Time     = Time(time.Unix(int64(1234567), int64(500)))
	final static Time TIME1 = new Time(1234567000);
	// FINETIME1 FineTime = FineTime(time.Unix(int64(1234567), int64(500)))
	final static FineTime FINETIME1 = new FineTime(1234567000000500L);

	// Encodes with FixedBinary and writes in a file
	@Test
	public void testFixedBInaryEncoding() throws Exception {
		FileOutputStream fos = new FileOutputStream("javafixedbinary.data");
		TimeEncoder timeEncoder = new BinaryTimeEncoder();
		FineTimeEncoder fineTimeEncoder = new BinaryFineTimeEncoder();
		JavaDurationEncoder durationEncoder = new JavaDurationEncoder();
		BinaryEncoder encoder = new BinaryEncoder(fos, false,  timeEncoder, fineTimeEncoder, durationEncoder);
		encoder.setVarintSupported(false);
		testEncoding(encoder);
		encoder.flush();
		fos.close();
	}

	// Reads a file and decodes with FixedBinary
	@Test
	public void testFixedBInaryDecoding() throws Exception {
		FileInputStream fis = new FileInputStream("javafixedbinary.data");
		TimeDecoder timeDecoder = new BinaryTimeDecoder();
		FineTimeDecoder fineTimeDecoder = new BinaryFineTimeDecoder();
		DurationDecoder durationDecoder = new JavaDurationDecoder();
		fr.cnes.encoding.binary.InputStreamDecoder isdecoder = new fr.cnes.encoding.binary.InputStreamDecoder(fis);
		isdecoder.setVarintSupported(false);
		BinaryDecoder decoder = new BinaryDecoder(isdecoder, false, timeDecoder, fineTimeDecoder, durationDecoder);
		testDecoding(decoder);
		decoder.close();
		fis.close();
	}

	// Encodes with VarintBinary and writes in a file
	@Test
	public void testVarintBInaryEncoding() throws Exception {
		FileOutputStream fos = new FileOutputStream("javavarintbinary.data");
		TimeEncoder timeEncoder = new BinaryTimeEncoder();
		FineTimeEncoder fineTimeEncoder = new BinaryFineTimeEncoder();
		JavaDurationEncoder durationEncoder = new JavaDurationEncoder();
		BinaryEncoder encoder = new BinaryEncoder(fos, false,  timeEncoder, fineTimeEncoder, durationEncoder);
		encoder.setVarintSupported(true);
		testEncoding(encoder);
		encoder.flush();
		fos.close();
	}

	// Reads a file and decodes with VarintBinary
	@Test
	public void testVarintBInaryDecoding() throws Exception {
		FileInputStream fis = new FileInputStream("javavarintbinary.data");
		TimeDecoder timeDecoder = new BinaryTimeDecoder();
		FineTimeDecoder fineTimeDecoder = new BinaryFineTimeDecoder();
		DurationDecoder durationDecoder = new JavaDurationDecoder();
		fr.cnes.encoding.binary.InputStreamDecoder isdecoder = new fr.cnes.encoding.binary.InputStreamDecoder(fis);
		isdecoder.setVarintSupported(true);
		BinaryDecoder decoder = new BinaryDecoder(isdecoder, false, timeDecoder, fineTimeDecoder, durationDecoder);
		testDecoding(decoder);
		decoder.close();
		fis.close();
	}

	// Encodes with SplitBinary and writes in a file
	@Test
	public void testSplitBInaryEncoding() throws Exception {
		FileOutputStream fos = new FileOutputStream("javasplitbinary.data");
		TimeEncoder timeEncoder = new BinaryTimeEncoder();
		FineTimeEncoder fineTimeEncoder = new BinaryFineTimeEncoder();
		JavaDurationEncoder durationEncoder = new JavaDurationEncoder();
		SplitBinaryEncoder encoder = new SplitBinaryEncoder(fos, false,  timeEncoder, fineTimeEncoder, durationEncoder);
		testEncoding(encoder);
		encoder.flush();
		encoder.close();
		fos.close();
	}

	// Reads a file and decodes with SplitBinary
	@Test
	public void testSplitBInaryDecoding() throws Exception {
		FileInputStream fis = new FileInputStream("javasplitbinary.data");
		TimeDecoder timeDecoder = new BinaryTimeDecoder();
		FineTimeDecoder fineTimeDecoder = new BinaryFineTimeDecoder();
		DurationDecoder durationDecoder = new JavaDurationDecoder();
		fr.cnes.encoding.splitbinary.InputStreamDecoder isdecoder = new fr.cnes.encoding.splitbinary.InputStreamDecoder(fis);
		isdecoder.setVarintSupported(true);
		SplitBinaryDecoder decoder = new SplitBinaryDecoder(isdecoder, false, timeDecoder, fineTimeDecoder, durationDecoder);
		testDecoding(decoder);
		decoder.close();
		fis.close();
	}

	void testEncoding(MALEncoder encoder) throws MALException {
		encoder.encodeUOctet(UOCTET1);
		encoder.encodeUOctet(UOCTET2);
		encoder.encodeOctet(OCTET1);;
		encoder.encodeOctet(OCTET2);;
		encoder.encodeOctet(OCTET3);
		encoder.encodeUShort(USHORT1);
		encoder.encodeUShort(USHORT2);
		encoder.encodeUShort(USHORT3);
		encoder.encodeShort(SHORT1);
		encoder.encodeShort(SHORT2);
		encoder.encodeShort(SHORT3);
		encoder.encodeShort(SHORT4);
		encoder.encodeShort(SHORT5);
		encoder.encodeUInteger(UINT1);
		encoder.encodeUInteger(UINT2);
		encoder.encodeUInteger(UINT3);
		encoder.encodeUInteger(UINT4);
		encoder.encodeInteger(INT1);
		encoder.encodeInteger(INT2);
		encoder.encodeInteger(INT3);
		encoder.encodeInteger(INT4);
		encoder.encodeInteger(INT5);
		encoder.encodeInteger(INT6);
		encoder.encodeInteger(INT7);
		encoder.encodeULong(ULONG1);
		encoder.encodeULong(ULONG2);
		encoder.encodeULong(ULONG3);
		encoder.encodeLong(LONG1);
		encoder.encodeLong(LONG2);
		encoder.encodeLong(LONG3);
		encoder.encodeFloat(FLOAT1);
		encoder.encodeFloat(FLOAT2);
		encoder.encodeDouble(DOUBLE1);
		encoder.encodeDouble(DOUBLE2);
		encoder.encodeBlob(BLOB1);
		encoder.encodeString(STRING1);
		encoder.encodeTime(TIME1);
		encoder.encodeFineTime(FINETIME1);
	}

	void testDecoding(MALDecoder decoder) throws MALException {
		Assert.assertEquals("UOCTET1", UOCTET1, decoder.decodeUOctet());
		Assert.assertEquals("UOCTET", UOCTET2, decoder.decodeUOctet());
		Assert.assertEquals("OCTET1", OCTET1, decoder.decodeOctet());
		Assert.assertEquals("OCTET2", OCTET2, decoder.decodeOctet());
		Assert.assertEquals("OCTET3", OCTET3, decoder.decodeOctet());
		Assert.assertEquals("USHORT1", USHORT1, decoder.decodeUShort());
		Assert.assertEquals("USHORT2", USHORT2, decoder.decodeUShort());
		Assert.assertEquals("USHORT3", USHORT3, decoder.decodeUShort());
		Assert.assertEquals("SHORT1", SHORT1, decoder.decodeShort());
		Assert.assertEquals("SHORT2", SHORT2, decoder.decodeShort());
		Assert.assertEquals("SHORT3", SHORT3, decoder.decodeShort());
		Assert.assertEquals("SHORT4", SHORT4, decoder.decodeShort());
		Assert.assertEquals("SHORT5", SHORT5, decoder.decodeShort());
		Assert.assertEquals("UINT1", UINT1, decoder.decodeUInteger());
		Assert.assertEquals("UINT2", UINT2, decoder.decodeUInteger());
		Assert.assertEquals("UINT3", UINT3, decoder.decodeUInteger());
		Assert.assertEquals("UINT4", UINT4, decoder.decodeUInteger());
		Assert.assertEquals("INT1", INT1, decoder.decodeInteger());
		Assert.assertEquals("INT2", INT2, decoder.decodeInteger());
		Assert.assertEquals("INT3", INT3, decoder.decodeInteger());
		Assert.assertEquals("INT4", INT4, decoder.decodeInteger());
		Assert.assertEquals("INT5", INT5, decoder.decodeInteger());
		Assert.assertEquals("INT6", INT6, decoder.decodeInteger());
		Assert.assertEquals("INT7", INT7, decoder.decodeInteger());
		Assert.assertEquals("ULONG1", ULONG1, decoder.decodeULong());
		Assert.assertEquals("ULONG2", ULONG2, decoder.decodeULong());
		Assert.assertEquals("ULONG3", ULONG3, decoder.decodeULong());
		Assert.assertEquals("LONG1", LONG1, decoder.decodeLong());
		Assert.assertEquals("LONG2", LONG2, decoder.decodeLong());
		Assert.assertEquals("LONG3", LONG3, decoder.decodeLong());
		Assert.assertEquals("FLOAT1", FLOAT1, decoder.decodeFloat());
		Assert.assertEquals("FLOAT2", FLOAT2, decoder.decodeFloat());
		Assert.assertEquals("DOUBLE1", DOUBLE1, decoder.decodeDouble());
		Assert.assertEquals("DOUBLE2", DOUBLE2, decoder.decodeDouble());
		Assert.assertEquals("BLOB1", BLOB1, decoder.decodeBlob());
		Assert.assertEquals("STRING1", STRING1, decoder.decodeString());
		Assert.assertEquals("TIME1", TIME1, decoder.decodeTime());
		Assert.assertEquals("FINETIME1", FINETIME1, decoder.decodeFineTime());
	}

}
