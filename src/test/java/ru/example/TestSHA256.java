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

package ru.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ru.example.packag.Block;
import ru.example.util.ByteArrUtil;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 07.07.2018
 */
public class TestSHA256
{
	@Test
	public void testBoolAND()
	{
		byte[] data1 = new byte[32];
		byte[] data2 = new byte[32];
		byte[] boolAnd = new byte[32];
		Random r = new Random();
		for (int i = 0; i < 32; i++)
		{
			data1[i] = (byte) (r.nextInt(2));
			data2[i] = (byte) (r.nextInt(2));
			boolAnd[i] = (byte) ((data1[i] + data2[i]) / 2);
		}
		Block res = (new Block(data1)).boolAnd(new Block(data2));
		for (int i = 0; i < 32; i++)
		{
			assertEquals(boolAnd[i], res.getDataInBinary()[i]);
		}
	}

	@Test
	public void testAnd()
	{
		String var1 = "BB67AE85";
		String var2 = "A54FF53A";
		String res = "60B7A3BF";//160B7A3BF
		Block b1 = new Block(ByteArrUtil.numberToBitsByteArray(new BigInteger(var1, 16)));
		Block b2 = new Block(ByteArrUtil.numberToBitsByteArray(new BigInteger(var2, 16)));
		Block testres = new Block(ByteArrUtil.numberToBitsByteArray(new BigInteger(res, 16)));
		Block sum = b1.and(b2);
		String myRes = sum.getIn16();
		assertTrue(res.equalsIgnoreCase(myRes));
	}

	@Test
	public void testXor()
	{
		byte[] data1 = new byte[32];
		byte[] data2 = new byte[32];
		byte[] boolXor = new byte[32];
		Random r = new Random();
		for (int i = 0; i < 32; i++)
		{
			data1[i] = (byte) (r.nextInt(2));
			data2[i] = (byte) (r.nextInt(2));
			if (data1[i] + data2[i] == 1)
				boolXor[i] = 1;
		}
		Block res = (new Block(data1)).xor(new Block(data2));
		for (int i = 0; i < 32; i++)
		{
			assertEquals(boolXor[i], res.getDataInBinary()[i]);
		}
	}

	@Test
	public void testNot()
	{
		byte[] data1 = new byte[32];

		byte[] boolNot = new byte[32];
		Random r = new Random();
		for (int i = 0; i < 32; i++)
		{
			data1[i] = (byte) (r.nextInt(2));
			boolNot[i] = (byte) (1 - data1[i]);
		}
		Block res = (new Block(data1)).not();
		for (int i = 0; i < 32; i++)
		{
			assertEquals(boolNot[i], res.getDataInBinary()[i]);
		}
	}

	@Test
	public void testBoolOr()
	{
		byte[] data1 = new byte[32];
		byte[] data2 = new byte[32];
		byte[] boolOr = new byte[32];
		Random r = new Random();
		for (int i = 0; i < 32; i++)
		{
			data1[i] = (byte) (r.nextInt(2));
			data2[i] = (byte) (r.nextInt(2));
			if (data1[i] + data2[i] > 0)
				boolOr[i] = 1;
		}
		Block res = (new Block(data1)).boolOr(new Block(data2));
		for (int i = 0; i < 32; i++)
		{
			assertEquals(boolOr[i], res.getDataInBinary()[i]);
		}
	}
}
