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

package ru.example.packag;

import lombok.Getter;
import lombok.Setter;
import ru.example.util.BoolOptim;
import ru.example.util.ByteArrUtil;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 30.06.2018
 */
@Setter
@Getter
public class Block
{
	private boolean haveX;
	private String[] dataInString;
	private byte[] dataInBinary;

	public Block(byte[] bytes)
	{
		if (bytes.length != 32)
			throw new RuntimeException();
		this.dataInBinary = bytes;
		this.dataInString = new String[32];
		haveX = false;
	}

	public Block(byte[] bytes, String[] strings)
	{
		this(bytes);
		dataInString = strings;
		haveX = true;
	}

	public Block(byte[] bytes, int num)
	{
		this(bytes);
		for (int i = 0; i < 32; i++)
		{
			if (bytes[i] == -1)
			{
				haveX = true;
				dataInString[i] = "b[" + num + ":" + i + "]";
			}
		}
	}

	public Block boolAnd(Block block)
	{
		byte[] thisData = this.dataInBinary;
		byte[] input = block.dataInBinary;

		if (thisData.length != input.length)
			throw new RuntimeException();
		int len = input.length;
		check(len);
		byte[] out = new byte[len];
		Block block1;
		if (block.haveX || this.haveX)
		{
			String[] strings = new String[32];
			for (int i = 0; i < len; i++)
			{
				if (thisData[i] == -1 && input[i] == -1)
				{
					strings[i] = "(" + this.dataInString[i] + ")&(" + block.dataInString[i] + ")";
					out[i] = -1;
					continue;
				}
				if (thisData[i] != -1 && input[i] != -1)
				{
					out[i] = (byte) (thisData[i] & input[i]);
					continue;
				}
				if (thisData[i] == -1 && input[i] != -1)
				{
					if (input[i] == 0)
					{
						out[i] = 0;
						continue;
					}
					out[i] = -1;
					strings[i] = this.dataInString[i];
				}
				if (thisData[i] != -1 && input[i] == -1)
				{
					if (thisData[i] == 0)
					{
						out[i] = 0;
						continue;
					}
					out[i] = -1;
					strings[i] = block.dataInString[i];
				}
			}
			block1 = new Block(out, strings);
		}
		else
		{
			for (int i = 0; i < len; i++)
			{
				out[i] = (byte) (thisData[i] & input[i]);
			}
			block1 = new Block(out);
		}
		return block1;
	}

	public Block boolOr(Block block)
	{
		byte[] thisData = this.dataInBinary;
		byte[] input = block.dataInBinary;

		if (thisData.length != input.length)
			throw new RuntimeException();
		int len = input.length;
		check(len);
		byte[] out = new byte[len];
		Block block1;
		if (this.haveX || block.haveX)
		{
			String[] strings = new String[32];
			for (int i = 0; i < len; i++)
			{
				if (thisData[i] == -1 && input[i] == -1)
				{
					strings[i] = "(" + this.dataInString[i] + ")|(" + block.dataInString[i] + ")";
					out[i] = -1;
					continue;
				}
				if (thisData[i] != -1 && input[i] != -1)
				{
					out[i] = (byte) (thisData[i] | input[i]);
					continue;
				}
				if (thisData[i] == -1 && input[i] != -1)
				{
					if (input[i] == 1)
					{
						out[i] = 1;
						continue;
					}
					out[i] = -1;
					strings[i] = this.dataInString[i];
				}
				if (thisData[i] != -1 && input[i] == -1)
				{
					if (thisData[i] == 1)
					{
						out[i] = 1;
						continue;
					}
					out[i] = -1;
					strings[i] = block.dataInString[i];
				}
			}
			block1 = new Block(out, strings);
		}
		else
		{
			for (int i = 0; i < len; i++)
			{
				out[i] = (byte) (thisData[i] | input[i]);
			}
			block1 = new Block(out);
		}
		return block1;
	}

