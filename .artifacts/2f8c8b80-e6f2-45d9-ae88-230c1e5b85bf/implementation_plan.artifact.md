# Implementation Plan - Fix User Registration Failure

The user is unable to register new accounts because the app incorrectly reports that the user already exists for any email entered. This is due to a missing database column in the table creation script, causing all insertions to fail.

## User Review Required

> [!IMPORTANT]
> The error message in the UI is misleading. It states "email might exist" whenever the database insertion fails for *any* reason. I will update this to be more generic, or better yet, verify email existence explicitly.

## Proposed Changes

### Database Logic

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/DatabaseHelper.kt)
- Update `onCreate` to include `COLUMN_LOCATION` in the `users` table definition. This ensures fresh installs have the correct schema for version 9.
- Add an explicit `isEmailExists(email: String): Boolean` method to allow the UI to distinguish between "email taken" and "system error".

### UI Logic

#### [MODIFY] [SignUpActivity.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/SignUpActivity.kt)
- Before calling `addUser`, check if the email already exists using the new `isEmailExists` method.
- Show a specific "Email already registered" message if it exists.
- Show a "Registration failed due to a system error" message if `addUser` still fails (returns `-1`).

## Verification Plan

### Automated Tests
- I will attempt to run a build to ensure no syntax errors.
- Since I cannot easily run instrumentation tests here, I will rely on code analysis.

### Manual Verification
- The user should try registering with a new email after these changes.
- To test the "already exists" case, they should try registering with an email they know is already in the system (e.g., `admin@mytutor.com`).
