package com.mgdapps.play360.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CreateMatchModel implements Parcelable {

    private String matchTitle = "";
    private int matchType = 0;
    private int matchMode = 0;
    private int matchMap = 0;
    private int matchServer = 0;
    private int matchAA = 0;
    private int matchShowPassword = 0;
    private Date matchTime = new Date();
    private boolean matchprivate = false;
    private String matchId = "";
    private int joinCode = 0;
    private String UID = "";
    private int Joined = 0;
    private String Password = "";
    private String RoomId = "";

    public CreateMatchModel() {

    }

    protected CreateMatchModel(Parcel in) {
        matchTitle = in.readString();
        matchType = in.readInt();
        matchMode = in.readInt();
        matchMap = in.readInt();
        matchServer = in.readInt();
        matchAA = in.readInt();
        matchShowPassword = in.readInt();
        long tmpDate = in.readLong();
        matchTime = tmpDate == -1 ? null : new Date(tmpDate);
        matchprivate = in.readByte() != 0;
        matchId = in.readString();
        joinCode = in.readInt();
        UID = in.readString();
        Joined = in.readInt();
        Password = in.readString();
        RoomId = in.readString();

    }

    public static final Creator<CreateMatchModel> CREATOR = new Creator<CreateMatchModel>() {
        @Override
        public CreateMatchModel createFromParcel(Parcel in) {
            return new CreateMatchModel(in);
        }

        @Override
        public CreateMatchModel[] newArray(int size) {
            return new CreateMatchModel[size];
        }
    };


    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getJoined() {
        return Joined;
    }

    public void setJoined(int joined) {
        this.Joined = joined;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(int joinCode) {
        this.joinCode = joinCode;
    }

    public String getMatchTitle() {
        return matchTitle;
    }

    public void setMatchTitle(String matchTitle) {
        this.matchTitle = matchTitle;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public int getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(int matchMode) {
        this.matchMode = matchMode;
    }

    public int getMatchMap() {
        return matchMap;
    }

    public void setMatchMap(int matchMap) {
        this.matchMap = matchMap;
    }

    public int getMatchServer() {
        return matchServer;
    }

    public void setMatchServer(int matchServer) {
        this.matchServer = matchServer;
    }

    public int getMatchAA() {
        return matchAA;
    }

    public void setMatchAA(int matchAA) {
        this.matchAA = matchAA;
    }

    public int getMatchShowPassword() {
        return matchShowPassword;
    }

    public void setMatchShowPassword(int matchShowPassword) {
        this.matchShowPassword = matchShowPassword;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public boolean isMatchprivate() {
        return matchprivate;
    }

    public void setMatchprivate(boolean matchprivate) {
        this.matchprivate = matchprivate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(matchTitle);
        dest.writeInt(matchType);
        dest.writeInt(matchMode);
        dest.writeInt(matchMap);
        dest.writeInt(matchServer);
        dest.writeInt(matchAA);
        dest.writeInt(matchShowPassword);
        dest.writeLong(matchTime != null ? matchTime.getTime() : -1);
        dest.writeByte((byte) (matchprivate ? 1 : 0));
        dest.writeString(matchId);
        dest.writeInt(joinCode);
        dest.writeString(UID);
        dest.writeInt(Joined);
        dest.writeString(Password);
        dest.writeString(RoomId);
    }
}
