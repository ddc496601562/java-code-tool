package com.baidu.rigel.cdc.serverlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * come from  hadoop  
 * A very simple servlet to serve up a text representation of the current
 * stack traces. It both returns the stacks to the caller and logs them.
 * Currently the stack traces are done sequentially rather than exactly the
 * same data.
 */
public  class StackServlet extends HttpServlet {
	private static final long serialVersionUID = -6284183679759467039L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		PrintWriter out = new PrintWriter(response.getOutputStream());
		StackServlet.printThreadInfo(out, "thread stack");
		out.close();
	}
	private static String getTaskName(long id, String name) {
		if (name == null) {
			return Long.toString(id);
		}
		return id + " (" + name + ")";
	}
	public static void printThreadInfo(PrintWriter stream, String title) {
		final int STACK_DEPTH = 20;
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		boolean contention = threadBean.isThreadContentionMonitoringEnabled();
		long[] threadIds = threadBean.getAllThreadIds();
		stream.println("Process Thread Dump: " + title);
		stream.println(threadIds.length + " active threads");
		for (long tid : threadIds) {
			ThreadInfo info = threadBean.getThreadInfo(tid, STACK_DEPTH);
			if (info == null) {
				stream.println("  Inactive");
				continue;
			}
			stream.println("Thread "
					+ getTaskName(info.getThreadId(), info.getThreadName())
					+ ":");
			Thread.State state = info.getThreadState();
			stream.println("  State: " + state);
			stream.println("  Blocked count: " + info.getBlockedCount());
			stream.println("  Waited count: " + info.getWaitedCount());
			if (contention) {
				stream.println("  Blocked time: " + info.getBlockedTime());
				stream.println("  Waited time: " + info.getWaitedTime());
			}
			if (state == Thread.State.WAITING) {
				stream.println("  Waiting on " + info.getLockName());
			} else if (state == Thread.State.BLOCKED) {
				stream.println("  Blocked on " + info.getLockName());
				stream.println("  Blocked by "
						+ getTaskName(info.getLockOwnerId(),
								info.getLockOwnerName()));
			}
			stream.println("  Stack:");
			for (StackTraceElement frame : info.getStackTrace()) {
				stream.println("    " + frame.toString());
			}
		}
		stream.flush();
	}
}
