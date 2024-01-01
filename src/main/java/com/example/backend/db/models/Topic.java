package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Topic {
    private int topic_id;
    private String topic;

    public Topic(Topic other) {
        this.topic_id = other.topic_id;
        this.topic = other.topic;
    }
}