	//todo
	public Block and(Block block)
	{
		byte[] thisData = this.dataInBinary;
		byte[] input = block.dataInBinary;

		if (thisData.length != input.length)
			throw new RuntimeException();
		int len = input.length;
		check(len);
		byte[] out = new byte[len];
		byte add = 0;
		Block block1;
		if (this.haveX || block.haveX)
		{
			String[] strings = new String[32];
			String addS = "";
			for (int i = len - 1; i >= 0; i--)
			{
				//todo
				if (thisData[i] == -1 && input[i] == -1)
				{
					String temp;
					if (addS.equals(""))
					{
						if (add == 0)
							temp = "(" + this.dataInString[i] + "+" + block.dataInString[i] + ")";
						else
						{
							temp = "(" + this.dataInString[i] + "+" + block.dataInString[i] + "+" + add + ")";
							add = 0;
						}
					}
					else
					{
						temp = "(" + this.dataInString[i] + "+" + block.dataInString[i] + "+" + addS + ")";
						addS = "";
					}
					strings[i] = temp + "%2";
					out[i] = -1;
					addS = temp + "/2";
					continue;
				}

				if (thisData[i] != -1 && input[i] != -1)
				{
					if (thisData[i] == 1 && input[i] == 1)
					{
						if (addS.equals(""))
						{
							out[i] = add;
							add = 1;
							continue;
						}
						if (add != 0)
							throw new RuntimeException();

						out[i] = -1;
						strings[i] = addS;
						add = 1;
						addS = "";
						continue;
					}

					if (thisData[i] == 0 && input[i] == 0)
					{
						if (addS.equals(""))
						{
							out[i] = add;
							add = 0;
							continue;
						}

						if (add != 0)
							throw new RuntimeException();

						out[i] = -1;
						strings[i] = addS;
						addS = "";
						continue;
					}
					{
						if (addS.equals(""))
						{
							out[i] = (byte) ((add + 1) % 2);
							add = (byte) ((add + 1) / 2);
							continue;
						}
						if (add != 0)
							throw new RuntimeException();

						out[i] = -1;
						strings[i] = "(" + addS + "+1)%2";
						addS = "(" + addS + "+1)/2";
						continue;
					}
				}
				if (thisData[i] != -1 && input[i] == -1)
				{
					if (addS.equals(""))
					{
						if (thisData[i] + add == 2)
						{
							add = 1;
							strings[i] = block.dataInString[i];
							out[i] = -1;
							continue;
						}
						if (thisData[i] + add == 0)
						{
							add = 0;
							strings[i] = block.dataInString[i];
							out[i] = -1;
							continue;
						}
						add = 0;
						addS = "(" + block.dataInString[i] + "+1)/2";
						strings[i] = "(" + block.dataInString[i] + "+1)%2";
						out[i] = -1;
						continue;
					}

					if (add != 0)
						throw new RuntimeException();

					String temp;
					if (thisData[i] == 1)
						temp = "(" + block.dataInString[i] + "+" + thisData[i] + "+(" + addS + "))";
					else
						temp = "(" + block.dataInString[i] + "+(" + addS + "))";
					addS = temp + "/2";
					strings[i] = temp + "%2";
					out[i] = -1;
					continue;
				}

				if (thisData[i] == -1 && input[i] != -1)
				{
					if (addS.equals(""))
					{
						if (input[i] + add == 2)
						{
							add = 1;
							strings[i] = this.dataInString[i];
							out[i] = -1;
							continue;
						}
						if (input[i] + add == 0)
						{
							add = 0;
							strings[i] = this.dataInString[i];
							out[i] = -1;
							continue;
						}
						add = 0;
						addS = "(" + this.dataInString[i] + "+1)/2";
						strings[i] = "(" + this.dataInString[i] + "+1)%2";
						out[i] = -1;
						continue;
					}

					if (add != 0)
						throw new RuntimeException();

					String temp;
					if (input[i] == 1)
						temp = "(" + this.dataInString[i] + "+" + input[i] + "+(" + addS + "))";
					else
						temp = "(" + this.dataInString[i] + "+(" + addS + "))";
					addS = temp + "/2";
					strings[i] = temp + "%2";
					out[i] = -1;
					continue;
				}
			}
			block1 = new Block(out, BoolOptim.optimiz(strings));
		}
		else
		{
			for (int i = len - 1; i >= 0; i--)
			{
				byte temp = (byte) (thisData[i] + input[i] + add);
				out[i] = (byte) (temp % 2);
				add = (byte) (temp / 2);
			}
			block1 = new Block(out);
		}
		return block1;
	}

