package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface SpendApi {

    @POST("/addSpend")
    Call<SpendJson> addSpend(@Body SpendJson spend);

    @POST("/category")
    Call<CategoryJson> addCategory(@Body Map<String, String> category);
}
