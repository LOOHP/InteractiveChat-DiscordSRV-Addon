package com.loohp.interactivechatdiscordsrvaddon.resource.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.loohp.interactivechatdiscordsrvaddon.registies.ResourceRegistry;

public class ModelOverride {
	
	private Map<ModelOverrideType, Object> predicates;
	private String model;
	
	public ModelOverride(Map<ModelOverrideType, Object> predicates, String model) {
		this.predicates = Collections.unmodifiableMap(predicates);
		this.model = model;
	}

	public Map<ModelOverrideType, Object> getPredicates() {
		return predicates;
	}
	
	public String getRawModel() {
		return model;
	}

	public String getModel() {
		return model == null ? null : (model.contains(":") ? model : ResourceRegistry.DEFAULT_NAMESPACE + ":" + model);
	}

	public boolean test(Map<ModelOverrideType, Object> data) {
		boolean result = true;
		Map<ModelOverrideType, Object> dataCopy = new HashMap<>();
		dataCopy.putAll(data);
		for (Entry<ModelOverrideType, Object> entry : predicates.entrySet()) {
			Object value = dataCopy.remove(entry.getKey());
			if (value == null) {
				result = false;
				break;
			}
			Object valueComparing = entry.getValue();
			if (value instanceof Number && valueComparing instanceof Number) {
				if (((Number) value).doubleValue() > ((Number) valueComparing).doubleValue()) {
					result = false;
					break;
				}
			} else {
				if (!value.equals(valueComparing)) {
					result = false;
					break;
				}
			}
		}
		return result && dataCopy.isEmpty();
	}

	public static enum ModelOverrideType {
		
		ANGLE,
		BLOCKING,
		BROKEN,
		CAST,
		COOLDOWN,
		DAMAGE,
		DAMAGED,
		LEFTHANDED,
		PULL,
		PULLING,
		CHARGED,
		FIREWORK,
		THROWING,
		TIME,
		LEVEL,
		CUSTOM_MODEL_DATA;
		
		public static ModelOverrideType fromKey(String key) {
			for (ModelOverrideType type : values()) {
				if (key.toUpperCase().equals(type.toString())) {
					return type;
				}
			}
			return null;
		}
		
	}

}
