package com.igeeksky.xcache.support.Jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igeeksky.xcache.common.KeyValue;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-13
 */
public class TestJackson {

    @Test
    void testTypeReference() throws JsonProcessingException {
        User<String, Integer> u1 = new User<>("Rock", 3);
        User<String, Integer> u2 = new User<>("John", 4);
        List<User<String, Integer>> users = new ArrayList<>();
        users.add(u1);
        users.add(u2);
        KeyValue<String, List<User<String, Integer>>> kv = new KeyValue<>("test", users);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(kv);
        System.out.println(json);

        TypeReference<KeyValue<String, List<User<String, Integer>>>> typeReference = new TypeReference<KeyValue<String, List<User<String, Integer>>>>() {
        };

        KeyValue<String, List<User<String, Integer>>> keyValue = mapper.readValue(json, typeReference);


        // JavaType javaType = mapper.getTypeFactory().constructParametricType(KeyValue.class, String.class, List.class);
        // KeyValue<String, List<User<String, Integer>>> keyValue = mapper.readValue(json, javaType);

        String key = keyValue.getKey();
        System.out.println("1.key:" + key.getClass());
        List<User<String, Integer>> value = keyValue.getValue();
        System.out.println("2.value:" + value.getClass());
        User<String, Integer> user1 = value.get(0);
        System.out.println("3.user1:" + user1.getClass());
        String name = user1.getName();
        System.out.println("4.name:" + name.getClass());
        Integer age = user1.getAge();
        System.out.println("5.age:" + age.getClass());
    }

    public static class User<K, V> {

        private K name;
        private V age;

        public User() {
        }

        public User(K name, V age) {
            this.name = name;
            this.age = age;
        }

        public K getName() {
            return name;
        }

        public void setName(K name) {
            this.name = name;
        }

        public V getAge() {
            return age;
        }

        public void setAge(V age) {
            this.age = age;
        }
    }

}
