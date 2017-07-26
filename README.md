# README - Code Submission
---

Source code located in 'trunk/SteganographyTool'

### List Of Files:
- src/main/java
	- StartUp.java
- src/main/java/analysis
	- ChiSquare.java
	- Histogram.java
	- SteganalysisTest.java
- src/main/java/controller
	- DecodeController.java
	- EncodeController.java
	- MainController.java
	- SteganalysisController.java
- src/main/java/cryptography
	- Cyptography.java
- src/main/java/manipulation
	- BitStream.java
	- BitStreamException.java
	- Codec.java
	- CoverFileFilter.java
	- Encoder.java
	- MessageTooLargeException.java
	- Utils.java
	- WorkFile.java
- src/main/java/manipulation/image
	- ImageEncoder.java
	- MesageTooLargeException.java
- src/main/java/manipulation/image/JPEG
	- Component.java
	- DataUnit.java
	- Huffman.java
	- JPEGCodec.java
	- MCU.java
- src/main/java/manipulation/sound
	- AudioEncoder.java
	- AudioPlayer.java
	- PausablePlauer.java
- src/main/java/manipulation/sound/MP3
	- Frame.java
	- Header.java
	- MP3Codec.java
- src/main/java/views
	- AudioPreview.java
	- ChiSquareChart.java
	- DecodeView.java
	- EncodeView.java
	- HistogramChart.java
	- ImagePreview.java
	- MainFrame.java
	- SteganalysisView.java
- src/main/test/java/steganographyToolTests
	- TestEmbedding.java
	- TestNavigation.java
	- TestSteganalysis.java
	- Utils.java
	
---
### Development Environment

The code supplied is in the format of a Spring Tool Suite Project.

Programs required:
- Java Runtime Environment: [http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
- Spring Tool Suite: [https://spring.io/tools](https://spring.io/tools)
- Gradle Build Tool: [https://gradle.org/](https://gradle.org/)

The project can be imported into a STS workspace via:
1. Installing STS
2. Installing the Gradle plugin: 
	- Open Dashboard ('Help/Dashboard')
	- Open Extension Manager ('IDE EXTENSIONS')
	- Install the plugin ('Language and Framework Tooling/Gradle (STS) Legacy Support')
3. Import the project 'code/trunk/SteganographyTool':
	- 'File/Import' -> Gradle (STS)/Gradle (STS) Project
	- Select the project root folder 'SteganographyTool'
	- Select Build Model
	- Select Generated project 'SteganographyTool'
	- Select Finish
4. Resolve any dependencies, if missing:
	- Install Gradle
	- Using a command line, navigate to the project root folder
	- Run the command 'gradle eclipse'
	
---

### Executable

The executable for the application is in the form of a .jar ('code/SteganographyTool.jar').

This can be run via:
1. Double clicking the executable in a file browser
2. Running from command line, via command: 'java -jar .\StegonagraphyTool.jar'

This can be generated from STS:
1. Ensure the development environment is set up correctly (see above)
2. Open 'File/Export' 
3. Select 'Java/Runnable JAR File', and 'Next'
4. Select 'StartUp' in the 'Launch Configuration' dropdown
5. Select 'Extract required libraries into generated JAR' in 'Library handling'
6. Select the desired output location, and generate the jar via 'Finish'

---

### Documentation

The code itself contains comments and javadocs, the later can be found at 'code/trunk/SteganographyTool/build/docs/javadoc', the root being 'index'.

To generate the javadocs:
1.	Run the command 'gradle javadoc' in the root folder from a command line
