package com.dreamgyf.dim.entity;

import java.io.Serializable;

public class Group implements Serializable {

    private Integer id;

    private String name;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
