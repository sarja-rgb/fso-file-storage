# Local File Storage App (Java Swing)

This is a simple **Java Swing Desktop Application** that allows users to:

- Upload files to a local directory (`local_storage/`)
- Create folders
- View all uploaded files/folders in a table
- Delete selected files or folders

It acts as a mini file manager for local file storage using Java GUI.

-----------------------------------

###  Prerequisites:
- Install Java 17+

## Technologies Used
  GUI Framework -- Java Swing                             

##  Project Structure
The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

##  How to Run the Application (Eclipse)
   - Right-click LocalFileStorageApp.java → Run As → Java Application
   - The Swing GUI window will launch, and the app will create a 'local_storage' folder in your project directory.

> Alternatively, compile and run from terminal:

```bash
cd src
javac LocalFileStorageApp.java LocalStorageManager.java
java LocalFileStorageApp