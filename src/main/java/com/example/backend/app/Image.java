package com.example.backend.app;

public class Image {
    private int position;
    private String path;
    private String name;
    public int getPosition()
    {
        return this.position;
    }
    public String getPath()
    {
        return this.path;
    }
    public String getName()
    {
        return this.name;
    }
    public void setPosition(int pos)
    {
        this.position = pos;
    }
    public void setPath(String path)
    {
        this.path = path;
    }
    public void setName(String name)
    {
        this.name = name;
    }

}