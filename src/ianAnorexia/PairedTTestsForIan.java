package ianAnorexia;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import parsers.OtuWrapper;
import utils.ConfigReader;

public class PairedTTestsForIan
{
	public static void main(String[] args) throws Exception
	{
		OtuWrapper wrapper = new OtuWrapper(ConfigReader.getIanAnorexiaDir() + File.separator + "anSamplesTaxaAsColumns.txt");
		
		 HashMap<Integer, Holder> map = getHolders(wrapper);
		
		for(Integer key : map.keySet())
			System.out.println(key + " " + map.get(key).ad + " " + map.get(key).dis);
	}
	
	private static class Holder
	{
		List<Double> ad = null;
		List<Double> dis = null;
	}
	
	private static Holder getOrCreate(int id, HashMap<Integer, Holder> map) throws Exception
	{
		Holder h = map.get(id);
		
		if( h != null)
			return h;
		
		h = new Holder();
		map.put(id, h);
		return h;
	}
	
	private static HashMap<Integer, Holder> getHolders( OtuWrapper wrapper ) throws Exception
	{
		HashMap<Integer, Holder> map = new HashMap<Integer, Holder>();
		
		for(String s : wrapper.getSampleNames())
		{
			
			if( s.endsWith("ad") || s.endsWith("dis"))
			{
				Integer key = Integer.parseInt(s.replaceAll("ad", "").replace("dis", ""));
				Holder h= getOrCreate(key, map);
				
				if( s.endsWith("ad"))
				{
					h.ad = new ArrayList<Double>();
				}
				else if ( s.endsWith("dis"))
				{
					h.dis = new ArrayList<Double>();
				}
				else throw new Exception("Logic error");
			}
		}
		
		return map;
	}
}
