# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Next Player is a native Android video player written in Kotlin, using Jetpack Compose + Material 3. It relies on Media3/ExoPlayer with an FFmpeg extension (via nextlib) for broad format support.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing config)
./gradlew assembleRelease

# Build release bundle (AAB)
./gradlew bundleRelease

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew :core:domain:test --tests "dev.anilbeesetti.nextplayer.core.domain.GetSortedVideosUseCaseTest"

# Lint check (ktlint)
./gradlew ktlintCheck

# Auto-format (ktlint)
./gradlew ktlintFormat
```

CI runs `assembleDebug`, `test`, `ktlintCheck` in sequence. Match this before submitting PRs.

## Architecture

Multi-module Gradle project (12 modules). Dependency flow flows downward only:

```
app
 ├── feature:player
 ├── feature:videopicker
 └── feature:settings
      │
      ├── core:ui          (shared Compose components, theme)
      ├── core:domain       (use cases)
      ├── core:data         (repository implementations)
      │    ├── core:database   (Room DAOs, entities)
      │    ├── core:datastore  (DataStore preferences)
      │    ├── core:media      (media scanning, sync)
      │    └── core:model      (pure Kotlin domain models, no Android deps)
      └── core:common       (shared utilities, DI scopes)
```

### Module Responsibilities

- **`core:model`** — Pure Kotlin module, zero Android dependencies. Domain models (`Video`, `Sort`, `Folder`, `ThemeConfig`, etc.) use kotlinx.serialization.
- **`core:data`** — Repository pattern implementations. Maps between Room entities and domain models. Contains `Fake*Repository` classes for testing.
- **`core:domain`** — Use cases (e.g., `GetSortedVideosUseCase`). Business logic lives here, not in ViewModels directly.
- **`core:database`** — Room database with schema exports. DAOs return `Flow` for reactive queries.
- **`core:datastore`** — Jetpack DataStore for user preferences (theme, player settings, sort order).
- **`core:media`** — `MediaSynchronizer` scans device media into the database. `MediaOperationsService` handles file operations.
- **`core:ui`** — `NextPlayerTheme`, Material 3 color scheme generation, shared Compose components.
- **`core:common`** — Extensions, `@ApplicationScope` coroutine scope, character encoding detection (juniversalchardet).
- **`feature:player`** — Media3/ExoPlayer integration. Hybrid Compose + ViewBinding. Player controls, subtitle/audio track selection, playback state management, `PlayerService`.
- **`feature:videopicker`** — Media library browser (folder view, video list, tree view). Coil for thumbnails, Accompanist for permissions.
- **`feature:settings`** — All settings screens. Uses Hilt navigation for Compose-based settings destinations.

### DI & Navigation

- **Hilt** for dependency injection throughout. `@ApplicationScope` provides a process-wide coroutine scope.
- **Navigation Compose** with two top-level graphs: `MediaNavGraph` (picker → player) and `SettingsNavGraph`.
- Single `Activity` architecture (`MainActivity`).

## Conventions

- **Language**: Kotlin only. No Java source files.
- **UI**: Jetpack Compose. `feature:player` uses Compose + ViewBinding hybrid for the player surface.
- **Async**: Coroutines + Flow. Repositories return `Flow`; use cases are `suspend` functions.
- **Image loading**: Coil 3.x throughout.
- **Code style**: ktlint with `android_studio` style (enforced by `.editorconfig`). Trailing commas allowed. Composable naming rules are relaxed.
- **Version catalog**: All dependency versions managed in `gradle/libs.versions.toml`. Reference as `libs.xxx` in build scripts.
- **Build variants**: `debug` (`.debug` suffix), `release` (minified), `release-with-debug-signing` (`.release` suffix).
- **ABI splits**: Builds produce per-ABI APKs (armeabi-v7a, arm64-v8a, x86, x86_64) plus universal.

## Testing

- Framework: JUnit 4 + kotlinx-coroutines-test.
- Tests live in `src/test/` (unit only, no instrumentation tests currently).
- Fake repositories in `core:data` (`FakeVideoRepository`, `FakeFolderRepository`, etc.) are the primary test doubles.
- Use `runTest` from kotlinx-coroutines-test for coroutine-based tests.
