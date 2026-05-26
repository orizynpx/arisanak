---
date: 2026-05-26
---

# 📱 ANDROID APP PROJECT SPECIFICATION TEMPLATE

## 1. PROJECT METADATA & CONFIGURATION

* **App Name:** `Arisanak`
* **Package Name:** `io.github.naupyon.arisanak`
* **Primary Form Factor:** `Phone`
* **Target Orientation:** `Portrait Only`
* **AI Studio Architecture Constraint:** Client-side only, Single-Activity, Jetpack Compose UI.

---

## 2. CORE CONCEPT & AI STUDIO SYSTEM PROMPT

> **Developer Note:** Copy and paste this section directly into the Google AI Studio prompt builder panel to guide the Antigravity Agent.

### 2.1 High-Level Objective

`[Describe the core purpose of your app in 2-3 sentences. What pain point does it solve?]`
`A local-first money tracker app for managing arisan (rotating money pool in Indonesia). The user is only the admin or treasurer of the arisan group.`

### 2.2 Core User Persona

`A 32-year-old mother living in Jabodetabek region in Indonesia who is the "admin" or treasurer for an arisan group of 6 fellow women. She mostly uses her smartphone to manage the group's money, and Excel/Google Sheets are just too complicated. She wishes there is an Android app that allows her to input each member's transaction into the arisan, as well as a way to roll a winner in one app. She can't connect to the internet at all times, so she would prefer an offline app. However, she does have two phones, so she would appreciate a simple account synchronize, similar to what Obsidian has.`

### 2.3 Master Prompt for the AI Studio Build Agent

```text
Act as an expert Android Engineer. Build a native Android application using Kotlin, Jetpack Compose, and Material 3 Expressive design rules. Follow a strict single-activity architecture using clean ViewModels to manage state. Avoid any server-side dependencies or XML layouts. 
The app must execute the following core workflows:
1. When the user opens the app, they see a dashboard. The topmost element is a card carousel containing group information such as the current total balance of the group. The structure is similar to a mobile banking app, such as Brimo. Unlike a typical banking app, however, there is no logging in first. On the bottom right, just above the bottom navigation bar, there is a FAB with a plus icon to add a new transaction log entry, without opening a whole new page, similar to the mobile app of Google Tasks. When inputting the entry, the user is required to input the transaction amount, the group that the transaction belongs to (if the user has more than 1 groups), and the person in the group that did the transaction. By default, the log date is the time the user submits the new entry, but it can be overridden by the user. The user can also optionally upload attachment, specifically picture attachment for one log entry that is the "transfer proof" (bukti transfer). Upon submitting, the log entry will also be put in the history element, which is under the card carousel.
2. The app has three navigation buttons in the bottom bar: Home, Groups, and Profile. When the user navigates to Groups, all the groups that they manage will be listed. Clicking on an item will open up the detail screen, which will show a similar card to the one in the carousel (clicking the carousel item also navigates to the detail page), with the card containing at least the group name and the remaining balance. Under the information card, the history log of the transactions on that group (income and expense (i.e. it has been rolled)) are shown, with pagination of the months similar to a filter card at the top. Between the card and the history, there are two buttons: Roll and Members. Clicking on the Roll button will navigate to the rolling page for the group and the user can customize which members of the group are candidates of getting the money pool. Clicking on the Members button simply shows all members within the group, organized in a list.
3. Navigating to the Profile page shows a typical profile page and the items. At the very top is the user information itself, such as the profile picture, name, and the subscription type (for the MVP, everyone that signs in is considered Pro users). Below are lists such as the Settings button, which when clicked contains at least the following options: Language (ID and EN), Color mode (Default or Material You), and Dark mode toggle.
Ensure all states (Loading, Success, Error, Empty) are explicitly handled with visual components.
```

---

## 3. UI/UX DESIGN & DESIGN TOKENS (DOWN TO THE PIXEL)

