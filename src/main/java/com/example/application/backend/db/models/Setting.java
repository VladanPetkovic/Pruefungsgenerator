package com.example.application.backend.db.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "settings")
public class Setting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer language;
    private Double displayWidth;
    private Double displayHeight;

    // set default values
    public Setting() {
        this.language = 0;
        this.displayWidth = 800.0;
        this.displayHeight = 600.0;
    }
}
