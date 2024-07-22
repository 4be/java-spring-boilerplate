package com.vacant.account.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;

@UtilityClass
public class RedisUtil {

    private final String REDIS_PREFIX = "msaccount";
    public final String ACCOUNT_INFO_TYPE = "accountinfo";

    public String constructApiBankingRedisKey(String type, String clientId, String ciamId) {
        return String.format("%s-%s-%s-%s", REDIS_PREFIX, type, clientId, ciamId);
    }
}
