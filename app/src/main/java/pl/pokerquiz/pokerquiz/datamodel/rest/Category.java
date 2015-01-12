package pl.pokerquiz.pokerquiz.datamodel.rest;


import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

public class Category {
    @SerializedName("id")
    @DatabaseField(columnName = "id", id = true)
    private long mId;

    @SerializedName("name")
    @DatabaseField(columnName = "name")
    private String mName;

    @SerializedName("description")
    @DatabaseField(columnName = "description")
    private String mDescription;

    @SerializedName("small_image_url")
    @DatabaseField(columnName = "small_image_url")
    private String mSmallImageUrl;

    @SerializedName("big_image_url")
    @DatabaseField(columnName = "big_image_url")
    private String mBigImageUrl;

    @SerializedName("date_changed")
    @DatabaseField(columnName = "date_changed")
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
