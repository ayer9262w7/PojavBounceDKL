# 🚀 GitHub Actions CI/CD Workflows

## 📋 Overview

This repository uses GitHub Actions for automated building, testing, and artifact generation. The CI/CD pipeline ensures that every change is properly validated and produces ready-to-use JAR files.

## 🔧 Workflows

### 1. 🏗️ Auto Build & Deploy (`build.yml`)

**Triggers:**
- Push to `main`, `develop`, or any `feature/*` branches
- Pull requests to `main` or `develop` branches
- Manual workflow dispatch
- Changes to source code, build configurations, or workflows

**Features:**
- ☕ **Java 21 Support** with Microsoft OpenJDK
- 🚀 **Gradle Caching** for faster builds
- 🧪 **Automated Testing** with full test suite
- 🔨 **Full Compilation** including Kotlin sources
- 📦 **JAR Generation** via `gradlew remapJar`
- 📤 **Artifact Upload** with 30-day retention
- 📊 **Build Reports** with detailed statistics
- 📢 **Status Notifications** and summaries

**Monitored Paths:**
```yaml
paths:
  - 'src/**'                    # Source code changes
  - 'build.gradle*'             # Build configuration
  - 'gradle/**'                 # Gradle wrapper and config
  - 'settings.gradle*'          # Project settings
  - 'gradle.properties'         # Project properties
  - '.github/workflows/**'      # Workflow changes
```

## 📦 Artifacts Generated

After each successful build, the following artifacts are uploaded:

### 🎯 Main Artifacts (`liquidbounce-jars-java21`)
- `liquidbounce-0.31.2.jar` - Main production JAR
- `build-report.md` - Detailed build report
- **Retention**: 30 days

### 📋 Build Logs (`build-logs-java21`)
- Test reports from `build/reports/`
- Build logs from `build/tmp/`
- **Retention**: 7 days

## 🎮 Manual Workflow Dispatch

You can trigger builds manually from the GitHub Actions tab:

1. Go to **Actions** tab
2. Select **🚀 Auto Build & Deploy** workflow
3. Click **Run workflow**
4. Choose build type:
   - `release` (default) - Production build
   - `debug` - Debug build with additional logging

## 📊 Build Process

The CI pipeline follows these steps:

1. **📥 Checkout** - Fetch repository code
2. **☕ Java Setup** - Install Java 21 with caching
3. **🔧 Gradle Setup** - Configure Gradle with caching
4. **🧪 Testing** - Run complete test suite
5. **🔨 Compilation** - Compile Kotlin and Java sources
6. **🏗️ Build** - Execute `gradlew remapJar`
7. **📦 Reporting** - Generate build report
8. **📤 Upload** - Upload JAR files and reports
9. **📢 Notify** - Send build status summary

## 🔍 Build Verification

Each build includes comprehensive verification:

- ✅ **Compilation Check** - All sources compile successfully
- ✅ **Test Execution** - Full test suite passes
- ✅ **Dependency Resolution** - All external dependencies resolved
- ✅ **JAR Generation** - Production JAR created and validated
- ✅ **Artifact Upload** - Build outputs properly archived

## 📥 Downloading Artifacts

After a successful build:

1. Go to the **Actions** tab
2. Click on the completed workflow run
3. Scroll down to **Artifacts** section
4. Download:
   - `liquidbounce-jars-java21` for JAR files
   - `build-logs-java21` for detailed logs

## 🛠️ Development Workflow

### For Contributors:
1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes to source code
3. Push changes: `git push origin feature/your-feature`
4. **CI automatically builds** and validates changes
5. Create pull request - CI runs again for PR validation
6. Download artifacts from Actions tab to test locally

### For Maintainers:
1. Review PR with automated CI results
2. Check build artifacts for validation
3. Merge when CI passes and code review approves
4. CI automatically builds main branch
5. Production artifacts available for release

## 🎯 Performance Optimizations

The workflow includes several performance optimizations:

- **Gradle Caching**: Dependencies cached between builds
- **Java Caching**: JDK installation cached
- **Incremental Builds**: Only changed files recompiled
- **Parallel Execution**: Tests and builds run efficiently
- **Artifact Compression**: Optimal storage and download

## 🔧 Customization

To modify the CI behavior:

1. Edit `.github/workflows/build.yml`
2. Adjust trigger conditions, Java versions, or build steps
3. Commit changes - workflow automatically updates

## 📞 Support

If you encounter issues with the CI pipeline:

1. Check the **Actions** tab for build logs
2. Review the build report in artifacts
3. Check that all dependencies are properly configured
4. Ensure Java 21 compatibility for any new code

---

**🎉 Happy Building!** The CI pipeline ensures that every change is properly tested and ready for production use.