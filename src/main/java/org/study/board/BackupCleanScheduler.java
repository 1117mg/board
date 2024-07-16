package org.study.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupCleanScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void cleanUpOldBackups() {
        String sql = "DELETE FROM user_backup WHERE backup_date < DATE_SUB(NOW(), INTERVAL 5 YEAR)";
        int rowsAffected = jdbcTemplate.update(sql);
        System.out.println("5년이 지난 회원 데이터가 자동삭제되었습니다."+rowsAffected);
    }
}