# Easy File Picker

Material simple library for pick files. Easy to use, compatable with androidX, customizable and flexible.

### Start Using

#### Build.gradle (Project)
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### Build.gradle (Module app)
```
dependencies {
    ...
    implementation 'com.github.mostfabian:FilePicker:1.2.0'
}
```

#### Android Manifests
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

#### Picking File
```
new EasyFilePicker()
    .withActivity(this)
    .withRequestCode(65535)
    .addMimeType(FilePickerActivity.MimeType.MIME_TYPE_IMAGE)
    .withTitle("Title")
    .start();
```

#### Handle Result
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

### Extra Methods

#### Add Mime Types
```
.addMimeType(FilePickerActivity.MimeType.MIME_TYPE_VIDEO)
```
Use can use `MIME_TYPE_IMAGE`, `MIME_TYPE_VIDEO` and `MIME_TYPE_AUDIO` for now.

#### Add Extentions
```
.addExtensions("pdf", "zip", "txt")
```

#### Ignore Extentions
```
.ignoreExtensions("zip", "txt")
```
This method check extensions that added before.

#### Add Filter
If either of the above methods does not meet your needs, you can use this method:
```
.withFilter(Pattern.compile(".*\\.(jpg|zip)"))
```
If use this, methods `addMimeType`, `addExtensions` and `ignoreExtensions` will not work anymore.

### Customization

#### Add Close Menu
```
.withCloseMenu(true)
```

#### Add Close Menu With Special Color
```
.withCloseMenu(true, R.color.colorAccent)
```

#### Change Toolbar Title Color
```
.withToolbarTitleColor(R.color.colorAccent)
```

#### Change Toolbar Subtitle Color
```
.withToolbarSubtitleColor(R.color.colorAccent)
```

#### Change Toolbar Background Color
```
.withToolbarBackgroundColor(R.color.colorPrimary)
```

#### Change Toolbar Back Arrow Color
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
