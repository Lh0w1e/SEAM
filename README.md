# SEAM
Student Event Announcement Monitoring

Master Password :	adm1n2018

APP SCOPES PROCESS
1. Splash screen
2. Login form/Create new account
3. User Registration
	- if the user has select "Admin" user type, enter the Master Password
4. Admin Form
	- can add new Event
		- after save the new event, the user form receives notification.
	- can edit event
	- can delete event
5. User Form
	- can view events
6. Account Settings
	- info of user
	- can be edited and update


APP VALIDATIONS
1. Login
	- if the username and password is empty, Error message will display.
2. Registration
	- if all fields are empty, error message will display.
	- if password not matched to confirm password, error message will display.
3. Admin Form/User Form
	- if the device is not connected to internet, error message will display.
4. Add new event (Admin Form)
	- if all fields are empty, error message will display.
	- if selected date is current date and selected time is already passed, error message will display.
5. Edit Event (Admin Form)
	- if all fields are empty, error message will display.
	- if selected date is current date and selected time is already passed, error message will display.

APP LIMITATIONS
1. the device of the user will only receive the latest notification if the device is connected to the internet
   and the app is open. 
