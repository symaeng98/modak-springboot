package com.modak.modakapp.service;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.letter.FromLettersDTO;
import com.modak.modakapp.dto.letter.LetterDTO;
import com.modak.modakapp.repository.LetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public FromLettersDTO getSendLetterListsByFromMember(Member fromMember) {
        List<Letter> letterLists = letterRepository.findLettersByFromMember(fromMember);

        List<LetterDTO> sendLetterLists = new ArrayList<>();

        letterLists.forEach(l->{
            LetterDTO letterDto = LetterDTO.builder()
                    .fromMemberId(l.getFromMember().getId())
                    .toMemberId(l.getToMember().getId())
                    .date(l.getDate().toString())
                    .content(l.getContent())
                    .envelope(l.getEnvelope())
                    .build();
            sendLetterLists.add(letterDto);
        });

        return FromLettersDTO.builder().sendLetterList(sendLetterLists).build();
    }
}
