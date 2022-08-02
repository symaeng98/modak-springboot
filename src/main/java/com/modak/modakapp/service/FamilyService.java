package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;

    @Transactional
    public int join(Family family){
        familyRepository.save(family);
        return family.getId();
    }

    public Family find(int id){
        Family family = familyRepository.findOne(id);
        isDeleted(family);
        return family;
    }

    @Transactional
    public void deleteFamily(Family family){
        family.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void isDeleted(Family family){
        if(family.getDeletedAt()!=null){
            throw new NoResultException();
        }
    }
}
