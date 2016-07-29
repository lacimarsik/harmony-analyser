package harmanal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Database schema for containing the analysis facts for Chordanal and Harmanal
 */

/* internal format: Map <KEY(List<String>) <-> VALUE(List<String>)> */
/* handling format: key1,key2,...,keyN;value1,value2,...,valueN */

public class DatabaseTable {
	
	private Map<List<String>,List<String>> table;
	
	DatabaseTable() {
		table = new LinkedHashMap<List<String>,List<String>>();
	}
	
	/**
	 * Adds a row into the table
	 */
	
	void add(String stringValues) {
		String[] strings = stringValues.split(";");
		String[] strings1 = strings[0].split(",");
		String[] strings2 = strings[1].split(",");
		List<String> key = new ArrayList<String>();
		for (int i = 0; i < strings1.length; i++) {
			key.add(strings1[i]);
		}
		List<String> value = new ArrayList<String>();
		for (int i = 0; i < strings2.length; i++) {
			value.add(strings2[i]);
		}
		table.put(key,value);
	}
	
	/**
	 * Adds all rows from the table
	 */
	
	void addAll(DatabaseTable stringTable) {
		List<String> rows = stringTable.getAll();
		for (String row : rows) {
			add(row);
		}
	}
	
	/**
	 * Returns the first match in the value
	 */
	
