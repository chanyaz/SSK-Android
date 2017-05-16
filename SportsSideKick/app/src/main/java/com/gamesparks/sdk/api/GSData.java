package com.gamesparks.sdk.api;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSData {

	protected Map<String,Object> data = new HashMap<String, Object>();
	
	public GSData() {
		
	}
	
	public GSData(Map<String,Object> data) {
		this.data = data;
	}
	
	public GSData(GSData data) {
		this.data = data.data;
	}
	
	public Map<String, Object> getBaseData() {
		return data;
	}
	
	public String getString(String name){
		if(data.containsKey(name) && data.get(name) instanceof String){
			return data.get(name).toString();
		}
		return null;
	}

	public Integer getInteger(String name){
		if(data.containsKey(name) && data.get(name) instanceof Number){
			return((Number)data.get(name)).intValue();
		}
		return null;
	}
	
	public Long getLong(String name){
		if(data.containsKey(name) && data.get(name) instanceof Number){
			return((Number)data.get(name)).longValue();
		}
		return null;
	}
	
	public Double getDouble(String name){
		if(data.containsKey(name) && data.get(name) instanceof Number){
			return((Number)data.get(name)).doubleValue();
		}
		return null;
	}
	
	public Float getFloat(String name){
		if(data.containsKey(name) && data.get(name) instanceof Number){
			return((Number)data.get(name)).floatValue();
		}
		return null;
	}
	
	public Number getNumber(String name){
		if(data.containsKey(name) && data.get(name) instanceof Number){
			return((Number)data.get(name));
		}
		return null;
	}

	public Boolean getBoolean(String name){
		if(data.containsKey(name) && data.get(name) instanceof Boolean){
			return (Boolean)data.get(name);
		}
		return null;
	}

	public GSData getObject(String name){
		if(data.containsKey(name)){
			Object o = data.get(name);
			if (o instanceof Map) {
				return new GSData ((Map<String,Object>)o);
			}

		}
		return null;
	}

	public Date getDate(String name){
		if(data.containsKey(name) && data.get(name) instanceof String){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	        format.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
	        try {
	            return format.parse(data.get(name).toString());
	        } catch (ParseException e) {
	            throw new RuntimeException(e);
	        }

		}
		return null;
	}

	public List<String> getStringList(String name){
		return getFiteredList(name, String.class);
	}

	public List<Number> getNumberList(String name){
		return getFiteredList(name, Number.class);
	}
	
	public List<Boolean> getBooleanList(String name){
		return getFiteredList(name, Boolean.class);
	}

	public List<GSData> getObjectList(String name){
		List values = getList(name);
		if(values != null){
			List<GSData> ret = new ArrayList<GSData> ();
			for(Object value : values){
				if(value instanceof Map){
					ret.add(new GSData((Map)value));
				}
			}
			return ret;
		}
		return null;
	}
	
	private <T> List<T> getFiteredList(String name, Class<T> castType){
		List values = getList(name);
		if(values != null){
			List<T> ret = new ArrayList<T> ();
			for(Object value : values){
				if(castType.isAssignableFrom(value.getClass())){
					ret.add((T)value);
				}
			}
			return ret;
		}
		return null;
	}
	
	public <T extends GSData> List<T> getWrapperObjectList(String name, Class<T> castType){
		List values = getList(name);
		if(values != null){
			List<T> ret = new ArrayList<T> ();
			for(Object value : values){
				if(value instanceof Map){
					try {
						T item = castType.newInstance();
						item.data = (Map)value;
						ret.add(item);
					} catch (Exception e){
						
					}
				}
			}
			return ret;
		}
		return null;
	}
	
	private List getList(String name){
		if(data.containsKey(name)){
			if (data.get(name) instanceof List) {
				return (List)data.get(name);
			}
		}
		return null;
	}

	public Object getAttribute(String attributeName) {
		return data.get(attributeName);
	}
	
}
