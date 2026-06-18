package com.sky.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        //配置key序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        //自定义ObjectMapper 支持 java8 时间
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());//注册Java 8 时间模块
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);//禁用时间戳格式
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);//忽略未知属性

        om.activateDefaultTyping(om.getPolymorphicTypeValidator(),ObjectMapper.DefaultTyping.NON_FINAL);//保留类型信息

        //配置value的序列化，将pojo转换为json对象，读取时反序列化为pojo
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new
                GenericJackson2JsonRedisSerializer(om);

        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        //初始化模版
        template.afterPropertiesSet();
        return template;
    }
}
