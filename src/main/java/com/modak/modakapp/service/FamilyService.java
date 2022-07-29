package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;

    @Transactional
    public int join(Family family){
        familyRepository.save(family);
        return family.getId();
    }
}
