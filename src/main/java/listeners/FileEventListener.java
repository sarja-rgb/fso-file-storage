package listeners;

import storage.FileObject;
import util.FileEventExceptions;

public interface FileEventListener {
     public void onSave(FileObject fileObject) throws  FileEventExceptions; 

     public void onUpdate(FileObject fileObject) throws FileEventExceptions;

     public void onDelete(FileObject fileObject) throws FileEventExceptions;

}
