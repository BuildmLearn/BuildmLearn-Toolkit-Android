[![Preview the app](https://img.shields.io/badge/Preview-Appetize.io-green.svg)](https://appetize.io/app/k56b095hpt7ppmhmmhbg2ayj20) [![Build Status](https://travis-ci.org/BuildmLearn/BuildmLearn-Toolkit-Android.svg)](https://travis-ci.org/BuildmLearn/BuildmLearn-Toolkit-Android) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/05c83f4ecad84cc0a2e57d7ea39df41f)](https://www.codacy.com/app/anupam/BuildmLearn-Toolkit-Android?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=BuildmLearn/BuildmLearn-Toolkit-Android&amp;utm_campaign=Badge_Grade)

# BuildmLearn-Toolkit-Android

This repository contains the Android version of the [BuildmLearn Toolkit](https://github.com/BuildmLearn/BuildmLearn-Toolkit). BuildmLearn Toolkit app is an easy-to-use android app that helps the users make another mobile apps without any knowledge of android application development. The toolkit helps creating mobile application with various functionality and allows teachers to input their custom content. Targeted at teachers, this toolkit helps them make learning fun and engaging through mobile apps.

[Toolkit-mobile-templates](https://github.com/BuildmLearn/Toolkit-mobile-templates) contains the template applications of the toolkit.

# Development Setup
1. Go to the project repo and click the `Fork` button
2. Clone your forked repository : `git clone git@github.com:your_name/BuildmLearn-Toolkit-Android.git`
3. Move to android project folder `cd source-code`
4. Open the project with Android Studio

Glosarry
======
| Folders       | Description           |
| ------------- |:-------------|
| **source-code**       | **Android Project Files**           |
| **ui-design**      | **Contains UI mockups and wireframes**   |
| **[X]**       |   **source-code/app/src/main/java/org/buildmlearn/toolkit**    |
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
| [X]/dictationtemplate | Contains simulator's code for Dictation Template  |
| [X]/matchtemplate | Contains simulator's code for Match Template  |
| [X]/[?]/adapter | Contains simulator's adapter for [?] Template  |
| [X]/[?]/data  | Contains simulator's SQLiteDatabase code for [?] Template |
| [X]/[?]/fragment | Contains simulator's fragment for [?] Template  |

For complete documentation, please visit: http://buildmlearn.github.io/BuildmLearn-Toolkit-Android/

# How to build

All dependencies are defined in ```source-code/app/build.gradle```. Import the project in Android Studio or use Gradle in command line:

```
./gradlew assembleRelease
```

The result apk file will be placed in ```source-code/app/build/outputs/apk/```.

#Contribution policy

All contributions should be done in **bug-fixes** branch. PRs must pass build check on Travis-CI.

# License for use and distribution

All the code in this repository (unless specified otherwise) is governed by the BSD (3-Clause) License quoted below.

Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/ All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

Neither the name of the BuildmLearn nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Maintainers
The project is maintained by
- Pankaj Nathani ([@croozeus](https://github.com/croozeus))
- Anupam Das ([@opticod](https://github.com/opticod))

