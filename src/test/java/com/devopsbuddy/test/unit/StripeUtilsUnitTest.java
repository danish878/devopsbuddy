package com.devopsbuddy.test.unit;

import com.devopsbuddy.test.integration.StripeIntegrationTest;
import com.devopsbuddy.utils.StripeUtils;
import com.devopsbuddy.web.domain.frontend.ProAccountPayload;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;


public class StripeUtilsUnitTest {

    @Test
    public void createStripeTokenParamsFromUserPayload() {

        ProAccountPayload payload = new ProAccountPayload();
        String cardNumber = StripeIntegrationTest.TEST_CC_NUMBER;
        payload.setCardNumber(cardNumber);
        String cardCode = StripeIntegrationTest.TEST_CC_CVC_NBR;
        payload.setCardCode(cardCode);
        String cardMonth = String.valueOf(StripeIntegrationTest.TEST_CC_EXP_MONTH);
        payload.setCardMonth(cardMonth);
        String cardYear = String.valueOf(LocalDate.now(Clock.systemUTC()).getYear() + 1);
        payload.setCardYear(cardYear);

        Map<String, Object> tokenParams = StripeUtils.extractTokenParamsFromSignupPayload(payload);
        Map<String, Object> cardParams = (Map<String, Object>) tokenParams.get(StripeUtils.STRIPE_CARD_KEY);

        Assert.assertEquals(cardNumber, cardParams.get(StripeUtils.STRIPE_CARD_NUMBER_KEY));
        Assert.assertEquals(cardMonth, String.valueOf(cardParams.get(StripeUtils.STRIPE_EXPIRY_MONTH_KEY)));
        Assert.assertEquals(cardYear, String.valueOf(cardParams.get(StripeUtils.STRIPE_EXPIRY_YEAR_KEY)));
        Assert.assertEquals(cardCode, cardParams.get(StripeUtils.STRIPE_CVC_KEY));
    }

}
