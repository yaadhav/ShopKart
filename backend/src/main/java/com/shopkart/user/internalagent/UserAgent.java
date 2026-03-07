package com.shopkart.user.internalagent;

import com.shopkart.user.util.AuthUtil;
import org.springframework.stereotype.Component;

@Component
public class UserAgent {

    public Long getCurrentUserId() {
        return AuthUtil.getUserIdFromJwt();
    }
}
