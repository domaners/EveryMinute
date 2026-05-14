# Project Plan

EveryMinute: An Android app for managing Junior football teams. The app allows coaches to manage teams, players, and fixtures. It features an interactive pitch for formation planning, tracks match statistics like minutes played, and supports sharing with parents in a read-only mode. All data is synced via Firebase.

## Project Brief

# Project Brief: EveryMinute

## Features
1. **Team & Roster Management**: Create and manage a junior football team roster, allowing coaches to add players with profile photos and track their active/inactive status.
2. **Fixture & Availability Tracking**: Schedule match fixtures with venue details and integrated availability tracking to ensure the coach knows who can play each week.
3. **Live Pitch & Formation Planner**: An interactive half-pitch view using drag-and-drop mechanics to plan formations, set starting lineups, and manage substitutions in real-time.
4. **Match Statistics & Summary**: Record post-match results, including goal scorers and assists, while automatically calculating total minutes played for each athlete.
5. **User Authentication**: Firebase Auth (Google and Email).
6. **Data Storage**: Firebase Firestore for real-time cloud sync.
7. **Sharing**: Role-based access for Coaches (edit) and Parents (read-only).
8. **Dashboard**: Next/previous fixtures with prompts for missing details.
9. **Player Profiles**: Detailed stats, history, and descriptions.

## High-Level Technical Stack
- Kotlin
- Jetpack Compose (Material Design 3, Edge-to-Edge)
- Jetpack Navigation
- Firebase (Auth, Firestore)
- Kotlin Coroutines & Flow
- Coil (Image loading)

## Implementation Steps

### Task_1_Infrastructure_Auth: Initialize Firebase (Auth & Firestore), set up Navigation, and implement Authentication. Create the base data models and a placeholder Dashboard.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Firebase project integrated with Auth and Firestore
  - Google and Email/Password authentication functional
  - Navigation3 structure with Dashboard, Team, and Fixtures screens
  - Player, Fixture, and Team data models defined
- **StartTime:** 2026-05-14 13:16:31 BST

### Task_2_Team_Fixture_Management: Implement Player and Fixture management features including profiles and availability tracking.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Player CRUD (Add/Edit/Delete) with Firestore sync and Coil image loading
  - Detailed Player Profile screens
  - Fixture management (Schedule/Edit) with availability tracking logic
  - Dashboard shows next/previous fixtures with status prompts

### Task_3_MatchDay_Pitch_Formation: Develop the interactive pitch for formation planning and match statistics tracking.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Interactive half-pitch UI with drag-and-drop mechanics for formations
  - Real-time substitution management and lineup setting
  - Match stats recording (goals, assists, minutes played)
  - Role-based access (Coach edit vs Parent read-only) implemented

### Task_4_UI_Refinement_Verification: Apply Material 3 styling, generate assets, and perform final verification.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Vibrant Material 3 theme with Dark/Light support and Edge-to-Edge display
  - Adaptive app icon matching the football/management theme
  - Application builds and runs successfully (assembleDebug)
  - Verify stability, no crashes, and alignment with all requirements

