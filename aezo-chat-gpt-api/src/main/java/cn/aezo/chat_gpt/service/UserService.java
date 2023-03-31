package cn.aezo.chat_gpt.service;

import cn.aezo.chat_gpt.util.Result;

import java.util.List;
import java.util.Map;

public interface UserService {
    String checkAndGetUserId(String username, String password);

    List<String> getPermissionList(Object userId);

    List<String> getRoleList(Object userId);

    Map<String, Object> getUserInfo(Object userId);

    Map<String, Object> registerByOpenid(String openid, String inviterUserId);

    Result loginByWXMP(Map<String, Object> params);

    String encPassword(String password);

    boolean checkPassword(String password, String passwordPure);
}
