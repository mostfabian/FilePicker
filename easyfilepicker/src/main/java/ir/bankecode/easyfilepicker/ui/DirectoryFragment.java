package ir.bankecode.easyfilepicker.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import ir.bankecode.easyfilepicker.R;
import ir.bankecode.easyfilepicker.filter.CompositeFilter;
import ir.bankecode.easyfilepicker.utils.FileUtils;
import ir.bankecode.easyfilepicker.widget.EmptyRecyclerView;

public class DirectoryFragment extends Fragment {

    interface FileClickListener {
        void onFileClicked(File clickedFile);
    }

    private static final String ARG_FILE_PATH = "arg_file_path";
    private static final String ARG_FILTER = "arg_filter";
    private static final String ARG_HIDE_EMPTY_DIRS = "arg_hide_empty_dirs";

    private View mEmptyView;
    private String mPath;
    private boolean mHideEmptyDirs;

    private CompositeFilter mFilter;

    private EmptyRecyclerView mDirectoryRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private FileClickListener mFileClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFileClickListener = (FileClickListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFileClickListener = null;
    }

    public static DirectoryFragment getInstance(
            String path, CompositeFilter filter, boolean hideEmptyDirs) {
        DirectoryFragment instance = new DirectoryFragment();

        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        args.putSerializable(ARG_FILTER, filter);
        args.putBoolean(ARG_HIDE_EMPTY_DIRS, hideEmptyDirs);
        instance.setArguments(args);

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        mDirectoryRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.directory_recycler_view);
        mEmptyView = view.findViewById(R.id.directory_empty_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initFilesList();
    }

    private void initFilesList() {
        mDirectoryAdapter = new DirectoryAdapter(getActivity(),
                FileUtils.getFileListByDirPath(mPath, mFilter, mHideEmptyDirs));

        mDirectoryAdapter.setOnItemClickListener(new DirectoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mFileClickListener != null) {
                    mFileClickListener.onFileClicked(mDirectoryAdapter.getModel(position));
                }
            }
        });

        mDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDirectoryRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryRecyclerView.setEmptyView(mEmptyView);
    }

    private void initArgs() {
        if (getArguments().getString(ARG_FILE_PATH) != null) {
            mPath = getArguments().getString(ARG_FILE_PATH);
            mHideEmptyDirs = getArguments().getBoolean(ARG_HIDE_EMPTY_DIRS);
        }
        mFilter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
    }
}
