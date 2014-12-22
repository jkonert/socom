package de.tud.kom.socom.web.client.sharedmodels;

import java.util.Map;

/** A simple Map entry as AbstractMap.SimpleEntry is not supported by GWT
 * 
 * @author jkonert
 *
 * @param <K>
 * @param <V>
 */
public class SimpleEntry<K, V> implements Map.Entry<K,V>
{
	private K key;
	private V value;

	public SimpleEntry(K key, V value)
	{
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return getValue();
	}
}
