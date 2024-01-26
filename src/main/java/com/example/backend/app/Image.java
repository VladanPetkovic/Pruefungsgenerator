package com.example.backend.app;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Image {
    private int image_id;
    private String link;
    private String imageName;
    private int position;

    public Image(String link, String imageName, int position) {
        setLink(link);
        setImageName(imageName);
        setPosition(position);
    }
}