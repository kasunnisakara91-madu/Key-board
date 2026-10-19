# BESTIE Keyboard 🌸✨

A modern, beautiful, and privacy-focused Android keyboard built with Kotlin, Jetpack Compose, Material Design 3, and the official Android `InputMethodService` API. 

Designed for seamless bilingual typing in **English** and **Sinhala (සිංහල)**, featuring rich customizable themes, offline clipboard history, emoji picker, tactile feedback, and word suggestions.

---

## 1. What is BESTIE Keyboard?

**BESTIE Keyboard** is a full-featured Android Input Method application (IME) engineered for everyday communication, messaging, and productivity. It functions across all Android text inputs—messaging apps, web browsers, document editors, and social media platforms—while keeping your privacy intact.

Unlike many third-party keyboards, BESTIE Keyboard operates **100% offline**. It does not collect keystrokes, transmit text to external servers, track passwords, or bundle invasive analytics.

---

## 2. Key Features

- **Bilingual Support (English & Sinhala)**:
  - Full English QWERTY keyboard with lowercase, uppercase, and Caps Lock.
  - Sinhala Unicode character keyboard with support for consonants, independent vowels, and vowel signs (*pilli* such as `ා`, `ැ`, `ි`, `ී`, `ු`, `ෙ`, `්`).
  - One-tap quick language switch (`EN` ⇄ `සිං`).
- **Numbers & Multi-Layer Symbols**:
  - `?123` numeric & punctuation layout.
  - Extended mathematical, currency, and bracket symbols (`=\<`).
- **Emoji Keyboard**:
  - Organized category tabs: Recent, Smileys, Gestures, Hearts, Food, Animals, Objects.
  - Persistent recently-used emoji section for instant access.
- **Offline Clipboard History**:
  - Dedicated clipboard key on top accessory bar.
  - Automatically captures copied text snippets.
  - One-tap paste directly into the active field.
  - Delete individual clips or clear all history anytime.
- **7 Premium Keyboard Themes**:
  - **Bestie Violet** (Signature violet & electric indigo gradient)
  - **Midnight Dark** (AMOLED deep black)
  - **Pure Light** (Clean minimalist light theme)
  - **Sunset Coral** (Warm peach & rose gradient)
  - **Ocean Breeze** (Deep marine & cyan gradient)
  - **Emerald Forest** (Lush botanical emerald)
  - **Neon Cyberpunk** (High-contrast dark with neon cyan/pink accents)
- **Typing Intelligence & Autocorrect**:
  - Offline prefix-matching candidate word suggestions in top bar.
  - Automatic capitalization after sentence-ending punctuation (`.`, `!`, `?`).
  - Double-space to insert period + space shortcut.
- **Sensory Feedback**:
  - Tactile vibration/haptics toggle on keypress.
  - Audible click sound effect toggle on keypress.
- **Customization & Accessibility**:
  - 3 adjustable height modes: Compact (220dp), Normal (260dp), and Tall (300dp).
  - Optional key border outlines.
  - Minimum 48dp touch targets and high-contrast color schemes.
- **Interactive Sandbox & Companion App**:
  - In-app live keyboard preview sandbox to test all keys, languages, and themes without leaving the app.
  - Step-by-step setup guide with one-tap shortcuts to Android Settings.

---

## 3. How to Open the Project in Android Studio

1. Download or clone this repository.
2. Launch **Android Studio** (Ladybug / Koala / Hedgehog or newer recommended).
3. Select **Open an Existing Project** and navigate to the project root folder.
4. Allow Gradle to sync dependencies automatically.
5. Ensure **JDK 17** or **JDK 21** is selected under `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK`.
6. Connect an Android device (Android 8.0 / API 26 or higher) or start an Android Virtual Device (AVD).
7. Click the green **Run** button (or press `Shift + F10`) to build and deploy to your phone.

---

## 4. How to Upload the Project to GitHub

To push the project to your own GitHub repository:

```bash
# 1. Initialize git (if not already initialized)
git init

# 2. Add all project files
git add .

# 3. Create initial commit
git commit -m "Initial commit: BESTIE Keyboard complete Android project"

# 4. Rename default branch to main
git branch -M main

# 5. Link your GitHub repository (replace with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/bestie-keyboard.git

# 6. Push code to GitHub
git push -u origin main
```

---

## 5. How to Run the GitHub Actions Workflow

This repository includes a preconfigured GitHub Actions CI workflow located at:
`.github/workflows/android.yml`

