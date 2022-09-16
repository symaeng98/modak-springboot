package com.modak.modakapp.service;

import com.modak.modakapp.domain.Letter;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.dto.letter.LetterDTO;
import com.modak.modakapp.dto.letter.ReceivedLettersDTO;
import com.modak.modakapp.dto.letter.SentLettersDTO;
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

    public SentLettersDTO getSentLettersByMember(Member fromMember) {
        List<Letter> letterLists = letterRepository.findSentLettersByMember(fromMember);

        List<LetterDTO> sendLetters = new ArrayList<>();

        letterLists.forEach(l -> {
            LetterDTO letterDto = LetterDTO.builder()
                    .fromMemberId(l.getFromMember().getId())
                    .toMemberId(l.getToMember().getId())
                    .date(l.getDate().toString())
                    .content(l.getContent())
                    .envelope(l.getEnvelope())
                    .isNew(0)
                    .build();
            sendLetters.add(letterDto);
        });

        return SentLettersDTO.builder().sentLetterList(sendLetters).build();
    }

    public ReceivedLettersDTO getReceivedLettersByMember(Member member) {
        List<Letter> letterLists = letterRepository.findReceivedLettersByMember(member);

        List<LetterDTO> receivedLetters = new ArrayList<>();

        letterLists.forEach(l -> {
            LetterDTO letterDto = LetterDTO.builder()
                    .fromMemberId(l.getFromMember().getId())
                    .toMemberId(l.getToMember().getId())
                    .date(l.getDate().toString())
                    .content(l.getContent())
                    .envelope(l.getEnvelope())
                    .isNew(l.getIsNew())
                    .build();
            receivedLetters.add(letterDto);
        });

        return ReceivedLettersDTO.builder().receivedLetterList(receivedLetters).build();
    }

    public ReceivedLettersDTO getReceivedNewLettersByMember(Member member) {
        List<Letter> letterLists = letterRepository.findReceivedNewLettersByMember(member);

        List<LetterDTO> receivedNewLetters = new ArrayList<>();

        letterLists.forEach(l -> {
            LetterDTO letterDto = LetterDTO.builder()
                    .fromMemberId(l.getFromMember().getId())
                    .toMemberId(l.getToMember().getId())
                    .date(l.getDate().toString())
                    .content(l.getContent())
                    .envelope(l.getEnvelope())
                    .isNew(l.getIsNew())
                    .build();
            receivedNewLetters.add(letterDto);
        });

        return ReceivedLettersDTO.builder().receivedLetterList(receivedNewLetters).build();
    }
}
