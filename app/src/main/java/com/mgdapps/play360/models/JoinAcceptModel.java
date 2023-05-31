package com.mgdapps.play360.models;

import android.os.Parcel;
import android.os.Parcelable;

public class JoinAcceptModel implements Parcelable {

    private String UID = "";
    private String NAME = "";
    private String STATUS = "";
    private int kills = 0;
    private String profilePic = "";

    protected JoinAcceptModel(Parcel in) {
        UID = in.readString();
        NAME = in.readString();
        STATUS = in.readString();
        kills = in.readInt();
        profilePic = in.readString();
    }

    public JoinAcceptModel() {

    }

    public static final Creator<JoinAcceptModel> CREATOR = new Creator<JoinAcceptModel>() {
        @Override
        public JoinAcceptModel createFromParcel(Parcel in) {
            return new JoinAcceptModel(in);
        }

        @Override
        public JoinAcceptModel[] newArray(int size) {
            return new JoinAcceptModel[size];
        }
    };

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        kills = kills;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UID);
        dest.writeString(NAME);
        dest.writeString(STATUS);
        dest.writeInt(kills);
        dest.writeString(profilePic);
    }
}
