package org.study.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountLockScheduler {

    private final UserMapper mapper;

    //@Scheduled(fixedRate = 30000)
    public void unlockAccounts() {
        List<User> lockedUsers = mapper.findLockedUsers();
        long now = System.currentTimeMillis();

        for (User user : lockedUsers) {
            if (user.getLockTime() != null) {
                long elapsedTime = now - user.getLockTime().getTime();
                if (elapsedTime >= 30000) {
                    user.setFailedAttempts(0);
                    user.setLocked(false);
                    user.setLockTime(null);
                    //user.setRecaptchaRequired(true); // reCAPTCHA 필요 설정
                    mapper.updateStatus(user);
                }
            }
        }
    }
}