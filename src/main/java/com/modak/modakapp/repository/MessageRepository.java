package com.modak.modakapp.repository;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    Page<Message> findByFamily(Family family, Pageable pageable);
}
