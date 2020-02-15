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
