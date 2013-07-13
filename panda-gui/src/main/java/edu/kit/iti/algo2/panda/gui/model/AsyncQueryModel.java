package edu.kit.iti.algo2.panda.gui.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.kit.iti.algo2.panda.management.IndexManager;

public class AsyncQueryModel extends QueryModel {
	private static final long serialVersionUID = 5648176460342697920L;
	
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private Future<?> task;
	
	public AsyncQueryModel(IndexManager manager, HashMap<String, Path> fileViewer) {
		super(manager, fileViewer);
	}
	
	public void stopOtherQuery() {
		if (this.task != null) {
			this.task.cancel(true);
			try {
				this.task.get();
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public void setQuery(final String queryString) {
		this.stopOtherQuery();
		this.task = service.submit(new Runnable() {
			@Override
			public void run() {
				AsyncQueryModel.super.setQuery(queryString);
			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.service.shutdownNow();
	}


}
