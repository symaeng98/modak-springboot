package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.exception.family.NoSuchFamilyException;
import com.modak.modakapp.repository.FamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FamilyService {
    private final FamilyRepository familyRepository;

    @Transactional
    public int join(Family family) {
        familyRepository.save(family);
        return family.getId();
    }

    public Family getById(int id) {
        return familyRepository.findById(id).orElseThrow(() -> new NoSuchFamilyException("가족 정보가 없습니다."));
    }

    public Family getByCode(String code) {
        return familyRepository.findByCode(code).orElseThrow(() -> new NoSuchFamilyException("가족 정보가 없습니다."));
    }

    public void deleteFamily(Family family) {
        family.removeFamily(Timestamp.valueOf(LocalDateTime.now()));
    }

    public String generateInvitationCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        Random random = new Random();
        String generatedString;
        while (true) {
            generatedString = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            if (!familyRepository.isExists(generatedString)) {
                break;
            }
        }
        return generatedString;
    }
}
