package ir.bankecode.filepicker;

import androidx.appcompat.app.AppCompatActivity;
import ir.bankecode.easyfilepicker.EasyFilePicker;
import ir.bankecode.easyfilepicker.ui.FilePickerActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 65535 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
            setTitle(getResources().getString(R.string.app_name));
        }
    }

    public void pick(View view) {
        new EasyFilePicker()
                .withActivity(this)
                .withLanguageCode("fa")
                .withRequestCode(65535)
                //.addExtensions("pdf", "zip", "txt")
                .addMimeType(FilePickerActivity.MimeType.MIME_TYPE_IMAGE)
                //.addMimeType(FilePickerActivity.MimeType.MIME_TYPE_VIDEO)
                //.ignoreExtensions("zip", "txt")
                //.withFilter(Pattern.compile(".*\\.(jpg|zip)"))
                .withTitle("انتخاب فایل")
                .withCloseMenu(false)
                .withHideEmptyDirs(false)
                //.withCloseMenu(true, R.color.colorAccent)
                //.withToolbarTitleColor(R.color.colorAccent)
                //.withToolbarSubtitleColor(R.color.colorAccent)
                //.withToolbarBackgroundColor(R.color.colorPrimaryDark)
                //.withToolbar(R.layout.easy_file_picker_toolbar, R.id.toolbar)
                .start();
    }
}
