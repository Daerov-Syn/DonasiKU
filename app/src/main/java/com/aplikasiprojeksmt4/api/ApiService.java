package com.aplikasiprojeksmt4.api;

import com.aplikasiprojeksmt4.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Contoh endpoint untuk mengambil data user dari MySQL
    @GET("get_users.php")
    Call<List<User>> getUsers();
}
