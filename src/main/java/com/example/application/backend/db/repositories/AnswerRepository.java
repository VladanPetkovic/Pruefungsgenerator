package com.example.application.backend.db.repositories;

import com.example.application.backend.db.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
//    public Answer get(String answer) {
//        return getAnswerDAO().read(answer);
//    }
//
//    public int getMaxAnswerId() {
//        return getAnswerDAO().getMaxAnswerId();
//    }
//
//    public void removeUnused() {
//        getAnswerDAO().delete();
//    }
}
