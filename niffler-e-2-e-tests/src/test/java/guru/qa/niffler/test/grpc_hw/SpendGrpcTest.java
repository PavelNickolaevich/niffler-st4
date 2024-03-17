package guru.qa.niffler.test.grpc_hw;

import com.google.protobuf.Empty;
import guru.qa.grpc.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
public class SpendGrpcTest extends BaseGrpcTest {

    private static String id = "";

    @Test
    @Order(1)
    void addSpendTest() {
        SpendJson candidate = SpendJson.newBuilder()
                .setSpendDate(convertToGoogleDate(new Date()))
                .setCategory("Обучение")
                .setUsername("duck")
                .setCurrency(CurrencyValues.EUR)
                .setAmount(666)
                .setDescription("Judgment Day")
                .build();

        SpendJson saved = blockingStub.addSpend(AddSpendRequest.newBuilder()
                .setSpend(candidate).
                        build());

        id = saved.getId();

        assertAll(
                "SpendJson fields were saved",
                () -> assertEquals(666, saved.getAmount()),
                () -> assertEquals("Обучение", saved.getCategory()),
                () -> assertEquals("Judgment Day", saved.getDescription()),
                () -> assertEquals("duck", saved.getUsername()),
                () -> assertEquals(CurrencyValues.EUR, saved.getCurrency())
        );
    }

    @Test
    @Order(2)
    void editSpendsTest() {
        guru.qa.grpc.niffler.grpc.SpendJson spendJson = guru.qa.grpc.niffler.grpc.SpendJson.newBuilder()
                .setId(id)
                .setSpendDate(convertToGoogleDate(new Date()))
                .setCategory("Угар")
                .setAmount(777)
                .setCurrency(CurrencyValues.EUR)
                .setDescription("Красивая жизнь")
                .setUsername("duck")
                .build();

        SpendJson result = blockingStub.editSpend(EditSpendRequest.newBuilder()
                .setSpend(spendJson)
                .build());

        assertAll(
                "SpendJson edits fields",
                () -> assertEquals(777, result.getAmount()),
                () -> assertEquals("Угар", result.getCategory()),
                () -> assertEquals("Красивая жизнь", result.getDescription()),
                () -> assertEquals("duck", result.getUsername()),
                () -> assertEquals(CurrencyValues.EUR, result.getCurrency())
        );
    }

    @Test
    @Order(3)
    void getSpendsTest() {

        SpendRequest spendRequest = SpendRequest.newBuilder()
                .setUsername("duck")
                .setFilterCurrency(CurrencyValues.EUR)
                .setFrom(convertToGoogleDate(new Date()))
                .setTo(convertToGoogleDate(new Date()))
                .build();

        SpendsResponse result = blockingStub.getSpends(spendRequest);

        assertAll(
                () -> assertEquals("duck", result.getSpendJsonList().get(0).getUsername()),
                () -> assertEquals(CurrencyValues.EUR, result.getSpendJsonList().get(0).getCurrency())
        );
    }

    @Test
    @Order(4)
    void getStatisticTest() {

        StatisticRequest statisticRequest = StatisticRequest.newBuilder()
                .setUsername("duck")
                .setUserCurrency(CurrencyValues.EUR)
                .setFilterCurrency(CurrencyValues.EUR)
                .setDateFrom(convertToGoogleDate(new Date()))
                .setDateTo(convertToGoogleDate(new Date()))
                .build();

        StatisticResponse result = blockingStub.getStatistic(statisticRequest);

        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getStatisticJsonList()
                        .stream()
                        .anyMatch(currency -> currency.getCurrency().equals(CurrencyValues.EUR))
                ));
    }

    @Test
    @Order(5)
    void deleteSpendsTest() {

        DeleteSpendRequest deleteSpendsRequest = DeleteSpendRequest.newBuilder()
                .setUsername("duck")
                .addIds(id)
                .build();

        Empty empty = blockingStub.deleteSpend(deleteSpendsRequest);
        assertEquals(empty.getSerializedSize(), 0);
    }


    private static com.google.type.Date convertToGoogleDate(java.util.Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        com.google.type.Date.Builder dateBuilder = com.google.type.Date.newBuilder();
        dateBuilder.setDay(cal.get(Calendar.DAY_OF_MONTH));
        // Months start at 0, not 1
        dateBuilder.setMonth(cal.get(Calendar.MONTH) + 1);
        dateBuilder.setYear(cal.get(Calendar.YEAR));
        return dateBuilder.build();
    }

}

