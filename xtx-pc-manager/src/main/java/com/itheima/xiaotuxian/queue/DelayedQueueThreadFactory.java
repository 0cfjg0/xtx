//package com.itheima.xiaotuxian.queue;
///*
// * @author: lbc
// * @Date: 2023-11-10 00:11:13
// * @Descripttion:
// */
//
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class DelayedQueueThreadFactory implements ThreadFactory {
//
//	private final ThreadGroup group;
//	private final AtomicInteger threadNumber = new AtomicInteger(1);
//
//	private String namePrefix;
//
//	public String getNamePrefix() {
//		return namePrefix;
//	}
//
//	public void setNamePrefix(String namePrefix) {
//		this.namePrefix = namePrefix+"-Thread-";
//	}
//
//	public DelayedQueueThreadFactory(String name) {
//		// 声明安全管理器
//		SecurityManager s = System.getSecurityManager();
//		// 得到线程组
//		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
//		// 线程名前缀
//		namePrefix = name+"-Thread-";
//	}
//
//	@Override
//	public Thread newThread(Runnable r) {
//		return new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
//	}
//}
