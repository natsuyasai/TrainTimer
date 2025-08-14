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
This is an Android Kotlin application called "TrainTimer" that helps users track train schedule information. The app has been migrated from Fragment-based navigation to Jetpack Compose for modern UI development.

### Key Technologies
- **Language**: Kotlin with Java 19 compatibility
- **UI**: Jetpack Compose with Navigation Compose (migrated from Fragments)
- **Database**: Room with migration support (currently at schema version 6)
- **HTTP**: Fuel HTTP client and Jsoup for web scraping (Yahoo Route API)
- **Testing**: JUnit 5, Mockk, Robolectric, and AndroidX Test frameworks
- **Architecture**: MVVM with LiveData, Compose State, and Data Binding

### Core Package Structure
```
com.nyasai.traintimer/
├── MainActivity.kt                    # Original Fragment-based activity (deprecated)
├── MainComposeActivity.kt            # New Compose-based main activity
├── database/                          # Room database layer
│   ├── RouteDatabase.kt              # Main database class with migrations
│   ├── RouteDatabaseDao.kt           # Data access object
│   ├── RouteListItem.kt              # Route list entity
│   ├── RouteDetail.kt                # Route detail entity
│   └── FilterInfo.kt                 # Filter configuration entity
├── routelist/                        # Route list feature (Compose screens)
│   ├── RouteListScreen.kt            # Compose route list screen
│   ├── RouteListViewModel.kt         # Route list business logic
│   └── RouteListFragment.kt          # Legacy Fragment (deprecated)
├── routeinfo/                        # Route details feature (Compose screens)
│   ├── RouteInfoScreen.kt            # Compose route details screen
│   ├── RouteInfoViewModel.kt         # Route info business logic
│   └── RouteInfoFragment.kt          # Legacy Fragment (deprecated)
├── routesearch/                      # Route search functionality (Compose dialogs)
│   ├── SearchTargetInputDialogWithViewModel.kt  # Compose search dialog
│   ├── ListItemSelectDialogWithViewModel.kt     # Compose selection dialog
│   └── ListItemSelectViewModel.kt               # Dialog ViewModels
├── commonparts/                      # Reusable Compose components
│   ├── CommonLoadingCompose.kt      # Loading overlay component
│   ├── RouteListItemCompose.kt      # Route list item component
│   ├── RouteInfoItemCompose.kt      # Route info item component
│   └── RouteInfoTitleCompose.kt     # Route info title component
├── setting/                          # App settings (Compose screens)
│   ├── PreferenceScreen.kt          # Compose settings screen
│   └── PreferenceFragment.kt        # Legacy Fragment (deprecated)
├── datamigration/                    # Import/Export functionality
├── http/                             # HTTP client abstraction
└── util/                             # Utility classes
    └── YahooRouteInfoGetter.kt       # Yahoo Route API integration
```

### Navigation Flow
The app follows a hub-and-spoke navigation pattern using Jetpack Compose Navigation:
- **RouteListScreen**: Main hub showing saved routes with add/sort/settings actions
- **RouteInfoScreen**: Displays detailed train schedules with countdown timer and filtering
- **PreferenceScreen**: Application settings for data backup/restore and app info
- **Dialogs**: Various Compose dialogs for search, selection, edit, and filter operations

### Main User Flows
1. **Route Addition Flow** (based on 路線追加シーケンス.pu):
   - User enters station name in SearchTargetInputDialog
   - System queries Yahoo Route API for station matches
   - User selects station from ListItemSelectDialog
   - System retrieves destination options and displays them
   - User selects destination and system fetches timetable data
   - Route data is stored in database

2. **Data Backup Flow** (based on ファイルバックアップシーケンス.pu):
   - User accesses backup function in PreferenceScreen
   - System requests storage access permission
   - On permission grant: exports route data to external storage
   - On permission deny: shows error dialog

### Database Schema
The app uses Room database with careful migration handling (based on DB.pu):

#### RouteListItem Entity (route_list_item_table)
- **dataId**: Long (Primary Key, Auto-generated) - Unique identifier
- **routeName**: String - Route name (e.g., "JR山手線")  
- **stationName**: String - Station name (e.g., "新宿駅")
- **destination**: String - Destination direction (e.g., "池袋・上野方面")
- **sortIndex**: Long - Manual sort order index

#### RouteDetail Entity (route_details_table)
- **dataId**: Long (Primary Key, Auto-generated) - Unique identifier
- **parentDataId**: Long (Foreign Key, Indexed) - References RouteListItem.dataId
- **diagramType**: Int - Diagram type (0=Weekday, 1=Saturday, 2=Holiday)
- **departureTime**: String - Departure time (HH:MM format)
- **trainType**: String - Train type (e.g., "普通", "快速", "急行")
- **destination**: String - Train destination

#### FilterInfo Entity (filter_info_table)
- **dataId**: Long (Primary Key, Auto-generated) - Unique identifier
- **parentDataId**: Long (Foreign Key, Indexed) - References RouteListItem.dataId
- **trainTypeAndDestination**: String - Combined train type and destination for filtering
- **isShow**: Boolean - Whether to display this train type/destination combination

#### Relationships
- RouteListItem has one-to-many relationship with RouteDetail
- RouteListItem has one-to-many relationship with FilterInfo

Schema changes require adding new Migration objects in RouteDatabase.kt companion object.

### Testing Strategy
- **Unit Tests**: JUnit 5 with Mockk for mocking business logic
- **Compose UI Tests**: Compose testing framework for screen interactions
- **Database Tests**: Room migration and DAO operation validation
- **Integration Tests**: End-to-end user flow testing
- **Framework Testing**: Robolectric enables Android framework testing in JVM

### Data Import/Export
The app supports data backup and restore functionality:
- **Export**: CSV format backup of route configurations to external storage
- **Import**: Route data restoration from backup files
- **Permissions**: Requires storage access permissions for file operations

## Development Notes

### Code Style
- Uses official Kotlin code style
- Japanese comments are used throughout the codebase
- Compose UI with Material 3 design system
- ViewModels follow Android Architecture Components patterns
- MVVM + Compose State management pattern

### Jetpack Compose Migration Status
The application has been fully migrated to Jetpack Compose:
- ✅ **MainComposeActivity**: New Compose-based entry point
- ✅ **RouteListScreen**: Complete Compose implementation with dialogs
- ✅ **RouteInfoScreen**: Full Compose UI with countdown timer and filtering
- ✅ **PreferenceScreen**: Settings screen in Compose
- ✅ **Dialogs**: All dialogs converted to Compose (Search, Selection, Edit, Filter)
- ✅ **Components**: Reusable Compose components for route display
- ⚠️ **Legacy**: Fragment-based files retained for reference but deprecated

### External Dependencies
- **Yahoo Route API**: Real-time train schedule data via YahooRouteInfoGetter
- **Web Scraping**: Jsoup for HTML parsing of route information
- **HTTP Client**: Fuel library for network requests
- **Compose BOM**: Jetpack Compose libraries for modern UI
- **Navigation Compose**: Type-safe navigation between screens
- **LiveData Compose**: Integration between LiveData and Compose State