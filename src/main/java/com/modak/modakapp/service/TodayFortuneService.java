package com.modak.modakapp.service;

import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.TodayFortune;
import com.modak.modakapp.repository.TodayFortuneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodayFortuneService {
    private final TodayFortuneRepository todayFortuneRepository;

    public TodayFortune generateTodayFortune() {
        return todayFortuneRepository.getTodayFortuneRandom();
    }

    public TodayFortune getHomeTodayFortune(Member member) {
        TodayFortune todayFortune = member.getTodayFortune();
        Date todayFortuneAt = member.getTodayFortuneAt();
        if (todayFortune == null || todayFortuneAt.before(Date.valueOf(LocalDate.now()))) {
            return null;
        } else {
            return todayFortune;
        }
    }
}
