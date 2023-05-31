package com.mgdapps.play360.models;

import java.io.Serializable;

public class TournamentsModel implements Serializable {

    String tournamentImg,tournamentTitle,tournamentType
            ,tournamentVersion,tournamentMap,tournamentKeys;

    long tournamentTime,tournamentTotalJoined,myCoins,tournamentEntryFee,tournamentPerKil,tournamentWinPrize;
    public String getTournamentKeys() {
        return tournamentKeys;
    }

    public long getMyCoins() {
        return myCoins;
    }

    public void setMyCoins(long myCoins) {
        this.myCoins = myCoins;
    }

    public void setTournamentKeys(String tournamentKeys) {
        this.tournamentKeys = tournamentKeys;
    }

    public String getTournamentImg() {
        return tournamentImg;
    }

    public void setTournamentImg(String tournamentImg) {
        this.tournamentImg = tournamentImg;
    }

    public String getTournamentTitle() {
        return tournamentTitle;
    }

    public void setTournamentTitle(String tournamentTitle) {
        this.tournamentTitle = tournamentTitle;
    }

    public long getTournamentTime() {
        return tournamentTime;
    }

    public void setTournamentTime(long tournamentTime) {
        this.tournamentTime = tournamentTime;
    }

    public long getTournamentEntryFee() {
        return tournamentEntryFee;
    }

    public void setTournamentEntryFee(long tournamentEntryFee) {
        this.tournamentEntryFee = tournamentEntryFee;
    }

    public long getTournamentPerKil() {
        return tournamentPerKil;
    }

    public void setTournamentPerKil(long tournamentPerKil) {
        this.tournamentPerKil = tournamentPerKil;
    }

    public long getTournamentWinPrize() {
        return tournamentWinPrize;
    }

    public void setTournamentWinPrize(long tournamentWinPrize) {
        this.tournamentWinPrize = tournamentWinPrize;
    }

    public String getTournamentType() {
        return tournamentType;
    }

    public void setTournamentType(String tournamentType) {
        this.tournamentType = tournamentType;
    }

    public String getTournamentVersion() {
        return tournamentVersion;
    }

    public void setTournamentVersion(String tournamentVersion) {
        this.tournamentVersion = tournamentVersion;
    }

    public String getTournamentMap() {
        return tournamentMap;
    }

    public void setTournamentMap(String tournamentMap) {
        this.tournamentMap = tournamentMap;
    }

    public long getTournamentTotalJoined() {
        return tournamentTotalJoined;
    }

    public void setTournamentTotalJoined(long tournamentTotalJoined) {
        this.tournamentTotalJoined = tournamentTotalJoined;
    }
}
