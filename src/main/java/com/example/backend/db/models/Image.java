package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Image {
    int image_id;
    String link;
    String imageName;
    int position;

    public Image(String link, String imageName, int position) {
        setLink(link);
        setImageName(imageName);
        setPosition(position);
    }
}
