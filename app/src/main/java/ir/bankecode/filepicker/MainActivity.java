package ir.bankecode.filepicker;

import androidx.appcompat.app.AppCompatActivity;
import ir.bankecode.easyfilepicker.EasyFilePicker;
import ir.bankecode.easyfilepicker.ui.FilePickerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