Use this section to outline the exact Material 3 tokens for the AI engine to translate into your theme files (`Theme.kt`, `Color.kt`).

### 3.1 Material 3 Color Palette

|**Token Name**|**Hex Code**|**App Context Usage**|
|---|---|---|
|`primary`|`#FF003F`|High-emphasis UI elements, FABs, prominent buttons|
|`on-primary`|`#FFFFFF`|Text and icons displayed on top of `primary`|
|`primary-container`|`#FFDAD9`|Standout container fills, selected states, badges|
|`on-primary-container`|`#41000A`|Text and icons displayed on `primary-container`|
|`secondary`|`#7B5354`|Less prominent components, filter chips, utility links|
|`on-secondary`|`#FFFFFF`|Text and icons displayed on top of `secondary`|
|`secondary-container`|`#FFDAD9`|Fills for secondary components and tonal selection|
|`on-secondary-container`|`#2C1114`|Text and icons displayed on `secondary-container`|
|`tertiary`|`#EFBF04`|Accents, celebratory states, contrasting callouts|
|`on-tertiary`|`#000000`|Text and icons displayed on top of `tertiary`|
|`tertiary-container`|`#FFDF96`|Fills for tertiary elements, warning/alert banners|
|`on-tertiary-container`|`#261900`|Text and icons displayed on `tertiary-container`|
|`error`|`#BA1A1A`|Destructive actions, error states, invalid inputs|
|`on-error`|`#FFFFFF`|Text and icons displayed on top of `error`|
|`error-container`|`#FFDAD6`|Backgrounds for error alerts and text field errors|
|`on-error-container`|`#410002`|Text and icons displayed on `error-container`|
|`background`|`#FFF8F7`|Underlying screen background behind scrollable content|
|`on-background`|`#221A1A`|Primary body text and system icons on the background|
|`surface`|`#FFF8F7`|Structural components, cards, sheets, dialogs, menus|
|`on-surface`|`#221A1A`|Title text and primary icons displayed on surfaces|
|`surface-variant`|`#F4DDDC`|Search bars, unselected states, structural headers|
|`on-surface-variant`|`#524343`|Secondary text, captions, and placeholder text|
|`outline`|`#857372`|Input field borders, switches, visible dividers|
|`outline-variant`|`#D7C1C1`|Decorative borders, low-contrast component lines|
|`surface-dim`|`#E4D7D6`|Dimmed surface behind active dialogs or overlays|
|`surface-bright`|`#FFF8F7`|Brightened surface state for focus or active states|
|`surface-container-lowest`|`#FFFFFF`|Lowest elevation container (e.g., pure white cards)|
|`surface-container-low`|`#FCEEEF`|Low-elevation surface card containers|
|`surface-container`|`#F7E8E8`|Default container fill for standard elevation cards|
|`surface-container-high`|`#F1E3E2`|High-elevation container fills for prominent sheets|
|`surface-container-highest`|`#EBE0DF`|Highest elevation container fills for top-level dialogs|
|`inverse-surface`|`#382E2E`|High-contrast dark backgrounds (e.g., Snackbars)|
|`inverse-on-surface`|`#F7EEED`|Light text and actions displayed on `inverse-surface`|
|`inverse-primary`|`#FFB3B9`|Primary interactive accents within inverse layouts|

### 3.2 Typography Rules

* **App Bar & Headers:** `MaterialTheme.typography.titleLarge` → `Libre Baskerville`, Weight: `Bold`
* **Body Text:** `MaterialTheme.typography.bodyMedium` → `Montserrat`, Weight: `Normal`
* **Microcopy / Captions:** `MaterialTheme.typography.labelSmall` → Weight: `Light`

### 3.3 Spacing, Padding & Elevation Grid

* **Screen Margins:** `16.dp` uniform outer boundary padding.
* **Component Spacing:** `8.dp` or `12.dp` vertical/horizontal spacers between elements.
* **Card Corner Radius:** `12.dp` or `16.dp` rounded corners (`RoundedCornerShape`).
* **Surface Elevation:** `CardDefaults.cardElevation(defaultElevation = 2.dp)`.

