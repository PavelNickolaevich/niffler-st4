package guru.qa.niffler.api.spend;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.StatisticJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Date;
import java.util.List;

public interface SpendApi {

    @POST("/addSpend")
    Call<SpendJson> addSpend(@Body SpendJson spend);


    @GET("/spends")
    Call<List<SpendJson>> getSpends(@Query("username") String username,
                                    @Query("filterCurrency") CurrencyValues filterCurrency,
                                    @Query("from") Date from,
                                    @Query("to") Date to);

    @GET("/statistic")
    Call<List<StatisticJson>> getStatistic(@Query("username") String username,
                                           @Query("userCurrency") CurrencyValues userCurrency,
                                           @Query("filterCurrency") CurrencyValues filterCurrency,
                                           @Query("from") Date from,
                                           @Query("to") Date to);

    @PATCH("/editSpend")
    Call<SpendJson> editSpend(@Body SpendJson spend);

    @DELETE("/deleteSpends")
    void deleteSpends(@Query("username") String username,
                      @Query("ids") List<String> ids);
}
