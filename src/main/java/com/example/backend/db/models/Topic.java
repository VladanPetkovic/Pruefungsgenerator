package com.example.backend.db.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Topic {
    int topic_id;
    String topic;

    public Topic(String topic) {
        setTopic(topic);
    }
}
