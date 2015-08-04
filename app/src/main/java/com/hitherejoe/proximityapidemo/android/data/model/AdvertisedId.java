package com.hitherejoe.proximityapidemo.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AdvertisedId implements Parcelable {
    public Type type;
    public String id;

    public enum Type {
        TYPE_UNSPECIFIED("Unspecified"),
        EDDYSTONE("Eddystone"),
        IBEACON("iBeacon"),
        ALTBEACON("AltBeacon");

        private String string;

        Type(String string) {
            this.string = string;
        }

        public static Type fromString(String string) {
            if (string != null) {
                for (Type status : Type.values()) {
                    if (string.equalsIgnoreCase(status.string)) {
                        return status;
                    }
                }
            }
            return null;
        }

        public String getString() {
            return string;
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.id);
    }

    public AdvertisedId() { }

    protected AdvertisedId(Parcel in) {
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.id = in.readString();
    }

    public static final Parcelable.Creator<AdvertisedId> CREATOR = new Parcelable.Creator<AdvertisedId>() {
        public AdvertisedId createFromParcel(Parcel source) {
            return new AdvertisedId(source);
        }

        public AdvertisedId[] newArray(int size) {
            return new AdvertisedId[size];
        }
    };
}