---

## 4. SCREEN-BY-SCREEN DEEP DIVE

### Screen 1: `Home Dashboard`

* **Screen Layout Structure:** `Scaffold` container featuring a bottom navigation bar and a top action bar.
* **Visual Layout Component Tree:**
* `TopAppBar`: Title left-aligned, profile icon right-aligned.
* `LazyColumn` (Scrollable List):
* *Item 1:* Hero Summary Card with an asymmetric gradient background (`primary` to `secondary`).
* *Item 2:* Horizontal grid of recent user interactions (`LazyRow`).


* `FloatingActionButton`: Bottom right anchored, `16.dp` off-screen padding, triggering the creation flow.


* **UI State Variations:**
* *Loading State:* Show a centered `CircularProgressIndicator` with a muted shimmer effect on list items.
* *Empty State:* Centered vector asset (`Nano Banana` or custom generated icon) with a `bodyMedium` call-to-action text: `"No items found. Tap + to start."`

### Screen 2: `Group Detail`

* **Screen Layout Structure:** `Scaffold` container featuring a bottom navigation bar and a top action bar.
* **Visual Layout Component Tree:**
* `TopAppBar`: Title left-aligned, profile icon right-aligned.
* `LazyColumn` (Scrollable List):
* *Item 1:* Hero Summary Card with an asymmetric gradient background (`primary` to `secondary`).
* *Item 2:* Horizontal grid of recent user interactions (`LazyRow`).



* **UI State Variations:**
* *Loading State:* Show a centered `CircularProgressIndicator` with a muted shimmer effect on list items.
* *Empty State:* Centered vector asset (`Nano Banana` or custom generated icon) with a `bodyMedium` call-to-action text: `"No items found. Tap + to start."`

### Screen 3: `Profile`

* **Screen Layout Structure:** `Scaffold` container featuring a bottom navigation bar and a top action bar.
* **Visual Layout Component Tree:**
* `TopAppBar`: Title left-aligned, profile icon right-aligned.
* `LazyColumn` (Scrollable List):
* *Item 1:* Hero Summary Card with an asymmetric gradient background (`primary` to `secondary`).
* *Item 2:* Horizontal grid of recent user interactions (`LazyRow`).


* `FloatingActionButton`: Bottom right anchored, `16.dp` off-screen padding, triggering the creation flow.


* **UI State Variations:**
* *Loading State:* Show a centered `CircularProgressIndicator` with a muted shimmer effect on list items.
* *Empty State:* Centered vector asset (`Nano Banana` or custom generated icon) with a `bodyMedium` call-to-action text: `"No items found. Tap + to start."`

---

## 5. DATA ARCHITECTURE & LOCAL STORAGE

Because AI Studio builds are entirely client-side out of the box, state preservation must map to device-only solutions.

* **UI State Model:** Jetpack `ViewModel` exposing a unified Kotlin `StateFlow<UiState>` to the Compose views.
* **Local Storage Engine:** `Room Database`
* **Data Models:**
```kotlin
// Example data schema representation for the agent
data class AppItem(
    val id: Long = 0,
    val title: String,
    val timestamp: Long,
    val aiGeneratedContent: String
)
```

---

## 6. HARDWARE CAPABILITIES & PERMISSIONS

* `[ ]` **Internet:** Used ONLY for syncing data, similar to Obsidian.
* `[ ]` **Contact:** For quickly adding new members to a group.
* `[ ]` **Storage / Media Access:** Reading localized images.

---

## 7. HARD DEPLOYMENT CRITERIA (GOOGLE PLAY TRACK)

* **Minimum Android SDK support:** API Level 26 (Android 8.0)
* **Target Android SDK version:** API Level 34 / 35
* **Play Track Target:** `Internal Testing Track` via direct AI Studio console publishing connection.

```</UiState>

```
