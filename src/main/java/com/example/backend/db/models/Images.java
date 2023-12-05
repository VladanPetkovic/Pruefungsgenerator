package com.example.backend.db.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Images {
    @JsonAlias({"image_id"})
    String image_id;
    @JsonAlias({"link"})
    String link;
    @JsonAlias({"imageName"})
    String imageName;
    @JsonAlias({"position"})
    String position;

    // Jackson needs the default constructor
    public Images() {}
}
