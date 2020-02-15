# Easy File Picker

### build.gradle (Project)
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### build.gradle (Module app)
```
dependencies {
    ...
    implementation 'com.github.mostfabian:FilePicker:1.1.0'
}
```

### Start picking file
```
new EasyFilePicker()
    .withActivity(this)
    .withRequestCode(65535)
    .withFilter(Pattern.compile(".*\\.jpg"))
    .withTitle("Title")
    .start();
```

### Handle result
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 65535 && resultCode == RESULT_OK) {
        String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        //TODO
    }
}
```

### Customization

#### add close icon
```
.withCloseMenu(true)
```

#### add close icon with special color
```
.withCloseMenu(true, R.color.colorAccent)
```

#### change toolbar title color
```
.withToolbarTitleColor(R.color.colorAccent)
```

#### To change toolbar subtitle color
```
.withToolbarSubtitleColor(R.color.colorAccent)
```

#### change toolbar background color
```
.withToolbarBackgroundColor(R.color.colorPrimary)
```

#### change toolbar back arrow color
Create style like this:
```
<style name="ToolbarTheme">
    <item name="android:textColorSecondary">@color/colorAccent</item>
</style>
```
Then create a xml layout `easy_file_picker_toolbar`:
```
<?xml version="1.0" encoding="utf-8"?>
<androidx.appcompat.widget.Toolbar
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="?attr/actionBarSize"
    app:theme="@style/ToolbarTheme"
    app:popupTheme="@style/ThemeOverlay.AppCompat.Light"/>
```
After that:
```
.withToolbar(R.layout.easy_file_picker_toolbar, R.id.toolbar)
```
