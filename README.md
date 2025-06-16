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

#### How to Run Unit & Integration Tests
This project uses JUnit and Mockito for unit testing, and supports AWS integration tests via Maven runtime parameters.

###  Run Tests with AWS Credentials (Integration Tests Enabled)
  mvn clean install \
    -Daws.accessKey=YOUR_ACCESS_KEY \
    -Daws.secretKey=YOUR_SECRET_KEY \
    -Daws.region=us-east-1 \
    -Daws.bucketName=your-s3-bucket-name
  
  Replace the placeholders with your actual AWS credentials and S3 bucket.

  This runs unit tests and any integration tests requiring a live AWS S3 connection.


##  How to Run the Application (Eclipse)
   - Right-click LocalFileStorageApp.java → Run As → Java Application
   - The Swing GUI window will launch, and the app will create a 'local_storage' folder in your project directory.

Meanwhile, the compiled output files will be generated in the `bin` folder by default.