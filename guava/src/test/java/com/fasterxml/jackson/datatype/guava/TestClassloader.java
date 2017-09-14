package com.fasterxml.jackson.datatype.guava;

import java.net.URLClassLoader;
import java.util.AbstractMap;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Unit tests for verifying that various immutable types
 * (like {@link ImmutableList}, {@link ImmutableMap} and {@link ImmutableSet})
 * work as expected when loaded by another classloader.
 * 
 * @author digitalillusion
 */
public class TestClassloader extends ModuleTestBase {
	private final ObjectMapper MAPPER = mapperWithModule();

	static class Holder {
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
		public Object value;

		public Holder() {
		}

		public Holder(Object v) {
			value = v;
		}
	}

	static class AnotherClassLoader extends URLClassLoader {
		public AnotherClassLoader() {
			super(((URLClassLoader) AnotherClassLoader.class.getClassLoader()).getURLs(), null);
		}
	};

	/*
	/**********************************************************************
	/* Unit tests for verifying handling in absence of module registration
	/**********************************************************************
	 */

	/**
	 * Immutable types can actually be serialized as regular collections, without
	 * problems of classloading
	 */
	public void testWithoutSerializers() throws Exception {
		AnotherClassLoader cl = instanceAnotherClassLoader();
		Object list = instanceCollectionByClassloader(cl, ImmutableList.class, 1, 2, 3);
		assertEquals("[1,2,3]", MAPPER.writeValueAsString(list));

		Object set = instanceCollectionByClassloader(cl, ImmutableSet.class, "abc", "def");
		assertEquals("[\"abc\",\"def\"]", MAPPER.writeValueAsString(set));

		Object map = instanceMapByClassloader(cl, ImmutableMap.class, new AbstractMap.SimpleEntry<>("a", 1), new AbstractMap.SimpleEntry<>("b", 2));
		assertEquals("{\"a\":1,\"b\":2}", MAPPER.writeValueAsString(map));
	}

	private AnotherClassLoader instanceAnotherClassLoader() {
		AnotherClassLoader cl = new AnotherClassLoader();
		return cl;
	}

	private Object instanceCollectionByClassloader(AnotherClassLoader cl, Class<?> classToInstance, Object... elements) throws ReflectiveOperationException {
		Class<?> prototype = cl.loadClass(classToInstance.getCanonicalName());
		Object builder = prototype.getMethod("builder").invoke(prototype);
		for (Object element : elements) {
			builder.getClass().getMethod("add", Object.class).invoke(builder, element);
		}
		return builder.getClass().getMethod("build").invoke(builder);
	}

	private Object instanceMapByClassloader(AnotherClassLoader cl, Class<?> classToInstance, AbstractMap.SimpleEntry<?, ?>... pairs) throws ReflectiveOperationException {
		Class<?> prototype = cl.loadClass(classToInstance.getCanonicalName());
		Object builder = prototype.getMethod("builder").invoke(prototype);
		for (AbstractMap.SimpleEntry<?, ?> pair : pairs) {
			builder.getClass().getMethod("put", Object.class, Object.class).invoke(builder, pair.getKey(), pair.getValue());
		}
		return builder.getClass().getMethod("build").invoke(builder);
	}
	/*
	/**********************************************************************
	/* Unit tests for actual registered module
	/**********************************************************************
	*/

	public void testImmutableList() throws Exception {
		AnotherClassLoader cl = instanceAnotherClassLoader();
		Object anotherList = instanceCollectionByClassloader(cl, ImmutableList.class);

		ImmutableList<Integer> list = (ImmutableList<Integer>) MAPPER.readValue("[1,2,3]", anotherList.getClass());
		assertEquals(3, list.size());
		assertEquals(Integer.valueOf(1), list.get(0));
		assertEquals(Integer.valueOf(2), list.get(1));
		assertEquals(Integer.valueOf(3), list.get(2));
	}

	public void testImmutableSet() throws Exception {
		AnotherClassLoader cl = instanceAnotherClassLoader();
		Object anotherSet = instanceCollectionByClassloader(cl, ImmutableSet.class);

		ImmutableSet<Integer> set = (ImmutableSet<Integer>) MAPPER.readValue("[3,7,8]", anotherSet.getClass());
		assertEquals(3, set.size());
		Iterator<Integer> it = set.iterator();
		assertEquals(Integer.valueOf(3), it.next());
		assertEquals(Integer.valueOf(7), it.next());
		assertEquals(Integer.valueOf(8), it.next());

		set = MAPPER.readValue("[  ]", new TypeReference<ImmutableSet<Integer>>() {
		});
		assertEquals(0, set.size());
	}

}
