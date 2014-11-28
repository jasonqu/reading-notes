beanutil的一个神奇的地方

不管list的长度是多长

最后运行的时间基本是固定的 300ms左右
这是什么原因呢？

package com.qq.adnetwork.task.job.PlacementDI.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

public class A {
	long id;
	long value;
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<A> l = new ArrayList<>();
		int j = 100000;
		for(int i = 0; i< j; i++) {
			A a = new A();
			a.setId(i);
			a.setValue(100 + i);
			l.add(a);
		}
		
		long start = System.currentTimeMillis();
		for(int i = 0; i< j; i++) {
			long x = l.get(i).getId();
			x = l.get(i).getValue();
		}
		System.out.println(System.currentTimeMillis() - start);
		
		start = System.currentTimeMillis();
		for(int i = 0; i< j; i++) {
			long x = (long) PropertyUtils.getSimpleProperty(l.get(i), "id");
			x = (long) PropertyUtils.getSimpleProperty(l.get(i), "value");
		}
		System.out.println(System.currentTimeMillis() - start);
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
}



另一个密
        long start = System.currentTimeMillis();
        //System.out.println();
        for(int i = 0; i < 10; i++) {
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            executorService.shutdown();
        }
        long end = System.currentTimeMillis();
        System.out.println("shut down finished " + (end - start));


        不是线性变化的











