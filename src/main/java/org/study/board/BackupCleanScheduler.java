package org.study.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.study.board.repository.UserMapper;
import org.study.board.service.UserService;

@Component
public class BackupCleanScheduler {

    @Autowired
    private UserService service;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void cleanUpOldBackups() {
        service.deleteOldBackups();
        System.out.println("5년이 지난 회원정보 백업 데이터가 자동삭제되었습니다.");
    }
}