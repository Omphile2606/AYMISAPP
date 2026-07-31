# Implement Dropdown Lists in BookingFragment

This plan outlines the steps to replace the static `TextView` labels for Subject, Date, and Tutor with modern Material Design dropdown menus (Exposed Dropdown Menus) in the Booking screen.

## User Review Required

> [!IMPORTANT]
> The current `BookingFragment.kt` refers to several IDs that are missing from `fragment_booking.xml` (like `tutorAutoComplete`, `subjectGroup`, `calendarView`). This plan will align the XML with the code's expectations while satisfying your request for dropdown lists.

## Proposed Changes

### [Resources]

#### [MODIFY] [fragment_booking.xml](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/res/layout/fragment_booking.xml)
- Replace "Select Subject" `TextView` with a `TextInputLayout` and `AutoCompleteTextView`.
- Replace "Select Date" `TextView` with a `TextInputLayout` and `AutoCompleteTextView` (configured to show a Date Picker).
- Replace "Select Tutor" `TextView` with a `TextInputLayout` and `AutoCompleteTextView`.
- Add a `TextView` for the Tutor's bio (as expected by the current Kotlin code).

#### [MODIFY] [strings.xml](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/res/values/strings.xml)
- Add a string array for Subjects (Mathematics, Science, English, etc.).

### [Source Code]

#### [MODIFY] [BookingFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/BookingFragment.kt)
- Initialize the Subject dropdown using an `ArrayAdapter` with the new string array.
- Initialize the Date field to show a `DatePickerDialog` when clicked, updating the text with the selected date.
- Ensure the Tutor dropdown logic correctly uses the new XML IDs.

## Verification Plan

### Automated Tests
- Run the app and navigate to the Booking screen.
- Verify that clicking "Select Subject" shows a list of subjects.
- Verify that clicking "Select Date" opens a calendar picker.
- Verify that clicking "Select Tutor" shows the list of tutors from the database.
- Click "Book Now" and ensure the selection is captured and saved correctly.

### Manual Verification
- Check the layout on different screen sizes to ensure the dropdowns are correctly aligned and padded.
