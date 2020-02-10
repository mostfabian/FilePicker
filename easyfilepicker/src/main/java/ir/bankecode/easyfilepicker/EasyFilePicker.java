package ir.bankecode.easyfilepicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

import ir.bankecode.easyfilepicker.filter.CompositeFilter;
import ir.bankecode.easyfilepicker.filter.HiddenFilter;
import ir.bankecode.easyfilepicker.filter.PatternFilter;
import ir.bankecode.easyfilepicker.ui.FilePickerActivity;

public class EasyFilePicker {
    private Activity mActivity;
    private Fragment mFragment;
    private androidx.fragment.app.Fragment mSupportFragment;

    private Class<? extends FilePickerActivity> mFilePickerClass = FilePickerActivity.class;

    private Integer mRequestCode;
    private Pattern mFileFilter;
    private Boolean mDirectoriesFilter = false;
    private String mRootPath;
    private String mCurrentPath;
    private Boolean mShowHidden = false;
    private Boolean mCloseable = true;
    private CharSequence mTitle;

    public EasyFilePicker() {
    }


    /**
     * Specifies activity, which will be used to
     * start file picker
     */
    public EasyFilePicker withActivity(Activity activity) {
        if (mSupportFragment != null || mFragment != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }

        mActivity = activity;
        return this;
    }

    /**
     * Specifies fragment, which will be used to
     * start file picker
     */
    public EasyFilePicker withFragment(Fragment fragment) {
        if (mSupportFragment != null || mActivity != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }

        mFragment = fragment;
        return this;
    }

    /**
     * Specifies support fragment which will be used to
     * start file picker
     */
    public EasyFilePicker withSupportFragment(androidx.fragment.app.Fragment fragment) {
        if (mActivity != null || mFragment != null) {
            throw new RuntimeException("You must pass either Activity, Fragment or SupportFragment");
        }

        mSupportFragment = fragment;
        return this;
    }

    /**
     * Specifies request code that used in activity result
     *
     * @see <a href="https://developer.android.com/training/basics/intents/result.html">Getting a Result from an Activity</a>
     */
    public EasyFilePicker withRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }


    /**
     * Hides files that matched by specified regular expression.
     * Use {@link EasyFilePicker#withFilterDirectories withFilterDirectories} method
     * to enable directories filtering
     */
    public EasyFilePicker withFilter(Pattern pattern) {
        mFileFilter = pattern;
        return this;
    }

    /**
     * If directoriesFilter is true directories will also be affected by filter,
     * the default value of directories filter is false
     *
     * @see EasyFilePicker#withFilter
     */
    public EasyFilePicker withFilterDirectories(boolean directoriesFilter) {
        mDirectoriesFilter = directoriesFilter;
        return this;
    }

    /**
     * Specifies root directory for picker,
     * user can't go upper that specified path
     */
    public EasyFilePicker withRootPath(String rootPath) {
        mRootPath = rootPath;
        return this;
    }

    /**
     * Specifies start directory for picker,
     * which will be shown to user at the beginning
     */
    public EasyFilePicker withPath(String path) {
        mCurrentPath = path;
        return this;
    }

    /**
     * Show or hide hidden files in picker
     */
    public EasyFilePicker withHiddenFiles(boolean show) {
        mShowHidden = show;
        return this;
    }

    /**
     * Show or hide close menu in picker
     */
    public EasyFilePicker withCloseMenu(boolean closeable) {
        mCloseable = closeable;
        return this;
    }

    /**
     * Set title of picker
     */
    public EasyFilePicker withTitle(CharSequence title) {
        mTitle = title;
        return this;
    }

    public EasyFilePicker withCustomActivity(Class<? extends FilePickerActivity> customActivityClass) {
        mFilePickerClass = customActivityClass;
        return this;
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


    /**
     * @return Intent that can be used to start Material File Picker
     */
    public Intent getIntent() {
        CompositeFilter filter = getFilter();

        Activity activity = null;
        if (mActivity != null) {
            activity = mActivity;
        } else if (mFragment != null) {
            activity = mFragment.getActivity();
        } else if (mSupportFragment != null) {
            activity = mSupportFragment.getActivity();
        }

        Intent intent = new Intent(activity, mFilePickerClass);
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

        return intent;
    }

    /**
     * Open Material File Picker activity.
     * You should set Activity or Fragment before calling this method
     *
     * @see EasyFilePicker#withActivity(Activity)
     * @see EasyFilePicker#withFragment(Fragment)
     * @see EasyFilePicker#withSupportFragment(androidx.fragment.app.Fragment)
     */
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
