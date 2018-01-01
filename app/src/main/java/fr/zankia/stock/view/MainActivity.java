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
        StockJSON.getInstance().load(getPreferences(MODE_PRIVATE));
    }

    public void setListActivity(View view) {
        startActivity(new Intent(this, ManageActivity.class));
    }
    public void setDisplayActivity(View view) {
        startActivity(new Intent(this, GridActivity.class));
    }

    public void showJSON(View view) {
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getString(R.string.jsonFile));
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setMessage(R.string.backupInstructions)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().equals(getString(R.string.load))) {
                            try {
                                FileInputStream fs = new FileInputStream(file);
                                BufferedReader br = new BufferedReader(new InputStreamReader(fs));
                                StringBuilder json = new StringBuilder();
                                String line;
                                while((line = br.readLine()) != null) {
                                    json.append(line);
                                }
                                StockJSON.getInstance().load(json.toString());
                                StockJSON.getInstance().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), R.string.loaded, Toast.LENGTH_SHORT).show();
                        } else if(editText.getText().toString().equals(getString(R.string.save))) {
                            try {
                                FileOutputStream fs = new FileOutputStream(file);
                                fs.write(getPreferences(MODE_PRIVATE).getString("data", "[]").getBytes());
                                fs.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
