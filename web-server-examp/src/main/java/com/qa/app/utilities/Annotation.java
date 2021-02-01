package com.qa.app.utilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.qa.app.http.filter.Filter;
import com.qa.app.http.filter.Filterable;

public class Annotation {
	
	public static List<Filter<?>> scan(String root) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		File dirPath = new File(root);
		File[] files = dirPath.listFiles();
		
		List<Filter<?>> filters = new ArrayList<Filter<?>>();
		
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					scan(f.getAbsolutePath());
				} else {
					System.out.println(f.getName());
					if (f.getName().contains(".java")) {
						System.out.println(f.getAbsolutePath());
						String[] classDir = f.getAbsolutePath()
											 .split("app");
						
						String classPackage = classDir[1].replace("\\", ".");
						classPackage = "com.qa.app" + classPackage;
						
						Class<?> clazz = Class.forName(classPackage.replaceFirst(".java", ""));
						
						if (clazz.isAnnotationPresent(Filterable.class)) {
							Constructor<?> constructor = clazz.getConstructor();
							filters.add((Filter<?>) constructor.newInstance());
						}
					}
				}
			}
		}
		return filters;
	}
}
