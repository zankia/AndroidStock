package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockJSON;
import fr.zankia.stock.model.Product;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.activity_main);
        StockJSON.getInstance();
    }

    public void setListActivity(View view) {
        startActivity(new Intent(this, ManageActivity.class));
    }
    public void setDisplayActivity(View view) {
        startActivity(new Intent(this, GridActivity.class));
    }

    public void sendMail(View view) {
        StringBuilder csvString = new StringBuilder(getString(R.string.csvHeader));
        StockJSON stock = StockJSON.getInstance();
        int i = 2;
        for (String categoryName : stock.getCategoryNames()) {
            for (Product product : stock.getCategory(categoryName).getProducts()) {
                csvString.append("\n\"")
                        .append(product.getName())
                        .append("\";\"")
                        .append(product.getQuantity())
                        .append("\";\"")
                        .append(String.format(Locale.getDefault(), "%.2f",product.getPrice()))
                        .append("\";\"=B").append(i).append("*C").append(i).append("\"");
                ++i;
            }
        }
        csvString.append("\n\"\";\"\";\"Total\";\"=")
                .append(getString(R.string.sumFunction))
                .append("(D2:D").append(i-1).append(")\"");

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getString(R.string.csvFile));
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fs = new FileOutputStream(file);
            fs.write(csvString.toString().getBytes());
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sendSubject));
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        sendIntent.setType("plain/text");
        startActivity(sendIntent);
    }
}
