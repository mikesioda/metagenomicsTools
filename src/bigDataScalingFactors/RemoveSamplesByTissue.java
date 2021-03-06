package bigDataScalingFactors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;

import parsers.OtuWrapper;
import utils.ConfigReader;

public class RemoveSamplesByTissue
{
	private static HashSet<String> getIncluded() throws Exception
	{
		HashSet<String> set = new HashSet<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				ConfigReader.getBigDataScalingFactorsDir() + File.separator + "June24_risk" 
						+ File.separator + "study_1939_mapping_file.txt")));
		
		reader.readLine();
		
		for(String s= reader.readLine(); s != null; s= reader.readLine())
		{
			String[] splits = s.split("\t");
			System.out.println(splits[23]);
			if(splits[23].equals("stool"))
				set.add(splits[0].trim());
		}
		
		reader.close();
		
		return set;
	}
	
	public static void main(String[] args) throws Exception
	{
		HashSet<String> included = getIncluded();
		System.out.println(included.size());
		System.out.println(included);
		
		String filepath = ConfigReader.getBigDataScalingFactorsDir() + 
				File.separator + "July_StoolRemoved" + File.separator + "risk_raw_countsTaxaAsColumns.txt";
		OtuWrapper wrapper = new OtuWrapper(filepath);
		
		HashSet<String> excludedSamples = new HashSet<String>();
		
		for(String s : wrapper.getSampleNames())
			if( included.contains(s) || wrapper.getCountsForSample(s) < 100)
				excludedSamples.add(s);
		
		System.out.println(excludedSamples.size());
		
		HashSet<String> excludedOTU = new HashSet<String>();
		
		for( int x=0;x < wrapper.getOtuNames().size(); x++)
		{
			System.out.println( x + " " + wrapper.getCountForTaxaExcludingTheseSamples(x, excludedSamples));
			if( wrapper.getCountForTaxaExcludingTheseSamples(x, excludedSamples) <= 0.01 ) 
			{
				excludedOTU.add(wrapper.getOtuNames().get(x));
			}
		}
		
		wrapper = new OtuWrapper(filepath,excludedSamples, excludedOTU);
		
		wrapper.writeRawDataWithTaxaAsColumns(ConfigReader.getBigDataScalingFactorsDir() + 
				File.separator + "July_StoolRemoved" + File.separator +"risk_raw_countsTaxaAsColumnsAllButStool.txt");
		
	}
}	
