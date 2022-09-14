package com.modak.modakapp.service;

import com.modak.modakapp.repository.TodayTalkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodayTalkService {
    private final TodayTalkRepository todayTalkRepository;

    pu
}