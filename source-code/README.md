#This folder contains the project source-code

Glosarry
======
| Folders       | Description           |
| ------------- |:-------------|
| **[X]**       |   **app/src/main/java/org/buildmlearn/toolkit**    |
| [X]/activity    | Contains various activities  |
| [X]/adapters    | Contains various adapters    |
| [X]/fragment | Contains various fragment  |
| [X]/simulator | Contains simulator activity  |
| [X]/templates | Contains various template activities |
| [X]/model | Contains KeyStoreDetails, SavedProject, TemplateInfos, Tutorial, etc  |
| [X]/utilities | Contains various utilities including SignerThread  |
| [X]/views | Contains text-view font support for old-backed SDKs  |
| [X]/infotemplate | Contains simulator's code for Info Template  |
| [X]/learnspelling | Contains simulator's code for learnspelling Template  |
| [X]/flashcardtemplate | Contains simulator's code for FlashCard Template  |
| [X]/quiztemplate | Contains simulator's code for Quiz Template  |
| [X]/videocollectiontemplate | Contains simulator's code for FlashCard Template  |
| [X]/comprehensiontemplate | Contains simulator's code for Comprehension Template  |
| [X]/[?]/adapter | Contains simulator's adapter for [?] Template  |
| [X]/[?]/data  | Contains simulator's SQLiteDatabase code for [?] Template |
| [X]/[?]/fragment | Contains simulator's fragment for [?] Template  |

# How to build

All dependencies are defined in ```app/build.gradle```. Import the project in Android Studio or use Gradle in command line:

```
./gradlew assembleRelease
```

The result apk file will be placed in ```app/build/outputs/apk/```.
