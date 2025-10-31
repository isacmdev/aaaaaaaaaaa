package com.example.demo.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration(redisHost, redisPort);
        System.out.println("Conectando a Redis en " + redisHost + ":" + redisPort);
        return new LettuceConnectionFactory(cfg);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // ESTA LÍNEA ES LA CLAVE - Activa la información de tipo
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder
                .value(jacksonSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jacksonSerializer)
                .build();

        ReactiveRedisTemplate<String, Object> template = new ReactiveRedisTemplate<>(factory, context);

        testRedisConnection(template);

        return template;
    }

    private void testRedisConnection(ReactiveRedisTemplate<String, Object> template) {
        String testKey = "test:connection";
        String testValue = "OK";

        template.opsForValue().set(testKey, testValue, Duration.ofSeconds(10))
                .then(template.opsForValue().get(testKey))
                .doOnSuccess(result -> {
                    if (testValue.equals(result)) {
                        System.out.println("✅ Conexión a Redis exitosa y serialización funcionando");
                    } else {
                        System.out.println("⚠️  Conexión a Redis OK pero problema con serialización");
                    }
                })
                .doOnError(e -> System.out.println("❌ Error conectando a Redis: " + e.getMessage()))
                .subscribe();
    }
}