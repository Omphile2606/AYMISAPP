# Implementation Plan - Find Tutor Functionality

Implement the filtering and booking logic for the "Find Tutor" screen. This includes updating the database to support location-based filtering, creating a list view for tutors, and handling the booking process.

## User Review Required

> [!IMPORTANT]
> - I will be adding a `location` column to the `users` table to support the requested filtering.
> - The subject filtering will be based on searching for the subject keywords within the tutor's biography, as there is currently no separate `subject` field for tutors.
> - A new layout `item_tutor.xml` will be created to display tutor details in the search results.

## Proposed Changes

### Database Layer

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/DatabaseHelper.kt)
- Increment `DATABASE_VERSION` to 9.
- Add `COLUMN_LOCATION` to `TABLE_USERS`.
- Update `onCreate` to include the location column.
- Update `onUpgrade` to add the location column for existing installations.
- Update `preloadTutors` to assign locations (e.g., "Cape Town", "Johannesburg", "Online").
- Add `searchTutors(subject: String?, location: String?): List<User>` method.
- Update `User` data class to include `location`.
- Update `getUser` and `getAllTutorsWithBio` to populate the `location` field.

### UI Layer

#### [NEW] [item_tutor.xml](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/res/layout/item_tutor.xml)
- Create a CardView-based layout for tutor list items.
- Include Tutor Name, Subject/Bio snippet, Location, and a "BOOK" button.

#### [NEW] [TutorRecyclerAdapter.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/TutorRecyclerAdapter.kt)
- Implement a `RecyclerView.Adapter` for the tutor list.
- Add a click listener for the "BOOK" button.

#### [MODIFY] [FindTutorFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/FindTutorFragment.kt)
- Set up `DatePickerDialog` for the `dateAutoComplete` field.
- Populate `subjectAutoComplete` using `R.array.subjects_array`.
- Populate `locationAutoComplete` with a list of locations.
- Implement `filterButton` click logic:
    - Extract filters (subject, location, date).
    - Query database for matching tutors.
    - Update the `timeTable` RecyclerView with results.
- Implement booking logic when "BOOK" is clicked:
    - Verify all filters (especially Date) are selected.
    - Call `databaseHelper.addBooking`.
    - Show success toast and navigate to the Home screen.

## Verification Plan

### Automated Tests
- No specific automated tests planned, but will verify build stability.

### Manual Verification
1. Open the "Find Tutor" screen.
2. Select a Subject (e.g., Mathematics).
3. Select a Location (e.g., Cape Town).
4. Select a Date using the date picker.
5. Click "APPLY FILTER".
6. Verify that matching tutors appear in the list below.
7. Click "BOOK" on a tutor.
8. Verify that a "Booking Successful" toast appears.
9. Verify that the new booking appears on the Home screen's session list.
