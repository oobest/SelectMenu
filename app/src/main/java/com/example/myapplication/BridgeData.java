package com.example.myapplication;

/**
 * @author albert
 * @date 2022/3/22 16:01
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class BridgeData {

    private final int id;

    private final String name;

    public BridgeData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
