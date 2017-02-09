package org.arquillian.cube.populator.nosql.redis;

import com.lordofthejars.nosqlunit.redis.DefaultRedisInsertionStrategy;
import com.lordofthejars.nosqlunit.redis.RedisConnectionCallback;
import org.arquillian.cube.populator.nosql.api.NoSqlPopulatorService;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RedisPopulatorService implements NoSqlPopulatorService<Redis> {

   private Jedis jedis;

   @Override
   public void connect(String host, int port, String database) {
      jedis = new Jedis(host, port);
      jedis.connect();
   }

   @Override
   public void disconnect() {
      if (jedis != null) {
         jedis.disconnect();
      }
   }

   @Override
   public void execute(List<String> resources) {
      final DefaultRedisInsertionStrategy redisInsertionStrategy = new DefaultRedisInsertionStrategy();
      final RedisConnectionCallback connection = new RedisConnectionCallback() {
         @Override
         public BinaryJedisCommands insertionJedis() {
            return jedis;
         }

         @Override
         public Jedis getActiveJedis(byte[] bytes) {
            return jedis;
         }

         @Override
         public Collection<Jedis> getAllJedis() {
            return Arrays.asList(jedis);
         }
      };

      resources.stream()
              .map(resource -> RedisPopulatorService.class.getResourceAsStream(resource))
              .forEach(dataset -> {
                 try {
                    redisInsertionStrategy.insert(connection, dataset);
                 } catch (Throwable throwable) {
                    throw new IllegalStateException(throwable);
                 }
              });
   }

   @Override
   public void clean() {
      this.jedis.flushDB();
   }

   @Override
   public Class<Redis> getPopulatorAnnotation() {
      return Redis.class;
   }
}
