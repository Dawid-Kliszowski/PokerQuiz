package pl.pokerquiz.pokerquiz.datamodel.rest;


import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("small_image_url")
    private String mSmallImageUrl;

    @SerializedName("big_image_url")
    private String mBigImageUrl;

    @SerializedName("date_changed")
    private long mDateChanged;

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getSmallImageUrl() {
        return mSmallImageUrl;
    }

    public String getBigImageUrl() {
        return mBigImageUrl;
    }
}
