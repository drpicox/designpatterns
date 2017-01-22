package org.legacycoderocks.redis;

import redis.clients.jedis.Jedis;

public class Redis {

	private static Redis redis;

	private Jedis redisClient;

	private Redis() {
		redisClient = new Jedis();
	}

	public static Redis getRedis() {
		if (redis == null) {
			redis = new Redis();
		}
		return redis;
	}

	public Jedis getJedis() {
		return redisClient;
	}
}
