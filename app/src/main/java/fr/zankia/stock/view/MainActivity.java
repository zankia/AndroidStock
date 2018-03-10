package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockJSON;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StockJSON.getInstance();
    }

    public void setListActivity(View view) {
        startActivity(new Intent(this, ManageActivity.class));
    }
    public void setDisplayActivity(View view) {
        startActivity(new Intent(this, GridActivity.class));
    }

}
