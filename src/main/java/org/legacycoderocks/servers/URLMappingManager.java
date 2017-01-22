package org.legacycoderocks.servers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.legacycoderocks.commands.Command;

public class URLMappingManager {

	private Map<String, Map<String, String>> mappings = new HashMap<String, Map<String, String>>();

	public void with(String method, String path, String clazz) {
		Map<String, String> methodMap = mappings.get(method);
		if (methodMap == null) {
			methodMap = new HashMap<String, String>();
			mappings.put(method, methodMap);
		}
		methodMap.put(path, clazz);

	}

	public Command getCommand(String method, String path, Map<String, Object> params) {
		Map<String, String> urls = mappings.get(method);
		if (urls != null) {
			String clazz = urls.get(path);
			try {
				Object o = Class.forName(clazz).newInstance();
				BeanUtils.populate(o, params);
				return (Command) o;
			} catch (Exception e) {
				throw new InvalidURLMapping();
			}

		}
		throw new InvalidURLMapping();
	}

}
