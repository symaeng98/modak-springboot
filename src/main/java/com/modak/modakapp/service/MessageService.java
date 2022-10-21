package com.modak.modakapp.service;

import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Message;
import com.modak.modakapp.dto.message.*;
import com.modak.modakapp.dto.metadata.MetaData;
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

        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message message = messageList.get(i);
            MessageDTO messageDTO = MessageDTO.builder()
                    .messageId(message.getId())
                    .memberId(message.getMember().getId())
                    .content(message.getContent())
                    .sendAt(message.getSendAt())
                    .build();
            if (message.getMetaData() == null) {
                messageDTO.setMetaData(null);
            } else {
                MetaData metaData = message.getMetaData();
                messageDTO.setMetaData(
                        MetaDataDTO.builder()
                                .type_code(metaData.getType_code())
                                .key(metaData.getKey())
                                .count(metaData.getCount())
                                .title(metaData.getTitle())
                                .addTodo(metaData.getAddTodo())
                                .participatedUsers(metaData.getParticipatedUsers())
                                .selectedUser(metaData.getSelectedUser())
                                .feeling(metaData.getFeeling())
                                .step(metaData.getStep())
                                .quizType(metaData.getQuizType())
                                .hint(metaData.getHint())
                                .build());
            }
            messageDTOList.add(messageDTO);
        }

        int newLastId;

        if (messageList.size() == 0) {
            newLastId = -1;
        } else {
            newLastId = messageDTOList.get(0).getMessageId();
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