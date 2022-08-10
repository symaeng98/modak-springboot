package com.modak.modakapp.service;

import com.modak.modakapp.date.KoreanLunarCalendar;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Anniversary ann = anniversaryRepository.findById(id)
                .orElseThrow(() -> new NoSuchAnniversaryException("기념일이 존재하지 않습니다."));
        if(ann.getDeletedAt()!=null){
            throw new NoSuchAnniversaryException("기념일이 존재하지 않습니다.");
        }
        else return ann;
    }

    public void deleteAnniversary(int id){
        Anniversary findAnn = findAnniversaryById(id);
        findAnn.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void updateAnniversary(int id, String title, String startDate, String endDate, String category, String memo, int isYear){
        Anniversary findAnn = findAnniversaryById(id);
        findAnn.setTitle(title);
        findAnn.setStartDate(Date.valueOf(startDate));
        findAnn.setEndDate(Date.valueOf(endDate));
        findAnn.setCategory(Category.valueOf(category));
        findAnn.setMemo(memo);
        findAnn.setIsYear(isYear);
    }

    public void updateBirthdayAndIsLunar(int id, String birthday,int isLunar){
        Anniversary findAnn = findAnniversaryById(id);
        findAnn.setStartDate(Date.valueOf(birthday));
        findAnn.setEndDate(Date.valueOf(birthday));
        findAnn.setIsLunar(isLunar);
    }

    public Anniversary findBirthdayByMember(int memberId){
        return anniversaryRepository.findAnniversaryByIsMemberBirthday(memberId);
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
        int cnt = 0;
        for (String date : dates) {
            List<AnniversaryDataDTO> anniversaryDataDTOs = new ArrayList<>();
            for (Anniversary a : anniversariesByDate) {
                Date startDate = a.getStartDate();
                if(a.getIsLunar()==0){ // 양력이면
                    if(startDate.equals(Date.valueOf(date))){ // 해당 날짜면
                        AnniversaryDataDTO annDto = AnniversaryDataDTO.builder().annId(a.getId()).title(a.getTitle()).memo(a.getMemo())
                                .category(a.getCategory().name()).build();
                        anniversaryDataDTOs.add(annDto);
                        cnt += 1;
                    }else{
                        if(a.getIsYear()==1){ // 매년 반복이면 달, 일만 같으면 됨
                            if(isValidSolarYear(startDate.toString(),date)){
                                AnniversaryDataDTO annDto = AnniversaryDataDTO.builder().annId(a.getId()).title(a.getTitle()).memo(a.getMemo())
                                        .category(a.getCategory().name()).build();
                                anniversaryDataDTOs.add(annDto);
                                cnt += 1;
                            }
                        }
                    }
                }
                else{ // 음력이면
                    if(isValidLunarYear(startDate.toString(),date)){
                        AnniversaryDataDTO annDto = AnniversaryDataDTO.builder().annId(a.getId()).title(a.getTitle()).memo(a.getMemo())
                                .category(a.getCategory().name()).build();
                        anniversaryDataDTOs.add(annDto);
                        cnt += 1;
                    }
                }
            }
            result.put(date, anniversaryDataDTOs);
        }

        TreeMap<String, List<AnniversaryDataDTO>> treeResult = new TreeMap<>(result);

        return new DateAnniversaryResponse(cnt,treeResult);
    }

    public boolean isValidLunarYear(String start, String date){
        KoreanLunarCalendar cal = KoreanLunarCalendar.getInstance();
        cal.setLunarDate(Integer.parseInt(date.substring(0,4)),Integer.parseInt(start.substring(5,7)),Integer.parseInt(start.substring(8)),false);
        String solarIsoFormat = cal.getSolarIsoFormat();
        return solarIsoFormat.substring(5).equals(date.substring(5));
    }

    public boolean isValidSolarYear(String start, String date){
        return start.substring(5).equals(date.substring(5));
    }
}
