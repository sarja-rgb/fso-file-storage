package storage;

import java.util.Date;

/**
  * FileObject holds the result of an operation on a file, including metadata.
*/
public class FileObject {
    private String fileName;
    private String fileType;
    private String bucketName;
    private Date lastModifiedDate;
    private long fileSize;
    private String filePath;
    private String checksum;
    private String version;

    
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
    public Date getLastModifiedDate() {
      return lastModifiedDate;
    }
    public void setLastModifiedDate(Date lastModifiedDate) {
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

    public String getChecksum() {
      return checksum;
    }
    public void setChecksum(String checksum) {
      this.checksum = checksum;
    }
    public String getVersion() {
      return version;
    }
    public void setVersion(String version) {
      this.version = version;
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileObject{");
        sb.append("fileName=").append(fileName);
        sb.append(", fileType=").append(fileType);
        sb.append(", bucketName=").append(bucketName);
        sb.append(", lastModifiedDate=").append(lastModifiedDate);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", filePath=").append(filePath);
        sb.append('}');
        return sb.toString();
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

        public FileObjectBuilder setLastModifiedDate( Date lastModifiedDate) {
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

          public FileObjectBuilder setCheckSum(String checksum) {
           this.fileObject.setChecksum(checksum);
           return this;
        }

        public FileObjectBuilder setVersion(String version) {
           this.fileObject.setVersion(version);
           return this;
        }

        public FileObject build(){
          return fileObject;
        }
    }
}