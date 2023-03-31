package cn.aezo.chat_gpt.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class MiscU {


	public static <K, V> Map<K, V> fieldValueMap(Iterable<V> iterable, String fieldName) {
		Map<K, V> retMap = new HashMap<>();
		boolean yesMap = false;
		Iterator<V> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			V next = iterator.next();
			if(next instanceof Map) {
				yesMap = true;
				retMap.put((K) ((Map) next).get(fieldName), next);
			}
		}
		if(yesMap) {
			return retMap;
		} else {
			return  CollUtil.fieldValueMap(iterable, fieldName);
		}
	}

	public static <V> Map<String, V> fieldValueMapSimple(Iterable<V> iterable, String fieldName) {
		Map<String, V> retMap = new HashMap<>();
		boolean yesMap = false;
		Iterator<V> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			V next = iterator.next();
			if(next instanceof Map) {
				yesMap = true;
				Object key = ((Map) next).get(fieldName);
				retMap.put(key == null ? "" : key.toString(), next);
			}
		}
		if(yesMap) {
			return retMap;
		} else {
			Map<Object, V> objectVMap = CollUtil.fieldValueMap(iterable, fieldName);
			for (Map.Entry<Object, V> entry : objectVMap.entrySet()) {
				retMap.put(entry.getKey().toString(), entry.getValue());
			}
			return retMap;
		}
	}

	public static void fillDateStr(Map<String, Object> map, String... keys) {
		for (String key : keys) {
			if(ValidU.isEmpty(map.get(key))) {
				continue;
			}
			Object val = map.get(key);
			if(val instanceof String[]) {
				String[] arr = (String[]) map.get(key);
				if(arr.length > 0) {
					arr[0] = arr[0] + " 00:00:00";
				}
				if(arr.length > 1) {
					arr[1] = arr[1] + " 23:59:59";
				}
			}
			if(val instanceof List) {
				List arr = (List) map.get(key);
				if(arr.size() > 0 && arr.get(0) instanceof String) {
					arr.set(0, arr.get(0) + " 00:00:00");
				}
				if(arr.size() > 1 && arr.get(1) instanceof String) {
					arr.set(1, arr.get(1) + " 23:59:59");
				}
			}
		}
	}

	public static void fillDateChangeStr(Map<String, Object> map, String... keys) {
		fillDateStr(map, keys);
		parseDateStr(map, null, keys);
	}

	public static void parseDateStr(Map<String, Object> map, String pattern, String... keys) {
		if(ValidU.isEmpty(pattern)) {
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		for (String key : keys) {
			try {
				if(map.get(key) != null) {
					if(map.get(key) instanceof String && ValidU.isNotEmpty(map.get(key))) {
						map.put(key, sdf.parse((String) map.get(key)));
					} else if(map.get(key) instanceof String[]) {
						String[] arr = (String[]) map.get(key);
						Date[] tm = new Date[arr.length];
						for (int i = 0; i < arr.length; i++) {
							if(ValidU.isNotEmpty(arr[i])) {
								tm[i] = sdf.parse(arr[i]);
							} else {
								tm[i] = null;
							}
						}
						map.put(key, tm);
					} else if (map.get(key) instanceof List) {
						List arr = (List) map.get(key);
						if(arr.size() != 0 && arr.get(0) instanceof String) {
							List<Date> tm = new ArrayList<>();
							for (int i = 0; i < arr.size(); i++) {
								if(arr.get(i) instanceof String) {
									if(ValidU.isNotEmpty(arr.get(i))) {
										tm.add(sdf.parse((String) arr.get(i)));
									} else {
										tm.add(null);
									}
								}
							}
							map.put(key, tm);
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 通过有效的key过滤map
	 * @param map
	 * @param keySet
	 * @return
	 * @author smalle
	 * @date 2016年11月15日 下午12:24:36
	 */
	public static <K, V, E> Map filter(Map<K, V> map, Set<E> keySet) {
		Map result = new HashMap(keySet.size());
		for (K k : map.keySet()) {
			if(keySet.contains(k)) {
				result.put(k, (V) map.get(k));
			}
		}
		return result;
	}


	/**
	 * @Description: 将LIST里面MAP的NULL转换为空字符串
	 * @Param:
	 * @Return:
	 * @Author: zhouhao
	 * @Date: 21:24 2018/11/18
	 */
	public static List<Map<String, Object>> nvlQueryMap(List<Map<String, Object>> dataList){
		for(Map<String, Object> data : dataList) {
			if(ValidU.isNotEmpty(data)) {
				for(Map.Entry<String, Object> entry : data.entrySet()) {
					if(ValidU.isEmpty(entry.getValue())) {
						entry.setValue("");
					}
				}
			}
		}
		return dataList;
	}

	/**
	 * @Description: 将MAP里面的空值转换成空字符串
	 * @Param:
	 * @Return:
	 * @Author: zhouhao
	 * @Date: 16:29 2018/11/23
	 */
	public static Map<String, Object> nvlMap(Map<String, Object> dataMap) {

		if(ValidU.isNotEmpty(dataMap)) {
			for(Map.Entry<String, Object> entry : dataMap.entrySet()) {
				if(ValidU.isEmpty(entry.getValue())) {
					entry.setValue("");
				}
			}
		}
		return dataMap;
	}

	/**
	 * 将 List(存放的Map) 按照map的某个字段(key)的值分组
	 * @param dataList
	 * @param key 分组字段
	 * @return
	 * @author smalle
	 * @date 2016年11月26日 下午8:27:37
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, List> groupByMapKey(List dataList, String key) {
		Map<String, List> resultMap = new HashMap<String, List>();
		for (Map map : (List<Map>) dataList) {
			String keyStr = String.valueOf(map.get(key));
			if(resultMap.containsKey(keyStr)){
				resultMap.get(keyStr).add(map);
			} else {
				List<Map> list = new ArrayList<Map>();
				list.add(map);
				if(null != keyStr && !"".equals(keyStr)) { // 业务上要求不为空
					resultMap.put(String.valueOf(map.get(key)), list);
				}
			}
		}

		return resultMap;
	}


	/**
	 * @Description: 根据某几个字段进行分组
	 * @Param: [dataList, keys, joinStr]
	 * @Return: java.util.Map<java.lang.String,java.util.List>
	 * @Author: zhouhao
	 * @Date: 17:39 2019/4/23
	 */
	public static Map<String, List> groupByMapKey(List dataList, List<String> keys, String joinStr) {
		if(ValidU.isEmpty(joinStr)) {
			joinStr = "$";
		}
		Map<String, List> resultMap = new HashMap<String, List>();
		for (Map map : (List<Map>) dataList) {
			String keyStr = "";
			for (String key : keys) {
				keyStr += joinStr + String.valueOf(map.get(key));
			}
			if(ValidU.isNotEmpty(keyStr))
				keyStr = keyStr.substring(1);

			if(resultMap.containsKey(keyStr)){
				resultMap.get(keyStr).add(map);
			} else {
				List<Map> list = new ArrayList<Map>();
				list.add(map);
				if(ValidU.isNotEmpty(keyStr)) {
					resultMap.put(keyStr, list);
				}
			}
		}

		return resultMap;
	}

	/**
	 * 将 List(存放的Bean) 按照bean的某个字段(filed)的值分组. (使用反射)
	 * @param beanList
	 * @param filedName
	 * @param classes
	 * @return
	 */
	public static Map<Object, List> listBeanGroupBy(List<? extends Object> beanList, String filedName, Class classes) {
		HashMap<Object, List> resultMap = new HashMap();

		Method method = null;
		Method[] methods = classes.getMethods();
		for (Method m : methods) {
			if(("get" + toUpperCaseFirst(filedName)).equals(m.getName())) {
				method = m;
				break;
			}
		}

		if(method != null) {
			for (Object object : beanList) {
				Object retObj = null;
				try {
					retObj = method.invoke(object);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				if(resultMap.containsKey(retObj)) {
					(resultMap.get(retObj)).add(object);
				} else {
					ArrayList list = new ArrayList();
					list.add(object);
					if(null != retObj) {
						resultMap.put(retObj, list);
					}
				}
			}
		}

		return resultMap;
	}

	public static <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
		if(ValidU.isEmpty(coll1)) {
			return new ArrayList<>();
		}
		if(ValidU.isEmpty(coll2)) {
			return coll1;
		}
		// CollUtil.subtractToList 如果参数2位空集合，则返回空集合
		return CollUtil.subtract(coll1, coll2);
	}


	public static String toUpperCaseFirst(String string) {
		char[] methodName = string.toCharArray();
		methodName[0] = Character.toUpperCase(methodName[0]);
		return String.valueOf(methodName);
	}

	/**
	 * 快速组装实例
	 */
	public static class Instance {
		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V> Map<String, V> toMap(String name1, V1 value1) {
			return populateMap(new HashMap<String, V>(), name1, value1);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V, V2 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2) {
			return populateMap(new HashMap<String, V>(), name1, value1, name2, value2);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V, V2 extends V, V3 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3) {
			return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4) {
			return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V, V5 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4, String name5, V5 value5) {
			return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4, name5, value5);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		public static <V, V1 extends V, V2 extends V, V3 extends V, V4 extends V, V5 extends V, V6 extends V> Map<String, V> toMap(String name1, V1 value1, String name2, V2 value2, String name3, V3 value3, String name4, V4 value4, String name5, V5 value5, String name6, V6 value6) {
			return populateMap(new HashMap<String, V>(), name1, value1, name2, value2, name3, value3, name4, value4, name5, value5, name6, value6);
		}

		/**
		 * Create a map from passed nameX, valueX parameters
		 * @return The resulting Map
		 */
		@SuppressWarnings("unchecked")
		public static <K, V> Map<String, V> toMap(Object... data) {
			if (data.length == 1 && data[0] instanceof Map) {
				// return UtilGenerics.<String, V>checkMap(data[0]); // TODO 校验
			}
			if (data.length % 2 == 1) {
				IllegalArgumentException e = new IllegalArgumentException("You must pass an even sized array to the toMap method (size = " + data.length + ")");
				throw e;
			}
			Map<String, V> map = new HashMap<String, V>();
			for (int i = 0; i < data.length;) {
				map.put((String) data[i++], (V) data[i++]);
			}
			return map;
		}

		@SuppressWarnings("unchecked")
		private static <K, V> Map<String, V> populateMap(Map<String, V> map, Object... data) {
			for (int i = 0; i < data.length;) {
				map.put((String) data[i++], (V) data[i++]);
			}
			return map;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			return theSet;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1, T obj2) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			return theSet;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1, T obj2, T obj3) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			theSet.add(obj3);
			return theSet;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			theSet.add(obj3);
			theSet.add(obj4);
			return theSet;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			theSet.add(obj3);
			theSet.add(obj4);
			theSet.add(obj5);
			return theSet;
		}

		/**
		 * Create a Set from passed objX parameters
		 * @return The resulting Set
		 */
		public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			theSet.add(obj3);
			theSet.add(obj4);
			theSet.add(obj5);
			theSet.add(obj6);
			return theSet;
		}

		public static <T> Set<T> toSet(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6, T obj7, T obj8) {
			Set<T> theSet = new LinkedHashSet<T>();
			theSet.add(obj1);
			theSet.add(obj2);
			theSet.add(obj3);
			theSet.add(obj4);
			theSet.add(obj5);
			theSet.add(obj6);
			theSet.add(obj7);
			theSet.add(obj8);
			return theSet;
		}

		public static <T> Set<T> toSet(Collection<T> collection) {
			if (collection == null) return null;
			if (collection instanceof Set<?>) {
				return (Set<T>) collection;
			} else {
				Set<T> theSet = new LinkedHashSet<T>();
				theSet.addAll(collection);
				return theSet;
			}
		}

		public static <T> Set<T> toSetArray(T[] data) {
			if (data == null) {
				return null;
			}
			Set<T> set = new LinkedHashSet<T>();
			for (T value: data) {
				set.add(value);
			}
			return set;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			return list;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1, T obj2) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			list.add(obj2);
			return list;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1, T obj2, T obj3) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			list.add(obj2);
			list.add(obj3);
			return list;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			list.add(obj2);
			list.add(obj3);
			list.add(obj4);
			return list;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4, T obj5) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			list.add(obj2);
			list.add(obj3);
			list.add(obj4);
			list.add(obj5);
			return list;
		}

		/**
		 * Create a list from passed objX parameters
		 * @return The resulting List
		 */
		public static <T> List<T> toList(T obj1, T obj2, T obj3, T obj4, T obj5, T obj6) {
			List<T> list = new LinkedList<T>();

			list.add(obj1);
			list.add(obj2);
			list.add(obj3);
			list.add(obj4);
			list.add(obj5);
			list.add(obj6);
			return list;
		}
	}

}
