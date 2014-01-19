package bottomUpTree.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import parsers.JsonObject;

public class TranformRnaSeqJSON
{
	public static void main(String[] args) throws Exception
	{
		JsonObject root= 
			 JsonObject.parseJsonFileWithChildren(
						"C:\\Documents and Settings\\Anthony\\git\\metagenomicsTools\\src\\bottomUpTree\\figures\\testOperon.json");
		
		transformNodeAndChildren(root);
		addContigLayer(root);
	}
	
	private static void addContigLayer(JsonObject root) throws Exception
	{
		List<JsonObject> list = new ArrayList<JsonObject>();
		addNodeAndChildren(root, list);
		
		HashMap<Integer, List<JsonObject>> outerMap = new HashMap<Integer, List<JsonObject>>();
		
		for(JsonObject json : list) 
		{
			int genomicLevel = Integer.parseInt(json.getNameValuePairMap().get("contig"));
			
			if(genomicLevel == 0)
			{
				if( ! json.getNameValuePairMap().get("name").equals("root") )
				{
					throw new Exception("Logic error");
				}
				else
				{
					List<JsonObject> aList = outerMap.get(genomicLevel);
					
					if( aList == null)
					{
						aList = new ArrayList<JsonObject>();
						outerMap.put(genomicLevel, aList);
					}
					
					aList.add(json);
				}
			}
		}
		
		List<Integer> outerKeys = new ArrayList<Integer>(outerMap.keySet());
		Collections.sort(outerKeys);
		
		root.getChildren().clear();
		
		for( Integer i : outerKeys )
		{
			List<JsonObject> innerList = outerMap.get(i);
			Collections.sort(innerList, new SortByPosition());
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.makeNewEmptyChildrenList();
			jsonObject.getChildren().addAll(innerList);
			jsonObject.getNameValuePairMap().put("level", "contig");
			jsonObject.getNameValuePairMap().put("name", "contig" + i);
			jsonObject.getNameValuePairMap().put("log_pValue.il02.ilaom02", "0");
			jsonObject.getNameValuePairMap().put("log_pValue.il12.ilaom12", "0");
			jsonObject.getNameValuePairMap().put("fc.il02.ilaom02", "0");
			jsonObject.getNameValuePairMap().put("fc.il12.ilaom12", "0");
			jsonObject.getNameValuePairMap().put("genomic.location",  i + ".0");
			jsonObject.getNameValuePairMap().put("contig",  i + ".0");
			jsonObject.getNameValuePairMap().put("position",  "0");
			root.getChildren().add(jsonObject);
		}
			
	}
	
	private static void addNodeAndChildren(JsonObject json, List<JsonObject> list)
	{
		list.add(json);
		
		if( json.getChildren() != null)
			for(JsonObject child : json.getChildren())
				addNodeAndChildren(child, list);
	}
	
	private static class SortByPosition implements Comparator<JsonObject>
	{
		@Override
		public int compare(JsonObject o1, JsonObject o2)
		{
			return Integer.parseInt(o1.getNameValuePairMap().get("position")) -
					Integer.parseInt(o2.getNameValuePairMap().get("position"));
		}
	}
	
	private static void transformNodeAndChildren( JsonObject json )
		throws Exception
	{
		//System.out.println(json.getNameValuePairMap());
		double aPValue = Double.parseDouble(json.getNameValuePairMap().get("pValue.il02.ilaom02"));
		json.getNameValuePairMap().remove("pValue.il02.ilaom02");
		json.getNameValuePairMap().put("log_pValue.il02.ilaom02","" + -Math.log10(aPValue));
		
		aPValue = Double.parseDouble(json.getNameValuePairMap().get("pValue.il12.ilaom12"));
		json.getNameValuePairMap().remove("pValue.il12.ilaom12");
		json.getNameValuePairMap().put("log_pValue.il12.ilaom12","" + -Math.log10(aPValue));
		
		String location = json.getNameValuePairMap().get("genomic.location");
		StringTokenizer sToken = new StringTokenizer(location, ".");
		
		if( sToken.countTokens() != 2)
			throw new Exception("Parsing error");
		
		json.getNameValuePairMap().put("contig","" + sToken.nextToken() );
		json.getNameValuePairMap().put("position","" + sToken.nextToken() );
		
		if( json.getChildren() != null)
			for( JsonObject child : json.getChildren())
				transformNodeAndChildren(child);
	}
	
	
}
