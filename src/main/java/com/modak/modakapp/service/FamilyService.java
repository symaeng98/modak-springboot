package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.exception.family.NoSuchFamilyException;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;

    public int join(Family family){
        familyRepository.save(family);
        return family.getId();
    }

    public Family find(int id){
        Family family = familyRepository.findById(id).orElseThrow(() -> new NoSuchFamilyException("가족 정보가 없습니다."));
        isDeleted(family);
        return family;
    }



    public void deleteFamily(Family family){
        family.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void isDeleted(Family family){
        if(family.getDeletedAt()!=null){
            throw new NoResultException();
        }
    }
}
