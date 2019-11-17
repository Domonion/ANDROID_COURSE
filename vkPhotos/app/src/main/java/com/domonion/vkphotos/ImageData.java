package com.domonion.vkphotos;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageData implements Parcelable {
    int id;
    String URL;
    String description;

    ImageData(int id, String _URL, String _description) {
        this.id = id;
        URL = _URL;
        description = _description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(URL);
        dest.writeString(description);
    }

    private ImageData(Parcel parcel) {
        id = parcel.readInt();
        URL = parcel.readString();
        description = parcel.readString();
    }

    public static final Parcelable.Creator<ImageData> CREATOR = new Parcelable.Creator<ImageData>() {
        // распаковываем объект из Parcel
        public ImageData createFromParcel(Parcel in) {
            return new ImageData(in);
        }

        public ImageData[] newArray(int size) {
            return new ImageData[size];
        }
    };
}
