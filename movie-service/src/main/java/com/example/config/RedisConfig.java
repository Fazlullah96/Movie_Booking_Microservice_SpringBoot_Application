package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

        Map<String, RedisCacheConfiguration> specificCacheConfig = new HashMap<>();

        specificCacheConfig.put("MOVIE_CACHE", defaultConfiguration.entryTtl(Duration.ofMinutes(60)));
        specificCacheConfig.put("MOVIE_LANGUAGE_CACHE_LIST", defaultConfiguration.entryTtl(Duration.ofMinutes(10)));
        specificCacheConfig.put("MOVIE_GENRE_CACHE_LIST", defaultConfiguration.entryTtl(Duration.ofMinutes(10)));
        specificCacheConfig.put("MOVIE_ACTIVE_CAHCE_LIST", defaultConfiguration.entryTtl(Duration.ofMinutes(3)));
        specificCacheConfig.put("MOVIE_INACTIVE_CACHE_LIST", defaultConfiguration.entryTtl(Duration.ofMinutes(3)));

        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(specificCacheConfig)
                .build();
    }
}
