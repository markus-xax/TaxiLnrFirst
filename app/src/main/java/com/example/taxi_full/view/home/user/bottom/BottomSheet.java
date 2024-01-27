package com.example.taxi_full.view.home.user.bottom;

import android.widget.EditText;

import com.example.taxi_full.API.model.RootOrderOne;

public interface BottomSheet {

    void EditTextLocked(EditText start, EditText finish);

    void loadHomeWorkText();

    void addHomeWorkBottom();

    void checkHomeWork();

    void setImageDollarClass(RootOrderOne rootOrderOne);

    void blockEditOrder();

    void unBlockEditOrder();

    void isStartOrder();
}
