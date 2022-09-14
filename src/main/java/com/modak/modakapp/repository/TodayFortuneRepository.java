package com.modak.modakapp.repository;

import com.modak.modakapp.domain.TodayFortune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodayFortuneRepository extends JpaRepository<TodayFortune, Integer> {
    @Query(value = "SELECT * FROM today_fortune order by RAND() limit 1", nativeQuery = true)
    TodayFortune getTodayFortuneRandom();
}