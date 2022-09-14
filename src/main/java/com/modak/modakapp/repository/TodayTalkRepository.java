package com.modak.modakapp.repository;

import com.modak.modakapp.domain.TodayTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayTalkRepository extends JpaRepository<TodayTalk, Integer> {
}
