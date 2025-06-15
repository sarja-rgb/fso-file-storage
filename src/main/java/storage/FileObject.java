package storage;

/**
  * FileObject holds the result of an operation on a file, including metadata.
*/
public class FileObject {
    private String fileName;
    private String fileType;
    private String bucketName;
    private long lastModifiedDate;
    private long fileSize;
    private String filePath;
    
    public String getFileName() {
      return fileName;
    }
    public void setFileName(String fileName) {
      this.fileName = fileName;
    }
    public String getFileType() {
      return fileType;
    }
    public void setFileType(String fileType) {
      this.fileType = fileType;
    }
    public String getBucketName() {
      return bucketName;
    }
    public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
    }
    public long getLastModifiedDate() {
      return lastModifiedDate;
    }
    public void setLastModifiedDate(long lastModifiedDate) {
      this.lastModifiedDate = lastModifiedDate;
    }
    public long getFileSize() {
      return fileSize;
    }
    public void setFileSize(long fileSize) {
      this.fileSize = fileSize;
    }
    
    public String getFilePath() {
      return filePath;
    }
    public void setFilePath(String filePath) {
      this.filePath = filePath;
    }

    public static FileObjectBuilder builder(){
       return new FileObjectBuilder();
    }


    public static class FileObjectBuilder{
        private final FileObject fileObject;

        public FileObjectBuilder(){
           this.fileObject = new FileObject();
        }

        public FileObjectBuilder setFileName(String fileName) {
            this.fileObject.setFileName(fileName);
            return this;
        }

        public FileObjectBuilder setFileType(String fileType) {
          this.fileObject.setFileType(fileType);
          return this;
        }
  
        public FileObjectBuilder setBucketName(String bucketName) {
          this.fileObject.setBucketName(bucketName);
          return this;
        }

        public FileObjectBuilder setLastModifiedDate(long lastModifiedDate) {
          this.fileObject.setLastModifiedDate(lastModifiedDate);
          return this;
        }
   
        public FileObjectBuilder setFileSize(long fileSize) {
           this.fileObject.setFileSize(fileSize);
           return this;
        }

          public FileObjectBuilder setFilePath(String filePath) {
           this.fileObject.setFilePath(filePath);
           return this;
        }
        public FileObject build(){
          return fileObject;
        }
    }
}