package com.example.demo.infrastructure.ex.messageerror;

public final class RedisMessageError {
    private RedisMessageError() {}

    public static final String CACHE_ERROR = "Error al acceder a la caché de Redis.";
    public static final String CACHE_PUT_ERROR = "Error al almacenar el dato en la caché de Redis.";
    public static final String CACHE_GET_ERROR = "Error al obtener el dato de la caché de Redis.";
}
