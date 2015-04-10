package com.mcar.net;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 我们网络请求线程�?:限制并行的网络请求线程�??
 * */

public class ThreadPoolUtils {

	private ThreadPoolUtils() {
	}

	// 定义核心线程数，并行线程�?
	private static int CORE_POOL_SIZE = 3;

	// 线程池最大线程数：除了正在运行的线程额外保存多少个线�?
	private static int MAX_POOL_SIZE = 200;

	// 额外线程空闲状�?�生存时�?
	private static int KEEP_ALIVE_TIME = 5000;

	// 阻塞队列。当核心线程队列满了放入�?
	// 初始化一个大小为10的泛型为Runnable的队�?
	private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
			10);
	// 线程工厂,把传递进来的runnable对象生成�?个Thread
	private static ThreadFactory threadFactory = new ThreadFactory() {

		// 原子型的integer变量生成的integer值不会重�?
		private final AtomicInteger ineger = new AtomicInteger();

		@Override
		public Thread newThread(Runnable arg0) {
			return new Thread(arg0, "MyThreadPool thread:"
					+ ineger.getAndIncrement());
		}
	};

	// 当线程池发生异常的时候回调进�?
	private static RejectedExecutionHandler handler = new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			// 进行重启操作
		}

	};
	// 线程池ThreadPoolExecutor java自带的线程池
	private static ThreadPoolExecutor threadpool;
	// 静�?�代码块，在类被加载的时候进�?
	static {
		threadpool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
				KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory,
				handler);
	}

	public static void execute(Runnable runnable) {
		threadpool.execute(runnable);
	}
}
