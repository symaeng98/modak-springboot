package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterRepository extends JpaRepository<Letter, Integer> {
}
