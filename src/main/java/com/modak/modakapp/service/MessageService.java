package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Message;
import com.modak.modakapp.dto.message.ConnectionDTO;
import com.modak.modakapp.dto.message.ConnectionResult;
import com.modak.modakapp.dto.message.MessageDTO;
import com.modak.modakapp.dto.message.MessageResult;
import com.modak.modakapp.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageResult getMessagesByFamily(Family family, int count, int lastId) {
        List<Message> messageList;

        if (lastId == 0) {
            messageList = messageRepository.findMessageByCount(family, count);
        } else {
            messageList = messageRepository.findMessageByCountAndLastId(family, count, lastId);
        }

        List<MessageDTO> messageDTOList = new ArrayList<>();

        messageList.forEach(m -> {
            MessageDTO messageDTO = MessageDTO.builder()
                    .messageId(m.getId())
                    .memberId(m.getMember().getId())
                    .content(m.getContent())
                    .metaData(m.getMetaData())
                    .sendAt(m.getSendAt())
                    .build();

            messageDTOList.add(messageDTO);
        });

        int newLastId;

        if (messageList.size() == 0) {
            newLastId = -1;
        } else {
            newLastId = messageDTOList.get(messageDTOList.size() - 1).getMessageId();
        }

        return MessageResult.builder()
                .lastId(newLastId)
                .result(messageDTOList).build();
    }

    public ConnectionResult getConnectionInfoByFamilyMembers(List<Member> members) {
        List<ConnectionDTO> connectionDTOList = new ArrayList<>();

        members.forEach(m -> {
            ConnectionDTO connectionDTO = ConnectionDTO.builder()
                    .memberId(m.getId())
                    .lastJoined(m.getChatLastJoined())
                    .isJoining(m.getConnectionId() != null)
                    .build();

            connectionDTOList.add(connectionDTO);
        });

        return ConnectionResult.builder()
                .result(connectionDTOList)
                .build();
    }
}