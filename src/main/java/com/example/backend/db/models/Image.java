package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Image {
    private int id;
    private byte[] image;
    private String name;
    private int position;
    private String comment;
}
