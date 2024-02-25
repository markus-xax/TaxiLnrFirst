package com.example.taxi_full.ui.exit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.taxi_full.API.DBClass;
import com.example.taxi_full.API.DBHelper;
import com.example.taxi_full.R;
import com.example.taxi_full.databinding.FragmentExitBinding;
import com.example.taxi_full.ui.info.InfoViewModel;

public class ExitFragment extends Fragment {

    private FragmentExitBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        InfoViewModel infoViewModel =
                new ViewModelProvider(this).get(InfoViewModel.class);

        binding = FragmentExitBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView13;
        infoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ImageButton exit = root.findViewById(R.id.backExit);
        DBClass db = new DBClass();
        if(db.getDC(getContext()).equals("0"))
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.Home")));
        else
            exit.setOnClickListener(view -> startActivity(new Intent("com.example.taxi_full.HomeDriver")));

        Button exit_account = root.findViewById(R.id.exit_account);

        exit_account.setOnClickListener(view -> {
            DBHelper dbHelper = new DBHelper(root.getContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            Cursor cursor = database.query(DBHelper.TABLE_USER_VALUES, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.KEY_ACTIVE, 0);
                    contentValues.put(DBHelper.KEY_ACTIVE_SMS, 0);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues, DBHelper.KEY_DC + " = ?", new String[]{"1"});

                    contentValues.put(DBHelper.KEY_ACTIVE, 0);
                    contentValues.put(DBHelper.KEY_ACTIVE_SMS, 0);
                    database.update(DBHelper.TABLE_USER_VALUES, contentValues, DBHelper.KEY_DC + " = ?", new String[]{"0"});

                } while (cursor.moveToNext());
            }

            cursor.close();
            database.close();

            startActivity(new Intent("com.example.taxi_full.Driver_client"));
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
