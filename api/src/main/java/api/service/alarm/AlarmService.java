package api.service.alarm;

import api.response.ApiDataResponse;
import api.response.ApiResponse;
import api.response.ApiStatusResponse;
import dto.PageInfo;
import dto.PageableInfo;
import dto.querydsl.QueryDslPageResponse;
import entity.alarm.Alarm;
import entity.user.User;
import exception.CommonException;
import exception.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jpa.repository.alarm.AlarmRepository;
import jpa.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import querydsl.repository.alarm.QueryDslAlarmRepository;
import querydsl.repository.user.QueryDslUserRepository;
import util.PageCalculator;

import java.util.List;

import static exception.Status.*;


@RequiredArgsConstructor
@Service
@Transactional
public class AlarmService {

    private final UserRepository userRepository;
    private final QueryDslUserRepository queryDslUserRepository;
    private final AlarmRepository alarmRepository;
    private final QueryDslAlarmRepository queryDslAlarmRepository;

    public ApiResponse checkAlarm(Long alarmId, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_USER));
            Alarm alarm = alarmRepository.findById(alarmId)
                    .orElseThrow(() ->  new CommonException(NOT_FOUND_USER));
            //TODO
//            alarm.setChecked(true);
            return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse deleteAlarm(Long alarmId, Long userId) {

            userRepository.findById(userId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_USER));
            Alarm alarm = alarmRepository
                    .findById(alarmId).orElseThrow(() ->  new CommonException(NOT_FOUND_USER));
            alarmRepository.delete(alarm);
            return ApiStatusResponse.of(SUCCESS);
    }
    public ApiResponse alarmList(Integer pageNum, Integer pageSize, Long userId, Boolean checked) {

            User user = userRepository.findById(userId)
                    .orElseThrow(()-> new CommonException(NOT_FOUND_USER));
            PageableInfo pageableInfo = PageCalculator.toPageableInfo(pageNum, pageSize);

            QueryDslPageResponse<Alarm> queryDslPageResponse;
            if(checked){
                queryDslPageResponse = queryDslAlarmRepository.findCheckedAlarm(pageableInfo, user.getId());
            }else{
                queryDslPageResponse = queryDslAlarmRepository.findUncheckedAlarm(pageableInfo, user.getId());
            }

            long totalCount = queryDslPageResponse.getTotalCount();
            List<Alarm> content = queryDslPageResponse.getContent();
            int elementSize = content.size();
            PageInfo<List<Alarm>> data = PageCalculator.toPageInfo(content, pageableInfo, totalCount, elementSize);
            return ApiDataResponse.of(data,SUCCESS);
    }
    public void save(Alarm alarm) {
        alarmRepository.save(alarm);
    }

    public void saveAll(List<Alarm> list) {
        alarmRepository.saveAll(list);
    }
}
