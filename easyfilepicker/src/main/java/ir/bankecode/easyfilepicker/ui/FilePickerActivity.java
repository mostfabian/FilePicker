package ir.bankecode.easyfilepicker.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ir.bankecode.easyfilepicker.R;
import ir.bankecode.easyfilepicker.filter.CompositeFilter;
import ir.bankecode.easyfilepicker.filter.PatternFilter;
import ir.bankecode.easyfilepicker.utils.FileUtils;

public class FilePickerActivity extends AppCompatActivity implements DirectoryFragment.FileClickListener {

    public static final String ARG_TOOLBAR_LAYOUT_RES = "arg_toolbar_layout_res";
    public static final String ARG_TOOLBAR_ID_RES = "arg_toolbar_id_res";
    public static final String ARG_TOOLBAR_TITLE_COLOR = "arg_toolbar_title_color";
    public static final String ARG_TOOLBAR_SUBTITLE_COLOR = "arg_toolbar_subtitle_color";
    public static final String ARG_TOOLBAR_BACKGROUND_COLOR = "arg_toolbar_background_color";
    public static final String ARG_TOOLBAR_ICON_COLOR = "arg_toolbar_icon_color";

    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_CURRENT_PATH = "arg_current_path";

    public static final String ARG_FILTER = "arg_filter";
    public static final String ARG_CLOSEABLE = "arg_closeable";
    public static final String ARG_HIDE_EMPTY_DIRS = "arg_hide_empty_dirs";
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_LANGUAGE_CODE = "arg_language_code";

    public static final String STATE_START_PATH = "state_start_path";
    private static final String STATE_CURRENT_PATH = "state_current_path";

