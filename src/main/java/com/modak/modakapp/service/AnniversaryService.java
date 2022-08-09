package com.modak.modakapp.service;

import com.modak.modakapp.domain.Anniversary;
import com.modak.modakapp.domain.Family;
import com.modak.modakapp.domain.Member;
import com.modak.modakapp.domain.Todo;
import com.modak.modakapp.domain.enums.Category;
import com.modak.modakapp.dto.AnniversaryDataDTO;
import com.modak.modakapp.dto.TodoDataDTO;
import com.modak.modakapp.dto.response.anniversary.DateAnniversaryResponse;
import com.modak.modakapp.dto.response.todo.WeekResponse;
import com.modak.modakapp.exception.anniversary.NoSuchAnniversaryException;
import com.modak.modakapp.repository.AnniversaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AnniversaryService {

    private final AnniversaryRepository anniversaryRepository;

    public int join(Anniversary anniversary){
        Anniversary savedAnniversary = anniversaryRepository.save(anniversary);
        return savedAnniversary.getId();
    }

    public Anniversary findAnniversaryById(int id){
        return anniversaryRepository.findById(id)
                .orElseThrow(() -> new NoSuchAnniversaryException("기념일이 존재하지 않습니다."));
    }

    public DateAnniversaryResponse findDateAnniversaryData(String sd, String ed, Family family){
        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(sd);
        LocalDate end = LocalDate.parse(ed);
        Stream.iterate(start, date->date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start,end)+1)
                .forEach(d -> {
                    dates.add(String.valueOf(d));
                });
        Map<String,List<AnniversaryDataDTO>> result = new HashMap<>();
        String firstDate = dates.get(0);
        String lastDate = dates.get(dates.size()-1);
        Date sqlFirstDate = Date.valueOf(firstDate);
        Date sqlLastDate = Date.valueOf(lastDate);

        List<Anniversary> anniversariesByDate = anniversaryRepository.findAnniversariesByDate(sqlFirstDate, sqlLastDate, family.getId());
        for (String date : dates) {
            List<AnniversaryDataDTO> anniversaryDataDTOs = new ArrayList<>();
            for (Anniversary a : anniversariesByDate) {
                Date startDate = a.getStartDate();
                if (isValid(startDate, Date.valueOf(date))||(a.getIsYear()==1&&isValidYear(startDate.toString(),date))){
                    AnniversaryDataDTO annDto = AnniversaryDataDTO.builder().title(a.getTitle()).memo(a.getMemo())
                            .category(a.getCategory().name()).build();
                    anniversaryDataDTOs.add(annDto);
                }
            }
            result.put(date, anniversaryDataDTOs);
        }

        TreeMap<String, List<AnniversaryDataDTO>> treeResult = new TreeMap<>(result);

        return new DateAnniversaryResponse(treeResult);
    }

    public boolean isValid(Date start,Date date){
        return start.equals(date);
    }

    public boolean isValidYear(String start, String date){
        if(start.substring(5).equals(date.substring(5))){
            return true;
        }
        return false;
    }
}
