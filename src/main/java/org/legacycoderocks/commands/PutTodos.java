package org.legacycoderocks.commands;

import org.legacycoderocks.redis.Redis;

public class PutTodos implements Command {

	private String task;

	public void setTask(String task) {
		this.task = task;
	}

	public void execute() throws Exception {
		Redis.getRedis().getJedis().sadd("todos", task);
	}

}
