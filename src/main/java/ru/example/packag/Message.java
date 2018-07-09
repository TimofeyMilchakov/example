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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author timofei.milchakov@novardis.com
 * Created on 30.06.2018
 */
@Setter
@Getter
public class Message
{
	private Message(Block[] blocks)
	{
		this.blocks = blocks;
	}

	private Block[] blocks;

	public static Message fabric(byte[] bytes){
		if(bytes.length%512!=0)
			throw new RuntimeException("Error");
		int size = bytes.length;
		List<Block> blockList = new ArrayList<>();
		for(int i =0;i<size;i=i+32){
			byte[] bytes1 = new byte[32];
			System.arraycopy(bytes,i,bytes1,0,32);
			Block block = new Block(bytes1,i/32);
			blockList.add(block);
		}
		Block[] blocks = new Block[blockList.size()];
		return new Message(blockList.toArray(blocks));
	}
}
