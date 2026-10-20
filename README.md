# गणित 10 BSEB — Maths Class 10 (Bihar Board)

Offline Android app for Bihar Board (BSEB) Class 10 maths board-exam practice, in Hindi and English.

- Key formulas and facts for all 14 chapters, with search (Hindi or English) and bookmarks
- 177 chapter-wise MCQs with instant feedback and worked steps
- Timed mixed mock test (25 random questions)
- Progress tracker: questions solved per chapter and overall, plus best scores, saved on the device (with reset)
- Dark mode (follows the phone setting, or toggle with 🌙)
- Daily practice reminder notification at a time you choose (Tips tab)
- Share the app with classmates from the Tips tab (opens the phone's share menu)

All content lives in `app/src/main/assets/index.html`, shown in a WebView.

## Build

```
./gradlew assembleDebug
```

The APK is at `app/build/outputs/apk/debug/app-debug.apk`. CI builds it on every push to `master`.

Content is for practice; check the official syllabus at biharboardonline.bihar.gov.in.
