/** 
 * Author:  anthony.fodor@gmail.com    
 * This code is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version,
* provided that any use properly credits the author.
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details at http://www.gnu.org * * */


package reduceOTU;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import utils.ConfigReader;

public class Cluster implements Comparable<Cluster>
{
	// todo: This should be a usable adjustable parameter
	// in current implementation can't be set above 32
	// (since we use a long as the key and 32 nucleotides can be encoded in the 64
	// bits of the long
	public static final int WORD_SIZE = 32;
	
	private String consensusSequence;
	
	private List<EditRepresentation> cigarList = new ArrayList<EditRepresentation>();
	
	private HashMap<Long, Integer> hashes = new HashMap<Long, Integer>();
	
	public int getTotalNum()
	{
		int sum=0;
		
		for( EditRepresentation cr : cigarList)
			sum += cr.getNumCopies();
		
		return sum;
	}
	
	/*
	 * As a side-effect re-hashes.  Not even remotely thread safe.
	 */
	public void setConsensusSequence(String s) throws Exception
	{
		hashes = new HashMap<Long, Integer>();
		HashHolder hh = new HashHolder(WORD_SIZE);
		hh.setToString(s);
		
		hashes.put(hh.getBits(), hh.getStringIndex());
		
		while( hh.advance() )
			hashes.put(hh.getBits(), hh.getStringIndex());
	}
	
	@Override
	public int compareTo(Cluster o)
	{
		return o.getTotalNum() - this.getTotalNum();
	}
	
	public static List<Cluster> getInitialListFromDereplicatedFile(File file) throws Exception
	{
		List<Cluster> list = new ArrayList<Cluster>();
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		reader.readLine();
		
		for(String s= reader.readLine(); s != null; s = reader.readLine())
		{
			Cluster c= new Cluster();
			StringTokenizer sToken = new StringTokenizer(s);
			c.consensusSequence = new String( sToken.nextToken());
			int numCopies = Integer.parseInt(sToken.nextToken());
			
			if( sToken.hasMoreTokens())
				throw new Exception("Unexpected line "  + s);
			
			EditRepresentation cr = new EditRepresentation( numCopies);
			c.cigarList.add(cr);
			
			list.add(c);
		}
		
		Collections.sort(list);
		return list;
	}
	
	@Override
	public String toString()
	{
		StringBuffer buff = new StringBuffer();
		buff.append(consensusSequence + "\n");
		
		for(EditRepresentation cr : cigarList)
			buff.append("\t" + cr.toString() + "\n");
		
		return buff.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		List<Cluster> list = getInitialListFromDereplicatedFile(new File(
				ConfigReader.getReducedOTUDir() + File.separator + "derepped.txt"));
		
		for(int x=0; x < 100; x++)
			System.out.println(list.get(x).toString());
	}
} 
