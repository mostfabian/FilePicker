package ir.bankecode.easyfilepicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import ir.bankecode.easyfilepicker.filter.CompositeFilter;
import ir.bankecode.easyfilepicker.filter.HiddenFilter;
import ir.bankecode.easyfilepicker.filter.PatternFilter;
import ir.bankecode.easyfilepicker.ui.FilePickerActivity;

public class EasyFilePicker {

    private Activity mActivity;
    private Fragment mFragment;
    private androidx.fragment.app.Fragment mSupportFragment;

    private @LayoutRes int mToolbarLayoutRes;
    private @IdRes int mToolbarIdRes;
    private @ColorRes int mToolbarTitleColor;
    private @ColorRes int mToolbarSubtitleColor;
    private @ColorRes int mToolbarBackgroundColor;
    private @ColorRes int mToolbarIconColor;

    private Class<? extends FilePickerActivity> mFilePickerClass = FilePickerActivity.class;

    private Integer mRequestCode;
    private Pattern mFileFilter;
    private Boolean mDirectoriesFilter = false;
    private String mRootPath;
    private String mCurrentPath;
    private Boolean mShowHidden = false;
    private Boolean mCloseable = true;
    private CharSequence mTitle;

    public EasyFilePicker() {}

    public EasyFilePicker withActivity(Activity activity) {
        if (mSupportFragment != null || mFragment != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }
        mActivity = activity;
        return this;
    }

    public EasyFilePicker withFragment(Fragment fragment) {
        if (mSupportFragment != null || mActivity != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }
        mFragment = fragment;
        return this;
    }

    public EasyFilePicker withSupportFragment(androidx.fragment.app.Fragment fragment) {
        if (mActivity != null || mFragment != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }
        mSupportFragment = fragment;
        return this;
    }

    public EasyFilePicker withRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    public EasyFilePicker withFilter(Pattern pattern) {
        mFileFilter = pattern;
        return this;
    }

    public EasyFilePicker withFilterDirectories(boolean directoriesFilter) {
        mDirectoriesFilter = directoriesFilter;
        return this;
    }

    public EasyFilePicker withRootPath(String rootPath) {
        mRootPath = rootPath;
        return this;
    }

    public EasyFilePicker withPath(String path) {
        mCurrentPath = path;
        return this;
    }

    public EasyFilePicker withHiddenFiles(boolean show) {
        mShowHidden = show;
        return this;
    }

    public EasyFilePicker withCloseMenu(boolean closeable) {
        mCloseable = closeable;
        return this;
    }

    public EasyFilePicker withCloseMenu(boolean closeable, @ColorRes int toolbarIconColor) {
        mCloseable = closeable;
        mToolbarIconColor = toolbarIconColor;
        return this;
    }

    public EasyFilePicker withTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    public EasyFilePicker withCustomActivity(Class<? extends FilePickerActivity> customActivityClass) {
        mFilePickerClass = customActivityClass;
        return this;
    }

    public EasyFilePicker withToolbarTitleColor(@ColorRes int toolbarTitleColor) {
        mToolbarTitleColor = toolbarTitleColor;
        return this;
    }

    public EasyFilePicker withToolbarSubtitleColor(@ColorRes int toolbarSubtitleColor) {
        mToolbarSubtitleColor = toolbarSubtitleColor;
        return this;
    }

    public EasyFilePicker withToolbarBackgroundColor(@ColorRes int toolbarBackgroundColor) {
        mToolbarBackgroundColor = toolbarBackgroundColor;
        return this;
    }

    public EasyFilePicker withToolbar(@LayoutRes int toolbarLayoutRes, @IdRes int toolbarIdRes) {
        mToolbarLayoutRes = toolbarLayoutRes;
        mToolbarIdRes = toolbarIdRes;
        return this;
    }

    private Activity getActivity() {
        if (mActivity != null) {
            return mActivity;
        } else if (mFragment != null) {
            return mFragment.getActivity();
        } else if (mSupportFragment != null) {
            return mSupportFragment.getActivity();
        }
        return null;
    }

    public CompositeFilter getFilter() {
        ArrayList<FileFilter> filters = new ArrayList<>();
        if (!mShowHidden) {
            filters.add(new HiddenFilter());
        }
        if (mFileFilter != null) {
            filters.add(new PatternFilter(mFileFilter, mDirectoriesFilter));
        }
        return new CompositeFilter(filters);
    }

    public Intent getIntent() {
        CompositeFilter filter = getFilter();
        Intent intent = new Intent(getActivity(), mFilePickerClass);
        intent.putExtra(FilePickerActivity.ARG_FILTER, filter);
        intent.putExtra(FilePickerActivity.ARG_CLOSEABLE, mCloseable);
        if (mRootPath != null) {
            intent.putExtra(FilePickerActivity.ARG_START_PATH, mRootPath);
        }
        if (mCurrentPath != null) {
            intent.putExtra(FilePickerActivity.ARG_CURRENT_PATH, mCurrentPath);
        }
        if (mTitle != null) {
            intent.putExtra(FilePickerActivity.ARG_TITLE, mTitle);
        }
        if (mToolbarLayoutRes != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_LAYOUT_RES, mToolbarLayoutRes);
        }
        if (mToolbarIdRes != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_ID_RES, mToolbarIdRes);
        }
        if (mToolbarTitleColor != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_TITLE_COLOR, mToolbarTitleColor);
        }
        if (mToolbarSubtitleColor != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_SUBTITLE_COLOR, mToolbarSubtitleColor);
        }
        if (mToolbarBackgroundColor != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_BACKGROUND_COLOR, mToolbarBackgroundColor);
        }
        if (mToolbarIconColor != 0) {
            intent.putExtra(FilePickerActivity.ARG_TOOLBAR_ICON_COLOR, mToolbarIconColor);
        }
        return intent;
    }

    public void start() {
        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity/Fragment by calling withActivity/withFragment/withSupportFragment method");
        }
        if (mRequestCode == null) {
            throw new RuntimeException("You must pass request code by calling withRequestCode method");
        }
        Intent intent = getIntent();
        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }
}
