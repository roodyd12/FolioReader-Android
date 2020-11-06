package com.folioreader.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {

    private static volatile ObjectMapper objectMapper;

    private ObjectMapperSingleton() {
    }

    public static ObjectMapper getObjectMapper() {

        if (objectMapper == null) {
            synchronized (ObjectMapperSingleton.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }

        return objectMapper;
    }
}
