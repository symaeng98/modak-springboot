package com.modak.modakapp.service;

import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.repository.TodayFortuneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodayFortuneService {
    private final TodayFortuneRepository todayFortuneRepository;

    public TodayFortune generateTodayFortune() {
        return todayFortuneRepository.getTodayFortuneRandom();
    }
}