	public Block shr(int size)
	{
		byte[] newData = new byte[this.dataInBinary.length];
		int len = this.dataInBinary.length;
		check(len);
		Block block1;
		if (this.haveX)
		{
			String[] strings = new String[32];
			for (int i = 0; i < len - size; i++)
			{
				newData[i + size] = dataInBinary[i];
				strings[i + size] = this.dataInString[i];
			}
			block1 = new Block(newData, BoolOptim.optimiz(strings));
		}
		else
		{
			for (int i = 0; i < len - size; i++)
			{
				newData[i + size] = dataInBinary[i];
			}
			block1 = new Block(newData);
		}
		return block1;
	}

	public Block rotr(int size)
	{
		byte[] newData = new byte[this.dataInBinary.length];
		int len = this.dataInBinary.length;
		check(len);
		Block block1;
		if (this.haveX)
		{
			String[] strings = new String[32];
			for (int i = 0; i < len; i++)
			{
				int j = (i + size) % len;
				newData[j] = dataInBinary[i];
				strings[j] = this.dataInString[i];
			}
			block1 = new Block(newData, BoolOptim.optimiz(strings));
		}
		else
		{
			for (int i = 0; i < len; i++)
			{
				int j = (i + size) % len;
				newData[j] = dataInBinary[i];
			}
			block1 = new Block(newData);
		}
		return block1;
	}

	private void check(int size)
	{
		if (size != 32)
			throw new RuntimeException("неверная длинна");
	}

	public Block xor(Block block)
	{
		int size = block.dataInBinary.length;
		byte[] newData = new byte[size];
		Block block1;
		if (this.haveX || block.haveX)
		{
			String[] strings = new String[32];
			for (int i = 0; i < size; i++)
			{
				if (this.dataInBinary[i] == -1 && block.dataInBinary[i] == -1)
				{
					newData[i] = -1;
					strings[i] = "(" + this.dataInString[i] + ")^(" + block.dataInString[i] + ")";
					continue;
				}
				if (this.dataInBinary[i] != -1 && block.dataInBinary[i] != -1)
				{
					newData[i] = (byte) (block.dataInBinary[i] ^ this.dataInBinary[i]);
					continue;
				}
				if (this.dataInBinary[i] != -1 && block.dataInBinary[i] == -1)
				{
					if (this.dataInBinary[i] == 0)
					{
						newData[i] = -1;
						strings[i] = block.dataInString[i];
						continue;
					}
					newData[i] = -1;
					strings[i] = "!(" + block.dataInString[i] + ")";
					continue;
				}
				if (this.dataInBinary[i] == -1 && block.dataInBinary[i] != -1)
				{
					if (block.dataInBinary[i] == 0)
					{
						newData[i] = -1;
						strings[i] = this.dataInString[i];
						continue;
					}
					newData[i] = -1;
					strings[i] = "!(" + this.dataInString[i] + ")";
					continue;
				}

			}
			block1 = new Block(newData, BoolOptim.optimiz(strings));
		}
		else
		{
			for (int i = 0; i < size; i++)
			{
				newData[i] = (byte) (block.dataInBinary[i] ^ this.dataInBinary[i]);
			}
			block1 = new Block(newData);
		}
		return block1;
	}

	public Block copyBlock()
	{
		String[] strings = new String[32];
		byte[] newData = new byte[32];
		for (int i = 0; i < 32; i++)
		{
			newData[i] = dataInBinary[i];
			if (this.haveX)
				strings[i] = this.dataInString[i];
		}
		if (this.haveX)
			return new Block(newData, BoolOptim.optimiz(strings));
		return new Block(newData);
	}

	public Block not()
	{
		byte[] newData = new byte[32];
		String[] strings = new String[32];
		for (int i = 0; i < 32; i++)
		{
			if (this.haveX)
				strings[i] = this.dataInString[i];
			newData[i] = (byte) (1 - dataInBinary[i]);
		}
		if (this.haveX)
			return new Block(newData, BoolOptim.optimiz(strings));
		return new Block(newData);
	}

	public String getIn16()
	{
		String data = ByteArrUtil.bitsByteArrayToNumber(dataInBinary).toString(16);
		while (data.length() < 8)
		{
			data = "0" + data;
		}
		return data;
	}
}
