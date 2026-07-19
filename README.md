<div align="center">

<!-- Animated Banner -->
<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=12,20,24&height=200&section=header&text=AplikasiMobile%20SMT4&fontSize=52&fontColor=ffffff&animation=fadeIn&fontAlignY=38&desc=Proyek%20Akhir%20Pemrograman%20Mobile%20Semester%204&descAlignY=60&descAlign=50" width="100%"/>

<!-- Badges -->
<p>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white"/>
  <img src="https://img.shields.io/badge/Semester-4-orange?style=for-the-badge"/>
</p>

<p>
  <img src="https://img.shields.io/badge/Status-Active-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/github/last-commit/XfindZ-pro/AplikasiMobile_smt4?style=for-the-badge&color=blue"/>
  <img src="https://img.shields.io/github/languages/top/XfindZ-pro/AplikasiMobile_smt4?style=for-the-badge&color=ED8B00"/>
</p>

<!-- Action Buttons -->
<p>
  <a href="https://github.com/XfindZ-pro/AplikasiMobile_smt4/archive/refs/heads/main.zip">
    <img src="https://img.shields.io/badge/⬇️%20Download%20ZIP-2ea44f?style=for-the-badge"/>
  </a>
  <a href="https://github.com/XfindZ-pro/AplikasiMobile_smt4/releases">
    <img src="https://img.shields.io/badge/📦%20Download%20APK-3DDC84?style=for-the-badge"/>
  </a>
  <a href="https://github.com/XfindZ-pro/AplikasiMobile_smt4/issues/new">
    <img src="https://img.shields.io/badge/🐛%20Laporkan%20Bug-red?style=for-the-badge"/>
  </a>
  <a href="https://github.com/XfindZ-pro/AplikasiMobile_smt4/fork">
    <img src="https://img.shields.io/badge/🍴%20Fork%20Repo-blueviolet?style=for-the-badge"/>
  </a>
</p>

</div>

---

## 📖 Tentang Proyek

**AplikasiMobile SMT4** adalah aplikasi Android native yang dikembangkan sebagai **Proyek Akhir Pemrograman Mobile Semester 4**. Aplikasi ini dibangun menggunakan **Java** sebagai bahasa pemrograman utama dan **Gradle Kotlin DSL** sebagai sistem build, dengan struktur project standar Android Studio.

> 📱 Proyek ini merupakan implementasi nyata dari konsep pengembangan aplikasi mobile Android menggunakan pendekatan native dengan Java dan komponen AndroidX.

---

## ✨ Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
| ☕ **Java Native** | Dibangun murni menggunakan Java untuk Android |
| 🏗️ **Gradle KTS** | Menggunakan Kotlin DSL untuk konfigurasi build yang modern |
| 📦 **AndroidX** | Memanfaatkan library AndroidX terbaru |
| ⚡ **Optimized Build** | JVM args dikonfigurasi dengan Xmx2048m untuk build yang cepat |
| 🔒 **Non-Transitive R Class** | Menggunakan `nonTransitiveRClass` untuk performa build lebih baik |

---

## 🗂️ Struktur Proyek

```
AplikasiMobile_smt4/
│
├── 📁 .idea/                  # Konfigurasi Android Studio / IntelliJ
├── 📁 app/                    # Modul aplikasi utama
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/       # Source code Java
│   │   │   ├── 📁 res/        # Resource (layout, drawable, strings)
│   │   │   └── 📄 AndroidManifest.xml
│   │   └── 📁 test/           # Unit test
├── 📁 gradle/                 # Gradle wrapper & version catalog
│   └── 📁 wrapper/
├── 📄 build.gradle.kts        # Build script root project
├── 📄 settings.gradle.kts     # Konfigurasi project & repository
├── 📄 gradle.properties       # Properti Gradle (JVM, AndroidX)
├── 📄 gradlew                 # Gradle wrapper (Linux/Mac)
├── 📄 gradlew.bat             # Gradle wrapper (Windows)
└── 📄 .gitignore              # File yang dikecualikan dari Git
```

---

## 🛠️ Teknologi yang Digunakan

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/AndroidX-4285F4?style=flat-square&logo=google&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat-square&logo=androidstudio&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/XML-FF6600?style=flat-square&logo=xml&logoColor=white" height="28"/>
</p>

---

## 🚀 Cara Menjalankan

### Prasyarat

Pastikan sudah terinstall:

- [Android Studio](https://developer.android.com/studio) (versi terbaru)
- JDK **11** atau lebih baru
- Android SDK (API Level **21+** direkomendasikan)
- Emulator Android atau perangkat fisik Android

### Langkah Instalasi

**1. Clone repositori ini**
```bash
git clone https://github.com/XfindZ-pro/AplikasiMobile_smt4.git
```

**2. Buka di Android Studio**
```
File → Open → Pilih folder AplikasiMobile_smt4
```

**3. Sync Gradle**

Tunggu Android Studio menyelesaikan Gradle sync secara otomatis. Jika tidak otomatis:
```
File → Sync Project with Gradle Files
```

**4. Jalankan Aplikasi**

Pilih emulator atau sambungkan perangkat fisik, lalu klik tombol **▶ Run** atau tekan:
```
Shift + F10
```

### Build APK Manual

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

APK akan tersedia di:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚙️ Konfigurasi Gradle

Project ini menggunakan konfigurasi Gradle yang dioptimalkan:

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
```

---

## 📸 Screenshot

> *Tambahkan screenshot tampilan aplikasi di sini*

```
screenshots/
├── splash_screen.png
├── home_screen.png
└── detail_screen.png
```

---

## 📋 Persyaratan Sistem

| Komponen | Minimum | Direkomendasikan |
|----------|---------|-----------------|
| Android SDK | API 21 (Android 5.0) | API 33+ |
| JDK | 11 | 17 |
| RAM Build | 2 GB | 4 GB+ |
| Android Studio | Iguana | Ladybug / terbaru |

---

## 👨‍💻 Kontributor

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/XfindZ-pro">
        <img src="https://github.com/XfindZ-pro.png" width="80" style="border-radius:50%"/><br/>
        <sub><b>XfindZ-pro</b></sub>
      </a><br/>
      <img src="https://img.shields.io/badge/👑%20Developer%20Utama-3DDC84?style=flat-square"/>
    </td>
    <td align="center">
      <a href="https://github.com/Daerov-Syn">
        <img src="https://github.com/Daerov-Syn.png" width="80" style="border-radius:50%"/><br/>
        <sub><b>daerov</b></sub>
      </a><br/>
      <img src="https://img.shields.io/badge/🛠️%20Developer-ED8B00?style=flat-square"/>
    </td>
  </tr>
</table>

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan **akademik** — Tugas Proyek Akhir Pemrograman Mobile Semester 4.

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=12,20,24&height=100&section=footer" width="100%"/>

<p>
  <img src="https://komarev.com/ghpvc/?username=XfindZ-pro&label=Profile%20Views&color=3DDC84&style=flat-square"/>
</p>

**⭐ Jangan lupa kasih star kalau proyek ini membantu!**

<br/>

Made with ❤️ using Java & Android Studio

</div>
