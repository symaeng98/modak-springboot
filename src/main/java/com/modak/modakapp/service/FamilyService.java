package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.exception.family.NoSuchFamilyException;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;

    public int join(Family family) {
        familyRepository.save(family);
        return family.getId();
    }

    public Family find(int id) {
        return familyRepository.findById(id).orElseThrow(() -> new NoSuchFamilyException("가족 정보가 없습니다."));
    }

    public void deleteFamily(Family family) {
        family.removeFamily(Timestamp.valueOf(LocalDateTime.now()));
    }

}
