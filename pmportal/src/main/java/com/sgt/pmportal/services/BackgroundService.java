package com.sgt.pmportal.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BackgroundService implements ServletContextListener{

	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		//scheduler.scheduleAtFixedRate(process, delay, interval, unit of time);
		scheduler.scheduleAtFixedRate(new AlertService(), 1, 7, TimeUnit.DAYS);
	}
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		scheduler.shutdownNow();
	}
}