	String getFirstInValue(String key) {
		String result = "";
		for (List<String> l : table.keySet()) {
			if (l.contains(key)) {
				result = table.get(l).get(0);
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the first match in the key
	 */
	
	String getFirstInKey(String value) {
		String result = "";
		for (List<String> l : table.keySet()) {
			if (table.get(l).contains(value)) {
				result = l.get(0);
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of values
	 */
	
	List<String> getValues(String key) {
		List<String> result = new ArrayList<String>();
		for (List<String> l : table.keySet()) {
			if (l.contains(key)) {
				result = table.get(l);
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of keys
	 */
	
	List<String> getKeys(String value) {
		List<String> result = new ArrayList<String>();
		for (List<String> l : table.keySet()) {
			if (table.get(l).contains(value)) {
				result = l;
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the first match in the value using first 2 keys
	 */
	
	String getFirstInValue(String key1, String key2) {
		String result = "";
		for (List<String> l : table.keySet()) {
			try {
				if ((l.get(0).equals(key1)) && (l.get(1).equals(key2))) {
					result = table.get(l).get(0);
					return result;
				}
			} catch (Exception e) {
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the first match in the value using first 3 keys
	 */
	
	String getFirstInValue(String key1, String key2, String key3) {
		String result = "";
		for (List<String> l : table.keySet()) {
			try {
				if ((l.get(0).equals(key1)) && (l.get(1).equals(key2)) && (l.get(2).equals(key3))) {
					return table.get(l).get(0);
				}
			} catch (Exception e) {
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of values using first 2 keys
	 */
	
	List<String> getValues(String key1, String key2) {
		List<String> result = new ArrayList<String>();
		for (List<String> l : table.keySet()) {
			try {
				if ((l.get(0).equals(key1)) && (l.get(1).equals(key2))) {
					result = table.get(l);
					return result;
				}
			} catch (Exception e) {
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of values using first 3 keys
	 */
	
	List<String> getValues(String key1, String key2, String key3) {
		List<String> result = new ArrayList<String>();
		for (List<String> l : table.keySet()) {
			try {
				if ((l.get(0).equals(key1)) && (l.get(1).equals(key2)) && (l.get(2).equals(key3))) {
					result = table.get(l);
					return result;
				}
			} catch (Exception e) {
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Returns all the key-value pairs in String
	 */

	public List<String> getAll() {
		ArrayList<String> result = new ArrayList<String>();
		for (List<String> l : table.keySet()) {
			String row = "";
			for (int i = 0; i < l.size(); i++) {
				if (i < l.size()-1) {
					row += l.get(i) + ",";
				} else {
					row += l.get(i);
				}
			}
			row += ";";
			for (int i = 0; i < table.get(l).size(); i++) {
				if (i < table.get(l).size()-1) {
					row += table.get(l).get(i) + ",";
				} else {
					row += table.get(l).get(i);
				}
			}
			result.add(row);
		}
		return result;
	}
	
	/**
	 * Returns all keys as lists of Strings
	 */

	List<List<String>> getAllKeys() {
		ArrayList<List<String>> result = new ArrayList<List<String>>();
		for (List<String> l : table.keySet()) {
			result.add(l);
		}
		return result;
	}
	
	/**
	 * Returns all keys as Strings
	 */
	
	List<String> getAllKeyStrings() {
		List<List<String>> keys = getAllKeys();
		List<String> result = new ArrayList<String>();
		for (List<String> listKeys : keys) {
			String key = "";
			for (int i = 0; i < listKeys.size(); i++) {
				if (i < listKeys.size() -1) {
					key += listKeys.get(i) + ",";
				} else {
					key += listKeys.get(i);
				}
			}
			result.add(key);
		}
		return result;
	}
	
	/**
	 * Returns all values as lists of Strings
	 */

	List<List<String>> getAllValues() {
		ArrayList<List<String>> result = new ArrayList<List<String>>();
		for (List<String> l : table.keySet()) {
			result.add(table.get(l));
		}
		return result;
	}
	
	/**
	 * Return all values as Strings
	 */
	
	List<String> getAllValueStrings() {
		List<List<String>> values = getAllValues();
		List<String> result = new ArrayList<String>();
		for (List<String> listValues : values) {
			String value = "";
			for (int i = 0; i < listValues.size(); i++) {
				if (i < listValues.size() -1) {
					value += listValues.get(i) + ",";
				} else {
					value += listValues.get(i);
				}
			}
			result.add(value);
		}
		return result;
	}
	
	/**
	 * Sort by value number 1
	 */
	
	DatabaseTable sortByValueByFirst() {
		List<List<String>> values = getAllValues();
		List<List<String>> keys = getAllKeys();
		List<String> rowKey,rowValue;
		DatabaseTable result = new DatabaseTable();
		
		if (values.size() < 2) {
			return null;
		}
		
		while (values.size() > 0) {
			String s_min = values.get(0).get(0);
			int min_index = 0;
			String s = "";
			for (int i = 1; i < values.size(); i++) {
				s = values.get(i).get(0);
				if (s.compareTo(s_min) < 0) {
					s_min = s;
					min_index = i;
				}
			}
			rowKey = keys.get(min_index);
			String key = "";
			for (int i = 0; i < rowKey.size(); i++) {
				if (i < rowKey.size()-1) {
					key += rowKey.get(i) + ","; 
				} else {
					key += rowKey.get(i);
				}
			}
			rowValue = values.get(min_index);
			String value = "";
			for (int i = 0; i < rowValue.size(); i++) {
				if (i < rowValue.size()-1) {
					value += rowValue.get(i) + ","; 
				} else {
					value += rowValue.get(i);
				}
			}
			result.add(key + ";" + value);
			keys.remove(min_index);
			values.remove(min_index);
		}
		return result;
	}
	
	/**
	 * Sort by value number 1 considered as numeric values
	 */
	
	DatabaseTable sortByValueByFirstNumeric() {
		List<List<String>> values = getAllValues();
		List<List<String>> keys = getAllKeys();
		List<String> rowKey,rowValue;
		DatabaseTable result = new DatabaseTable();
		
		if (values.size() < 2) {
			return this;
		}
		
		while (values.size() > 0) {
			int s_min = Integer.parseInt(values.get(0).get(0));
			int min_index = 0;
			int s = 0;
			for (int i = 1; i < values.size(); i++) {
				s = Integer.parseInt(values.get(i).get(0));
				if (s < s_min) {
					s_min = s;
					min_index = i;
				}
			}
			rowKey = keys.get(min_index);
			String key = "";
			for (int i = 0; i < rowKey.size(); i++) {
				if (i < rowKey.size()-1) {
					key += rowKey.get(i) + ","; 
				} else {
					key += rowKey.get(i);
				}
			}
			rowValue = values.get(min_index);
			String value = "";
			for (int i = 0; i < rowValue.size(); i++) {
				if (i < rowValue.size()-1) {
					value += rowValue.get(i) + ","; 
				} else {
					value += rowValue.get(i);
				}
			}
			result.add(key + ";" + value);
			keys.remove(min_index);
			values.remove(min_index);
		}
		return result;
	}
	
	/**
	 * Natural join of two tables by the whole key, combining the values
	 */
	
	DatabaseTable naturalJoin(DatabaseTable otherTable) {
		List<String> keys1 = getAllKeyStrings();
		List<String> keys2 = otherTable.getAllKeyStrings();
		List<String> values1 = getAllValueStrings();
		List<String> values2 = otherTable.getAllValueStrings();
		DatabaseTable result = new DatabaseTable();
		
		for (int i = 0; i < keys1.size(); i++) {
			for (int j = 0; j < keys2.size(); j++) {
				if (keys1.get(i).equals(keys2.get(j))) {
					result.add(keys1.get(i) + ";" + values1.get(i) + "," + values2.get(j));
				}
			}
		}	
		return result;
	}
	
	/**
	 * Natural join of two tables by first two columns of the key, combining the other values
	 */
	
	DatabaseTable naturalJoinByFirstAndSecond(DatabaseTable otherTable) {
		List<String> keys1 = getAllKeyStrings();
		List<String> keys2 = otherTable.getAllKeyStrings();
		List<String> keys1copy = getAllKeyStrings();
		List<String> keys2copy = otherTable.getAllKeyStrings();
		for (int i = 0; i < keys1copy.size(); i++) {
			String[] temp = keys1copy.get(i).split(",");
			keys1copy.set(i, temp[0] + "," + temp[1]);
		}
		for (int i = 0; i < keys2copy.size(); i++) {
			String[] temp = keys2copy.get(i).split(",");
			keys2copy.set(i, temp[0] + "," + temp[1]);
		}
		List<String> values1 = getAllValueStrings();
		List<String> values2 = otherTable.getAllValueStrings();
		DatabaseTable result = new DatabaseTable();
		
		for (int i = 0; i < keys1copy.size(); i++) {
			for (int j = 0; j < keys2copy.size(); j++) {
				if (keys1copy.get(i).equals(keys2copy.get(j))) {
					String[] temp1 = keys1.get(i).split(",");
					String[] temp2 = keys2.get(j).split(",");
					result.add(keys1copy.get(i) + "," + temp1[2] + "," + temp2[2] + ";" + values1.get(i) + "," + values2.get(j));
				}
			}
		}
		return result;
	}
	
	/**
	 * Natural join of two tables by first column of the key, combining the other values
	 */
	
	DatabaseTable naturalJoinByFirst(DatabaseTable otherTable) {
		List<String> keys1 = getAllKeyStrings();
		List<String> keys2 = otherTable.getAllKeyStrings();
		List<String> keys1copy = getAllKeyStrings();
		List<String> keys2copy = otherTable.getAllKeyStrings();
		for (int i = 0; i < keys1copy.size(); i++) {
			String[] temp = keys1copy.get(i).split(",");
			keys1copy.set(i, temp[0]);
		}
		for (int i = 0; i < keys2copy.size(); i++) {
			String[] temp = keys2copy.get(i).split(",");
			keys2copy.set(i, temp[0]);
		}
		List<String> values1 = getAllValueStrings();
		List<String> values2 = otherTable.getAllValueStrings();
		DatabaseTable result = new DatabaseTable();
		
		for (int i = 0; i < keys1copy.size(); i++) {
			for (int j = 0; j < keys2copy.size(); j++) {
				if (keys1copy.get(i).equals(keys2copy.get(j))) {
					String[] temp1 = keys1.get(i).split(",");
					String[] temp2 = keys2.get(j).split(",");
					result.add(keys1copy.get(i) + "," + temp1[1] + "," + temp2[1] + "," + temp1[2] + "," + temp2[2] + ";" + values1.get(i) + "," + values2.get(j));
				}
			}
		}
		return result;
	}
	
	/**
	 * Checks if the table is empty
	 */
	
	boolean isEmpty() {
		return table.isEmpty();
	}
	
}