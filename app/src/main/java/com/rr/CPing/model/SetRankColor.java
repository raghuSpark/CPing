package com.rr.CPing.model;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.rr.CPing.R;

public class SetRankColor {

    private final Context context;

    public SetRankColor(Context context) {
        this.context = context;
    }

    public int getAtCoderColor(String level) {
        switch (level) {
            case "5 Dan":
                return ContextCompat.getColor(context, R.color.atCoderFiveDan);
            case "6 Dan":
                return ContextCompat.getColor(context, R.color.atCoderSixDan);
            case "7 Dan":
                return ContextCompat.getColor(context, R.color.atCoderSevenDan);
            case "8 Dan":
                return ContextCompat.getColor(context, R.color.atCoderEightDan);
            case "9 Dan":
                return ContextCompat.getColor(context, R.color.atCoderNineDan);
            case "10 Dan":
                return ContextCompat.getColor(context, R.color.atCoderTenDan);
            case "Legend":
                return ContextCompat.getColor(context, R.color.atCoderLegend);
            case "King":
                return ContextCompat.getColor(context, R.color.atCoderKing);
        }
        return -1;
    }

    public int getCodeChefColor(String stars) {
        switch (stars) {
            case "1★":
                return ContextCompat.getColor(context, R.color.oneStar);
            case "2★":
                return ContextCompat.getColor(context, R.color.twoStar);
            case "3★":
                return ContextCompat.getColor(context, R.color.threeStar);
            case "4★":
                return ContextCompat.getColor(context, R.color.fourStar);
            case "5★":
                return ContextCompat.getColor(context, R.color.fiveStar);
            case "6★":
                return ContextCompat.getColor(context, R.color.sixStar);
            case "7★":
                return ContextCompat.getColor(context, R.color.sevenStar);
        }
        return -1;
    }

    public int getCodeforcesColor(String rank) {
        switch (rank) {
            case "newbie":
                return ContextCompat.getColor(context, R.color.codeForcesNewbieColor);
            case "pupil":
                return ContextCompat.getColor(context, R.color.codeForcesPupilColor);
            case "specialist":
                return ContextCompat.getColor(context, R.color.codeForcesSpecialistColor);
            case "expert":
                return ContextCompat.getColor(context, R.color.codeForcesExpertColor);
            case "candidate master":
                return ContextCompat.getColor(context, R.color.codeForcesCandidateMasterColor);
            case "master":
            case "international master":
                return ContextCompat.getColor(context, R.color.codeForcesMasterColor);
            case "grandmaster":
            case "legendary grandmaster":
            case "international grandmaster":
                return ContextCompat.getColor(context, R.color.codeForcesGrandMasterColor);
        }
        return -1;
    }
}
