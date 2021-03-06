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


package erinManuscript;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import parsers.OtuWrapper;
import utils.ConfigReader;

public class PivotWithqPCRDATA
{
	private static HashMap<Integer, Double> getPRCData()
	{
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		
		map.put(1, 23.06);
		map.put(3,24.25);
		map.put(4,28.70);
		map.put(5,28.21);
		map.put(7,15.26);
		map.put(8,16.64);
		map.put(9,26.86);
		map.put(10,15.92);
		map.put(11,25.93);
		map.put(12,22.35);
		map.put(13,15.74);
		map.put(14,16.28);
		map.put(15,26.44);
		map.put(16,18.02);
		
		return map;
	}
	
	private static class Holder
	{
		double counts;
		String identifier;
	}
	
	private static Holder getMax(OtuWrapper wrapper, String sampleID) throws Exception
	{
		int index = wrapper.getIndexForSampleName(sampleID);
		Holder max = null;
		
		for(int x=0; x < wrapper.getOtuNames().size();x++)
		{
			double newVal = wrapper.getDataPointsUnnormalized().get(index).get(x);
			
			if( max == null || newVal > max.counts)
			{
				max = new Holder();
				max.counts = newVal;
				max.identifier = wrapper.getOtuNames().get( Math.max(0,x));
			}
			
		}
		
		max.counts /= wrapper.getCountsForSample(sampleID);
		
		return max;
	}
	
	
	public static void main(String[] args) throws Exception
	{
		OtuWrapper wrapper = new OtuWrapper( ConfigReader.getErinDataDir() + File.separator + 
						"erinHannaHuman_raw_ord.txt");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ConfigReader.getErinDataDir() 
				+ File.separator + 
				"pivotedOutOrd.txt")));
		
		writer.write("sample\tqPCR\tshannonDiveristy\tnumberOfSequences\trichness\tfractionMax\tidentityMax\n");
		
		for(String s : wrapper.getSampleNames() )
		{
			writer.write(s.replaceAll("human","") + "\t");
			int key = Integer.parseInt(s.replaceAll("human", ""));
			writer.write(getPRCData().get(key) + "\t");
			writer.write(wrapper.getShannonEntropy(s) + "\t");
			writer.write(wrapper.getCountsForSample(s) + "\t");
			writer.write(wrapper.getRichness(s) + "\t");
			Holder h = getMax(wrapper, s);
			writer.write( h.counts  + "\t");
			writer.write( h.identifier + "\n");
		}
		
		writer.flush();  writer.close();
	}
	
	
}
