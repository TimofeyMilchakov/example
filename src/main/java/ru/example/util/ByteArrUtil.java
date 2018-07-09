/*********************************************************************
 * The Initial Developer of the content of this file is NOVARDIS.
 * All portions of the code written by NOVARDIS are property of
 * NOVARDIS. All Rights Reserved.
 *
 * NOVARDIS
 * Detskaya st. 5A, 199106 
 * Saint Petersburg, Russian Federation 
 * Tel: +7 (812) 331 01 71
 * Fax: +7 (812) 331 01 70
 * www.novardis.com
 *
 * (c) 2018 by NOVARDIS
 *********************************************************************/

package ru.example.util;

import java.math.BigInteger;

import ru.example.packag.Message;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 30.06.2018
 */
public class ByteArrUtil
{

	public static byte[] convertByteArrInBinary(byte[] in,int n)
	{
		byte[] data = parseStringInBinary(toBinary(in));
		byte[] noKnow = new byte[n*8];
		for (int i = 0;i<noKnow.length;i++)
			noKnow[i]=-1;
		data = concatenate(data,noKnow);
		return data;
	}

	public static byte[] convertByteArrInBinary(byte[] in)
	{
		return parseStringInBinary(toBinary(in));
	}

	private static String toBinary(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
		return sb.toString();
	}

	public static byte[] parseStringInBinary(String s)
	{
		byte[] bytes = new byte[s.length()];
		int len = s.length();
		for (int i = 0; i < len; i++)
			bytes[i] = Byte.valueOf(String.valueOf(s.charAt(i)));
		return bytes;
	}

	public static Message initMessage(byte[] dataInBinary)
	{
		int lenMessage = dataInBinary.length;
		int k=0;
		for(int i =0; i<1000;i++){
			if(448==((lenMessage+1+i)%512)){
				k=i;
				break;
			}
		}
		byte[] ofset = new byte[k+1];
		ofset[0]=1;
		byte[] newArr = concatenate(dataInBinary,ofset);
		String len = Integer.toString(lenMessage,2);
		byte[] lenInByte = parseStringInBinary(len);
		int j = 64-lenInByte.length;
		byte[] zero = new byte[j];
		lenInByte = concatenate(zero,lenInByte);
		newArr = concatenate(newArr,lenInByte);
		if(newArr.length%512!=0)
			throw new RuntimeException("Какаято хуйня братан");
		return Message.fabric(newArr);
	}
	public static byte[] concatenate (byte[] a, byte[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		byte[] c = new byte[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static byte[] numberToBitsByteArray(BigInteger number) {
		String tmp = new StringBuffer(number.toString(2)).toString();
		int offset = 32 - tmp.length();
		byte[] result = new byte[32];
		for (int i = 0+offset; i < 32; i++) {
			if (tmp.charAt(i-offset) == '1') {
				result[i] = 1;
			}
		}
		return result;
	}

	public static BigInteger bitsByteArrayToNumber(byte[] bits) {
		BigInteger temp = BigInteger.ZERO;
		for (int i = 0; i <bits.length; i++) {
			if (bits[i] == 1) {
				temp = temp.add(BigInteger.valueOf(2).pow(bits.length-i-1));
			}
		}
		return temp;
	}


}
