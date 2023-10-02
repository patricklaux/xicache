package com.igeeksky.xcache.autoconfigure;

import java.util.StringJoiner;

/**
 * @author Patrick.Lau
 * @since 0.0.4 2023-09-29
 */
public class Store {

    private String id;

    private String connection;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("\"id\":" + id + "\"")
                .add("\"connection\":\"" + connection + "\"")
                .toString();
    }

}