The workflow automatically:
1. Runs whenever you **push** code or open a **pull request** to the `main` or `master` branch.
2. Supports manual triggering via **`workflow_dispatch`**:
   - Go to your repository on GitHub.
   - Click the **Actions** tab.
   - Select **BESTIE Keyboard CI / Build APK** from the left sidebar.
   - Click **Run workflow** → select branch `main` → click **Run workflow**.

---

## 6. Where to Download the Generated APK Artifact

1. Open your repository on GitHub.
2. Click on the **Actions** tab.
3. Click on the latest workflow run under **BESTIE Keyboard CI / Build APK**.
4. Scroll down to the bottom of the summary page to the **Artifacts** section.
5. Click **`bestie-keyboard-debug-apk`** to download the ZIP file containing `app-debug.apk`.

---

## 7. How to Install the APK on an Android Phone

1. Transfer or download `app-debug.apk` to your Android device (via USB, Google Drive, or browser).
2. Tap the APK file in your phone's File Manager / Downloads.
3. If prompted with *"Install unknown apps"*, tap **Settings** and toggle **Allow from this source**.
4. Tap **Install**.
5. Once installation finishes, tap **Open** to launch the BESTIE Keyboard companion app.

---

## 8. How to Enable BESTIE Keyboard in Android Settings

Android requires users to explicitly authorize custom keyboards for security:

1. Open the **BESTIE Keyboard** app on your phone.
2. On the **Home** tab, tap **Step 1: Enable in Settings**.
   *(Or navigate manually: Phone Settings → System → Languages & Input → On-screen keyboard → Manage on-screen keyboards).*
3. Toggle the switch next to **BESTIE Keyboard** to **ON**.
4. Read the standard Android system security prompt and tap **OK**.
5. Return to the BESTIE Keyboard app and tap **Step 2: Choose BESTIE**.
6. Select **BESTIE Keyboard** from the input method picker dialog.
7. You are ready! BESTIE is now your active default keyboard.

---

## 9. How to Switch Between Sinhala and English

Switching between English and Sinhala is instant:

- **From English to Sinhala**: Tap the **`සිං`** key located on the bottom row (between `?123` and the Spacebar).
- **From Sinhala to English**: Tap the **`EN`** key on the bottom row.
- **Typing Sinhala Vowel Modifiers (*Pillam*)**:
  1. Tap a consonant (e.g. `ක`).
  2. Tap a vowel modifier (e.g. `ා` for `කා`, `ැ` for `කැ`, `ි` for `කි`, `ෙ` for `කෙ`).
  3. Tap `්` (*hal lakuna*) to produce pure consonants (e.g. `ක` + `්` = `ක්`).
- **Shift Key in Sinhala**: Tap the **Shift** key (upward arrow) to reveal independent vowels (`අ, ආ, ඇ, ඉ, උ, එ, ඔ`) and secondary retroflex/aspirated consonants (`ඝ, ඡ, ඨ, ඪ, ණ, ථ, ධ, භ, ළ, ෆ`).

---

## 10. How to Customize Themes and Settings

Inside the BESTIE Keyboard companion app:

- **Change Theme**:
  - Go to the **Themes** tab.
  - Browse through the 7 gradient and solid styles.
  - Tap any card to immediately apply it across the keyboard.
  - *Shortcut*: While typing in any app, tap the **Palette icon (🎨)** on the keyboard's top bar to quickly cycle through themes on the fly!
- **Haptic & Sound Feedback**:
  - Go to the **Settings** tab.
  - Toggle **Keypress Vibration** on or off.
  - Toggle **Keypress Sound** on or off.
- **Keyboard Height**:
  - In the **Settings** tab, choose between **Compact (220dp)**, **Normal (260dp)**, or **Tall (300dp)**.
- **Word Suggestions & Auto-Capitalization**:
  - Toggle **Word Suggestions**, **Auto-Capitalization**, and **Double-Space Period** in the **Settings** tab.
- **Manage Clipboard**:
  - Go to the **Clipboard** tab to review all locally saved snippets, delete individual entries, or tap **Clear All**.
  - While typing, tap the **Clipboard icon (📋)** on the top bar of the keyboard to paste any recent clip with one tap!

---

## Privacy & Security Commitment

BESTIE Keyboard operates with zero server communication:
- **No Internet Permission**: The app does not request `android.permission.INTERNET`.
- **No Keystroke Tracking**: Keystrokes are sent exclusively to the active input field via Android's `InputConnection`.
- **Password Protection**: Suggestions and learning are automatically deactivated when password fields are detected.
- **Local Storage Only**: Clipboard history and theme preferences are stored exclusively on your device.

---

**Developed with ❤️ for the Android Community**
