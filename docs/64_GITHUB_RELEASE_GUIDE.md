# GitHub release guide — Ishara 6.1.0

The teacher needs a repository link that opens without requesting permission. A **public repository** is the simplest option unless your course has a different privacy rule.

## Before pushing

- Confirm the project title is still `ISHARA – Sign-to-Speech Communication`.
- Configure Firebase client values in `app/src/main/res/values/firebase_config.xml` if the teacher must clone and run cloud features. These are client identifiers, not Admin SDK secrets.
- Never commit Firebase service-account JSON, admin private keys, keystores, passwords or signing credentials.
- Keep the published Firebase Realtime Database and Firestore rules restrictive.
- Run the Android build and device tests. Do not rely on static checks alone.
- Capture actual app and Firebase Console screenshots for the Word report.

## Create the repository

1. Sign in to GitHub and choose **New repository**.
2. Repository name suggestion: `ishara-android`.
3. Set visibility to **Public** if your teacher must access it without logging in.
4. Do not initialize it with a README or license because this project already contains a README.
5. Copy the HTTPS repository URL.

## Push this project from the extracted project root

Replace `<YOUR_GITHUB_USERNAME>` with your own GitHub username:

```bash
git init
git add .
git commit -m "Release Ishara 6.1.0"
git branch -M main
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/ishara-android.git
git push -u origin main
```

If a Git repository already exists, do not run `git init` again. Add the new files, commit and push to the intended remote.

## Verify

- Open the repository in a private/incognito browser window to confirm public access.
- Open the **Actions** tab and confirm the Android build workflow completes successfully.
- Open the repository README and confirm the build and Firebase setup instructions are clear.
- Copy the repository URL into the final submission form.

The repository link and video link are separate submission items. Set the video permission so the teacher can open it without requesting access.
