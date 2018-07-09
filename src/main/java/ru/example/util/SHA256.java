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

import ru.example.packag.Block;
import ru.example.packag.Message;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 30.06.2018
 */
public class SHA256
{
	private final static String k[] = {
					"428A2F98", "71374491", "B5C0FBCF", "E9B5DBA5", "3956C25B", "59F111F1", "923F82A4", "AB1C5ED5",
					"D807AA98", "12835B01", "243185BE", "550C7DC3", "72BE5D74", "80DEB1FE", "9BDC06A7", "C19BF174",
					"E49B69C1", "EFBE4786", "0FC19DC6", "240CA1CC", "2DE92C6F", "4A7484AA", "5CB0A9DC", "76F988DA",
					"983E5152", "A831C66D", "B00327C8", "BF597FC7", "C6E00BF3", "D5A79147", "06CA6351", "14292967",
					"27B70A85", "2E1B2138", "4D2C6DFC", "53380D13", "650A7354", "766A0ABB", "81C2C92E", "92722C85",
					"A2BFE8A1", "A81A664B", "C24B8B70", "C76C51A3", "D192E819", "D6990624", "F40E3585", "106AA070",
					"19A4C116", "1E376C08", "2748774C", "34B0BCB5", "391C0CB3", "4ED8AA4A", "5B9CCA4F", "682E6FF3",
					"748F82EE", "78A5636F", "84C87814", "8CC70208", "90BEFFFA", "A4506CEB", "BEF9A3F7", "C67178F2"
	};

	private final static String[] h = {
					"6A09E667",
					"BB67AE85",
					"3C6EF372",
					"A54FF53A",
					"510E527F",
					"9B05688C",
					"1F83D9AB",
					"5BE0CD19"
	};

	private Block[] blockH;
	private Block[] blockK;

	public SHA256()
	{
		this.blockH = initBlocks(h);
		this.blockK = initBlocks(k);
	}


	private Block[] initBlocks(String[] arr)
	{
		Block[] blocks = new Block[arr.length];
		int size = arr.length;
		for (int i = 0; i < size; i++)
		{
			byte[] temp = ByteArrUtil.numberToBitsByteArray(new BigInteger(arr[i], 16));
			blocks[i] = new Block(checkSizeArrAndGetting(temp));
		}
		return blocks;
	}

	private byte[] checkSizeArrAndGetting(byte[] arr)
	{
		if (arr.length == 32)
			return arr;
		if (arr.length > 32)
			throw new RuntimeException();
		int getSize = 32 - arr.length;
		byte[] q = new byte[getSize];
		return ByteArrUtil.concatenate(q, arr);
	}

	public String startHash(Message message)
	{
		Block[] data = message.getBlocks();
		int size = data.length;
		if (size % 16 != 0)
			throw new RuntimeException();
		for (int i = 0; i < size; i = i + 16)
		{
			Block[] copy = new Block[16];
			System.arraycopy(data, i, copy, 0, 16);
			round(copy);
		}
		String a = blockH[0].getIn16();
		String b = blockH[1].getIn16();
		String c = blockH[2].getIn16();
		String d = blockH[3].getIn16();
		String e = blockH[4].getIn16();
		String f = blockH[5].getIn16();
		String g = blockH[6].getIn16();
		String h = blockH[7].getIn16();
		String hash = a + b + c + d + e + f + g + h;
		return hash;

	}

	private void round(Block[] blocks)
	{
		Block[] w = new Block[64];
		for (int i = 0; i < 16; i++)
			w[i] = blocks[i];
		for (int i = 16; i < 64; i++)
		{
			Block s0 = (w[i - 15].rotr(7)).xor(w[i - 15].rotr(18)).xor(w[i - 15].shr(3));
			Block s1 = (w[i - 2].rotr(17)).xor(w[i - 2].rotr(19)).xor(w[i - 2].shr(10));
			w[i] = w[i - 16].and(s0).and(w[i - 7]).and(s1);
		}
		Block a = blockH[0].copyBlock();
		Block b = blockH[1].copyBlock();
		Block c = blockH[2].copyBlock();
		Block d = blockH[3].copyBlock();
		Block e = blockH[4].copyBlock();
		Block f = blockH[5].copyBlock();
		Block g = blockH[6].copyBlock();
		Block h = blockH[7].copyBlock();

		/* Σ0 := (a rotr 2) xor (a rotr 13) xor (a rotr 22)
        Ma := (a and b) xor (a and c) xor (b and c)
        t2 := Σ0 + Ma
        Σ1 := (e rotr 6) xor (e rotr 11) xor (e rotr 25)
        Ch := (e and f) xor ((not e) and g)
        t1 := h + Σ1 + Ch + k[i] + w[i]

         h := g
        g := f
        f := e
        e := d + t1
        d := c
        c := b
        b := a
        a := t1 + t2
        */

		for (int i = 0; i < 64; i++)
		{
			Block sum0 = (a.rotr(2)).xor(a.rotr(13)).xor(a.rotr(22));
			Block Ma = (a.boolAnd(b)).xor(a.boolAnd(c)).xor(b.boolAnd(c));
			Block t2 = sum0.and(Ma);
			Block sum1 = (e.rotr(6)).xor(e.rotr(11)).xor(e.rotr(25));
			Block ch = (e.boolAnd(f)).xor((e.not()).boolAnd(g));
			Block t1 = h.and(sum1).and(ch).and(blockK[i]).and(w[i]);
			h = g;
			g = f;
			f = e;
			e = d.and(t1);
			d = c;
			c = b;
			b = a;
			a = t1.and(t2);
		}
		blockH[0] = blockH[0].and(a);
		blockH[1] = blockH[1].and(b);
		blockH[2] = blockH[2].and(c);
		blockH[3] = blockH[3].and(d);
		blockH[4] = blockH[4].and(e);
		blockH[5] = blockH[5].and(f);
		blockH[6] = blockH[6].and(g);
		blockH[7] = blockH[7].and(h);

	}


}
