package manipulation;
import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * <p>Class for specifying the files allowed in FileBrowsers, extending
 * Swings FileFilter.
 * 
 * @author Ashley Allott
 */
public class CoverFileFilter extends FileFilter{

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
            return true;
        }
 
        String extension = null;
        String fileString = file.getName();
        int i = fileString.lastIndexOf('.');
        if(i>0 && i< fileString.length()-1) {
            extension = fileString.substring(i+1).toLowerCase();
        }
        if(extension != null) {
            if(extension.equals("png") ||
               extension.equals("bmp") ||
               extension.equals("jpeg") ||
               extension.equals("jpg") ||
               extension.equals("mp3") ||              
               extension.equals("wav")) {
                    return true;
            }else{
                return false;
            }
        }
        return false;
	}

	@Override
	public String getDescription() {
		return "Suitable Covertext Formats (png, bmp, wav, jpeg, mp3)";
	}

}
