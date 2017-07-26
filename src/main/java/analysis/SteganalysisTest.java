package analysis;

import java.io.IOException;

import manipulation.WorkFile;

public interface SteganalysisTest {
	/**
	 * <p>Performs a steganalysis test on a WorkFile.
	 * 
	 * @param  workFile		media file to be analysed
	 * @return				boolean value indicated a test pass or failure 
	 * 						(true indicating presence of hidden message)
	 * @throws IOException
	 */
	public boolean performTest(WorkFile workFile) throws IOException;
}
