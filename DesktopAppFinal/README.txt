User Manual for Attendance Team 54 Project

---------------------------------------------

**CHANGE THE DATABASE CONNECTION INFO IN: src/main/resources/.env**

From the main menu, click add class at the bottom right to add a new class. Right click class tiles to edit start and end dates, quiz password, open or close the quiz for students, or delete the class.

In the class view, left click students to change their current attendance status. Right click students to view and change a specific student's attendance history. 

The file menu in the ribbon allows the user to import CSV's, export the current attendance table to a CSV, and to return to the main menu with or without saving.

IMPORTANT:
**Saving means "locking in" the attendance for the day, which will mark the student's attendance for the day based on their current status. Furthermore, student attendance on a given day cannot be "duplicated". If attendance has already been taken for a given day, saving will simply overwrite.**

The view menu in the ribbon allows the user to toggle between the quiz table view and the student table view. In the quiz table view, the user can right click to add a new question, or edit/delete any existing quiz question.

The table menu in the ribbon allows the user to "reset attendance," which sets all students to absent for the day. 
IMPORTANT:
**RESET ATTENDANCE SHOULD BE DONE AT THE START OF EVERY CLASS PERIOD**.
The "Lock In Attendance" acts as the save button described earlier.
The "Refresh Table from Database" button updates the table to reflect what is currently in the database. This should be done before saving/locking in the attendance in case a student has submitted the quiz and the table hasn't yet been updated to reflect them being present.

Finally, the class menu in the ribbon simply allows the user to switch to other classes from within a class view. This was made primarily for testing purposes but is a useful feature, so it was kept.