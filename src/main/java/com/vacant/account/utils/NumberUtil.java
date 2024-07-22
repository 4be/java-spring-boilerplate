package com.vacant.account.utils;

import com.vacant.account.model.BalanceAmount;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@UtilityClass
public class NumberUtil {
    private  final Random rand = new Random();
    public BigDecimal parseCoreAmount(String amount) {
        if (ObjectUtils.isEmpty(amount)) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(amount.replaceAll(",", ""));
    }

    public BalanceAmount setBalanceAmount(BigDecimal amount, String currency) {
        return BalanceAmount.builder()
                .value(amount.setScale(2, RoundingMode.HALF_UP).toPlainString())
                .currency(currency)
                .build();
    }

    public String randomNumber(int length) {
        return String.valueOf(rand.nextInt((int) Math.pow(10,length)));
    }

    public String getNumericValueFromString(String inputString) {
        return inputString.replaceAll("[^0-9]", "");
    }
}
