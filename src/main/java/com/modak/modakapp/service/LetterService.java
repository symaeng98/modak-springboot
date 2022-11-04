package com.modak.modakapp.service;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.letter.LetterDTO;
import com.modak.modakapp.dto.letter.LettersDTO;
import com.modak.modakapp.exception.letter.NoSuchLetterException;
import com.modak.modakapp.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;

    @Transactional
    public int join(Letter letter) {
        letterRepository.save(letter);
        return letter.getId();
    }

    public Letter findById(int letterId) {
        return letterRepository.findById(letterId).orElseThrow(() -> new NoSuchLetterException("해당 편지가 없습니다."));
    }

    public LettersDTO getLettersByMember(Member fromMember) {
        List<Letter> letterLists = letterRepository.findLettersByMember(fromMember);

        List<LetterDTO> letters = new ArrayList<>();

        letterLists.forEach(l -> {
            LetterDTO letterDto = LetterDTO.builder()
                    .letterId(l.getId())
                    .fromMemberId(l.getFromMember().getId())
                    .toMemberId(l.getToMember().getId())
                    .date(l.getDate().toString())
                    .content(l.getContent())
                    .envelope(l.getEnvelope())
                    .isNew(0)
                    .build();
            letters.add(letterDto);
        });

        return LettersDTO.builder().count(letterLists.size()).letterList(letters).build();
    }

    @Transactional
    public void deleteAllByMember(Member member) {
        letterRepository.deleteAllByMember(member, Timestamp.valueOf(LocalDateTime.now()));
    }
}
