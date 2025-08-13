# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

- 必ず日本語で回答してください。
- ユーザーからの指示や仕様に疑問などがあれば作業を中断し、質問すること。
- コードエクセレンスの原則に基づき、テスト駆動開発を必須で実施すること。
- TDDおよびテスト駆動開発で実装する際は、すべてt-wadaの推奨する進め方に従ってください。
- リファクタリングはMartin Fowloerが推奨する進め方に従ってください。
- セキュリティルールに従うこと。
- 実装時は可能な限りテストコードも作成してください
- 実装時は適宜コミットを行ってください

## Development Commands

### Build and Test
```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run instrumented tests (requires Android device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Generate debug APK
./gradlew assembleDebug

# Generate release APK
./gradlew assembleRelease
```

### Database Schema Management
```bash
# Database schema files are automatically generated in app/schemas/
# Version migrations are handled in RouteDatabase.kt companion object
```

## Project Architecture

### Overview
This is an Android Kotlin application called "TrainTimer" that helps users track train schedule information. The app uses a single Activity with Navigation Component for fragment-based navigation.

### Key Technologies
- **Language**: Kotlin with Java 19 compatibility
- **UI**: Android Navigation Component with Data Binding and View Binding
- **Database**: Room with migration support (currently at schema version 6)
- **HTTP**: Fuel HTTP client and Jsoup for web scraping
- **Testing**: JUnit 5, Mockk, Robolectric, and AndroidX Test frameworks
- **Architecture**: MVVM with LiveData and Data Binding

### Core Package Structure
```
com.nyasai.traintimer/
├── MainActivity.kt                    # Single activity host
├── database/                          # Room database layer
│   ├── RouteDatabase.kt              # Main database class with migrations
│   ├── RouteDatabaseDao.kt           # Data access object
│   ├── RouteListItem.kt              # Route list entity
│   ├── RouteDetail.kt                # Route detail entity
│   └── FilterInfo.kt                 # Filter configuration entity
├── routelist/                        # Route list feature
├── routeinfo/                        # Route details feature
├── routesearch/                      # Route search functionality
├── datamigration/                    # Import/Export functionality
├── setting/                          # App settings and preferences
├── http/                             # HTTP client abstraction
└── util/                             # Utility classes
```

### Navigation Flow
The app follows a hub-and-spoke navigation pattern:
- **RouteListFragment**: Main hub showing saved routes
- **RouteInfoFragment**: Displays detailed train schedules for a selected route
- **SearchTargetInputDialogFragment**: Route search input
- **ListItemSelectDialogFragment**: Station/route selection
- **PreferenceFragment**: Application settings

### Database Schema
The app uses Room database with careful migration handling:
- **route_list_item_table**: Stores user's saved routes
- **route_details_table**: Stores train schedule details
- **filter_info_table**: Stores display filter preferences

Schema changes require adding new Migration objects in RouteDatabase.kt companion object.

### Testing Strategy
- Unit tests use JUnit 5 with Mockk for mocking
- UI tests use AndroidX Test with Espresso
- Database tests validate migrations and DAO operations
- Robolectric enables Android framework testing in JVM

### Data Import/Export
The app supports data migration through the datamigration package, allowing users to backup and restore their route configurations.

## Development Notes

### Code Style
- Uses official Kotlin code style
- Japanese comments are used throughout the codebase
- Data binding is enabled for layouts
- ViewModels follow Android Architecture Components patterns

### External Dependencies
- Yahoo Route API integration via YahooRouteInfoGetter
- Web scraping capabilities through Jsoup
- HTTP requests handled by Fuel library