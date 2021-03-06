package com.chenjw.knife.agent.utils;

import com.alibaba.fastjson.JSON;
import com.chenjw.knife.agent.core.ServiceRegistry;
import com.chenjw.knife.agent.service.ObjectHolderService;
import com.chenjw.knife.utils.ClassHelper;
import com.chenjw.knife.utils.StringHelper;

public class ParseHelper {

	public static Object parseValue(String expr, Class<?> type) {
		Object obj = null;
		if (expr.startsWith("@")) {
			int num = Integer.parseInt(StringHelper.substringAfter(expr, "@")
					.trim());
			obj = ServiceRegistry.getService(ObjectHolderService.class)
					.get(num);
			if (obj == null) {
				throw new java.lang.IllegalArgumentException("object " + expr
						+ " not found!");
			}
		} else {
			obj = JSON.parseObject(expr, type);

		}
		if (obj != null && !(ClassHelper.getBoxClazz(type).isInstance(obj))) {
			throw new java.lang.IllegalArgumentException("cant parse expr ["
					+ expr + "] to type [" + type.getName() + "]");
		}
		return obj;
	}

	public static Object[] parseMethodArgs(String expr, Class<?>[] types) {
		Object[] objs = new Object[types.length];
		int i = 0;
		for (Class<?> type : types) {
			int end = getFirstArgIndex(expr);
			String s = StringHelper.substring(expr, 0, end);
			Object value = ParseHelper.parseValue(s, type);
			objs[i] = value;
			expr = StringHelper.substring(expr, end + 1);
			i++;
		}
		return objs;
	}

	private static int getFirstArgIndex(String str) {
		int n = 0;
		int i = 0;
		for (char c : str.toCharArray()) {
			if (c == '(') {
				n++;
			} else if (c == ')') {
				n--;
			} else if (c == '[') {
				n++;
			} else if (c == ']') {
				n--;
			} else if (c == '{') {
				n++;
			} else if (c == '}') {
				n--;
			} else if (c == ',') {
				if (n == 0) {
					return i;
				}
			}
			i++;
		}
		return i;
	}

}
