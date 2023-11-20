package com.example.backend.app;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private int position;
    private String path;
    private String name;

    Image()
    {

    }

    Image(int position, String path, String name)
    {
        setPosition(position);
        setPath(path);
        setName(name);
    }
}