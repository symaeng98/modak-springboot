package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;

    public Long join(Family family){
        familyRepository.save(family);
        return family.getId();
    }
}