    public static final String RESULT_FILE_PATH = "result_file_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    public enum MimeType {
        MIME_TYPE_IMAGE,
        MIME_TYPE_VIDEO,
        MIME_TYPE_AUDIO,
    }

    private Toolbar mToolbar;
    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;
    private CharSequence mTitle;
    private @LayoutRes int mToolbarLayoutRes;
    private @IdRes int mToolbarIdRes;
    private @ColorRes int mToolbarTitleColor;
    private @ColorRes int mToolbarSubtitleColor;
    private @ColorRes int mToolbarBackgroundColor;
    private @ColorRes int mToolbarIconColor;
    private Locale mStartLocale;
    private String mLanguageCode;

    private Boolean mCloseable = true;
    private Boolean mHideEmptyDirs = false;

    private CompositeFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments(savedInstanceState);
        setContentView(R.layout.activity_file_picker);
        initViews();
        initToolbar();
        initBackStackState();
        initFragment();
        updateLocale();
    }

    private void initArguments(Bundle savedInstanceState) {
        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);
            if (filter instanceof Pattern) {
                ArrayList<FileFilter> filters = new ArrayList<>();
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }
        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
            updateTitle();
        } else {
            if (getIntent().hasExtra(ARG_START_PATH)) {
                mStartPath = getIntent().getStringExtra(ARG_START_PATH);
                mCurrentPath = mStartPath;
            }
            if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
                String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);
                if (currentPath.startsWith(mStartPath)) {
                    mCurrentPath = currentPath;
                }
            }
        }
        if (getIntent().hasExtra(ARG_TITLE)) {
            mTitle = getIntent().getCharSequenceExtra(ARG_TITLE);
        }
        if (getIntent().hasExtra(ARG_LANGUAGE_CODE)) {
            mLanguageCode = getIntent().getStringExtra(ARG_LANGUAGE_CODE);
        }
        if (getIntent().hasExtra(ARG_CLOSEABLE)) {
            mCloseable = getIntent().getBooleanExtra(ARG_CLOSEABLE, true);
        }
        if (getIntent().hasExtra(ARG_HIDE_EMPTY_DIRS)) {
            mHideEmptyDirs = getIntent().getBooleanExtra(ARG_HIDE_EMPTY_DIRS, true);
        }
        if (getIntent().hasExtra(ARG_TOOLBAR_TITLE_COLOR)) {
            mToolbarTitleColor = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_TITLE_COLOR, 0);
        }
        if (getIntent().hasExtra(ARG_TOOLBAR_SUBTITLE_COLOR)) {
            mToolbarSubtitleColor = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_SUBTITLE_COLOR, 0);
        }
        if (getIntent().hasExtra(ARG_TOOLBAR_BACKGROUND_COLOR)) {
            mToolbarBackgroundColor = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_BACKGROUND_COLOR, 0);
        }
        if (getIntent().hasExtra(ARG_TOOLBAR_ICON_COLOR)) {
            mToolbarIconColor = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_ICON_COLOR, 0);
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (mToolbarTitleColor != 0) {
            mToolbar.setTitleTextColor(getResources().getColor(mToolbarTitleColor));
        }
        if (mToolbarSubtitleColor != 0) {
            mToolbar.setSubtitleTextColor(getResources().getColor(mToolbarSubtitleColor));
        }
        if (mToolbarBackgroundColor != 0) {
            mToolbar.setBackgroundColor(getResources().getColor(mToolbarBackgroundColor));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        try {
            Field f;
            if (TextUtils.isEmpty(mTitle)) {
                f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            } else {
                f = mToolbar.getClass().getDeclaredField("mSubtitleTextView");
            }
            f.setAccessible(true);
            TextView textView = (TextView) f.get(mToolbar);
            textView.setEllipsize(TextUtils.TruncateAt.START);
        } catch (Exception ignored) {}
        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }
        updateTitle();
    }

    private void initViews() {
        if (getIntent().hasExtra(FilePickerActivity.ARG_TOOLBAR_LAYOUT_RES) && getIntent().hasExtra(FilePickerActivity.ARG_TOOLBAR_ID_RES)) {
            @LayoutRes int toolbarLayoutRes = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_LAYOUT_RES, 0);
            View toolbarLayout = LayoutInflater.from(this).inflate(toolbarLayoutRes, null, false);
            @IdRes int toolbarIdRes = getIntent().getIntExtra(FilePickerActivity.ARG_TOOLBAR_ID_RES, 0);
            mToolbar = toolbarLayout.findViewById(toolbarIdRes);
            ViewFlipper viewFlipper = findViewById(R.id.viewFlipperToolbar);
            viewFlipper.addView(mToolbar);
            viewFlipper.startFlipping();
            viewFlipper.setVisibility(View.VISIBLE);
        } else {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(mCurrentPath, mFilter, mHideEmptyDirs))
                .addToBackStack(null)
                .commit();
    }

    private void initBackStackState() {
        String pathToAdd = mCurrentPath;
        ArrayList<String> separatedPaths = new ArrayList<>();
        while (!pathToAdd.equals(mStartPath)) {
            pathToAdd = FileUtils.cutLastSegmentOfPath(pathToAdd);
            separatedPaths.add(pathToAdd);
        }
        Collections.reverse(separatedPaths);
        for (String path : separatedPaths) {
            addFragmentToBackStack(path);
        }
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            String titlePath = mCurrentPath.isEmpty() ? "Storage" : mCurrentPath
                    .replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "Storage")
                    .replace("/storage/emulated", "Storage");
            if (TextUtils.isEmpty(mTitle)) {
                getSupportActionBar().setTitle(titlePath);
            } else {
                getSupportActionBar().setSubtitle(titlePath);
            }
        }
    }

    private void updateLocale() {
        if (mLanguageCode != null) {
            mStartLocale = getResources().getConfiguration().locale;
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration conf = getResources().getConfiguration();
            conf.setLocale(new Locale(mLanguageCode.toLowerCase()));
            getResources().updateConfiguration(conf, dm);
        }
    }

    private void resetLocale() {
        if (mStartLocale != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            Configuration conf = getResources().getConfiguration();
            conf.setLocale(mStartLocale);
            getResources().updateConfiguration(conf, dm);
        }
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(path, mFilter, mHideEmptyDirs))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItemClose = menu.findItem(R.id.action_close);
        if (mCloseable && mToolbarIconColor != 0) {
            Drawable drawable = menuItemClose.getIcon();
            drawable.setColorFilter(getResources().getColor(mToolbarIconColor), PorterDuff.Mode.MULTIPLY);
            menuItemClose.setIcon(drawable);
        }
        menuItemClose.setVisible(mCloseable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (menuItem.getItemId() == R.id.action_close) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (!mCurrentPath.equals(mStartPath)) {
            fm.popBackStack();
            mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
            updateTitle();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetLocale();
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            mCurrentPath = clickedFile.getPath();
            if (mCurrentPath.equals("/storage/emulated"))
                mCurrentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            addFragmentToBackStack(mCurrentPath);
            updateTitle();
        } else {
            setResultAndFinish(clickedFile.getPath());
        }
    }

    private void setResultAndFinish(String filePath) {
        Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        setResult(RESULT_OK, data);
        finish();
    }
}
