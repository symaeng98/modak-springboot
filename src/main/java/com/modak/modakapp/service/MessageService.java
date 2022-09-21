package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Message;
import com.modak.modakapp.dto.message.MessageDTO;
import com.modak.modakapp.dto.message.MessageResult;
import com.modak.modakapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageResult getMessagesByFamily(Family family, PageRequest pageRequest) {
        List<Message> messageList = messageRepository.findByFamily(family, pageRequest).getContent();

        List<MessageDTO> messageDTOList = new ArrayList<>();

        messageList.forEach(m -> {
            MessageDTO msgDto = MessageDTO.builder()
                    .user_id(m.getMember().getId())
                    .content(m.getContent())
                    .send_at(m.getSend_at())
                    .metadata(m.getMetaData())
                    .build();

            messageDTOList.add(msgDto);
        });

        return MessageResult.builder()
                .result(messageDTOList)
                .build();
    }
}