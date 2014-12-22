package de.tud.kom.socom.util.attributemapping;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;

public class ListAttribute implements Attribute{
	
	private List<Attribute> values;
	
	public ListAttribute(){
		this.values = new LinkedList<Attribute>();
	}

	public ListAttribute(List<Attribute> values) {
		this.values = values;
	}

	public void addAttribute(Attribute value){
		this.values.add(value);
	}
	
	public void addAllAttributes(ListAttribute vals){
		this.values.addAll(vals.getValues());
	}
	
	public List<Attribute> getValues() {
		return values;
	}

	@Override
	public String toJSONString() {
		return new JSONArray(values).toString();
	}
}
