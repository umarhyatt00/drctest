package com.yeel.drc.api;

import com.yeel.drc.model.phonecontacts.ContactPostModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIInterface {


    @Headers({"Content-Type:text/xml"})
    @POST("{apiName}")
    Call<CommonResponse> apiCall(
            @Path("apiName") String apiName
    );





    @Multipart
    @POST("saveKYCDetails/")
    Call<CommonResponse> saveKYCDetails(
            @Part MultipartBody.Part idImage,
            @Part MultipartBody.Part selfieImage,
            @Part MultipartBody.Part signatureImage,
            @Part("user_id") RequestBody user_id
    );

    @Multipart
    @POST("saveProfileImage/")
    Call<CommonResponse> saveProfileImage(
            @Part MultipartBody.Part profileImage
    );


    @Multipart
    @POST("updateRemitterKYC/")
    Call<CommonResponse> uploadBeneficiaryIDImage(
            @Part MultipartBody.Part beneficiaryIDImage
    );

    @Multipart
    @POST("addphoto/")
    Call<CommonResponse> addbankReciept(
            @Part MultipartBody.Part profileImage
           /* @Part("user_id") RequestBody user_id,
            @Part("req_id") RequestBody req_id*/
    );

    @Multipart
    @POST("updateBeneficiaryNtransStatus/")
    Call<CommonResponse> addCashPickupReceiverDoc(
            @Part MultipartBody.Part IdImage,
            @Part MultipartBody.Part signatureImage
    );

    @Multipart
    @POST("updateBeneficiaryNtransStatus/")
    Call<CommonResponse> addCashOutReceiverDoc(
            @Part MultipartBody.Part signatureImage
    );

    @POST("checkUserContacts/")
    Call<CommonResponse> syncContact(@Body ContactPostModel postModel);

}
