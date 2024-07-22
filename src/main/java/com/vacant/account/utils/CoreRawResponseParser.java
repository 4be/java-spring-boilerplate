package com.vacant.account.utils;

import com.vacant.account.constants.CoreConstant;
import com.vacant.account.enums.ResponseCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class CoreRawResponseParser {

    public Map<String, String> parse(String rawResponse) {
        log.info("core raw response: {}", rawResponse);

        Map<String, String> map = new LinkedHashMap<>();
        if(CoreConstant.CORE_ERROR_RESPONSE.equals(rawResponse)){
            map.put("error", rawResponse);
            map.put(CoreConstant.RESPONSE_CODE_KEY, ResponseCode.INTERNAL_SERVER_ERROR.getCode());
        }
        String[] rows = rawResponse.split(",");
        String responseHeader = null;
        boolean first = true;
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            if (first) {
                first = false;
                responseHeader = row;
            } else {
                String[] keyVal = row.split("=");
                if(keyVal.length==2) {
                    map.put(keyVal[0], keyVal[1]);
                } else {
                    map.put(String.valueOf(i),keyVal[0]);
                }
            }
        }
        String responseCode = map.get(CoreConstant.RESPONSE_CODE_KEY);
        try {
            if (!"00".equals(responseCode)) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (!CoreConstant.RESPONSE_CODE_KEY.equals(entry.getKey())) {
                        map.put("error", entry.getValue());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put(CoreConstant.RESPONSE_CODE_KEY, "99");
            map.put("error", e.getMessage());
        }

        map.put("responseHeader", responseHeader);
        log.info("core raw response parsed: {}", map);
        return map;
    }
}


