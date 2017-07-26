package manipulation.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JLabel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import manipulation.WorkFile;

/**
 * <p>Class handles Audio playback for WAV and MP3 files.
 * 
 * @author Ashley Allott
 */
public class AudioPlayer extends PlaybackListener{
	
	public static final int AUDIO_PLAYER_WAV = 1;
	public static final int AUDIO_PLAYER_MP3 = 2;
	private int audioType;
	private WorkFile workFile;
	
	private JLabel label;
	
	private Clip audio_clip;
	private long audio_pos = -1;
	private boolean audio_playing = false;
	
	PausablePlayer player;
	File audio_file;
	
	private Thread playback;
	private Thread coverTimerThread;
	
	/**
	 * Constructor, creates a new AudioPLayer
	 * 
	 * @param audioType		the type of audio format
	 * @param workFile		the WorkFile containing the file to play
	 * @param label			the JLabel which is used to display playback information
	 * @param covertext		boolean indicating if the player is aimed to play the stegotext
	 * @throws LineUnavailableException
	 * @throws IOException
	 * @throws JavaLayerException
	 */
	public AudioPlayer(int audioType, WorkFile workFile, JLabel label, boolean covertext) throws LineUnavailableException, IOException, JavaLayerException{
		this.audioType = audioType;
		this.workFile = workFile;
		this.label = label;
		if(covertext){
			this.audio_file = workFile.file;
		}else{
			this.audio_file = workFile.getTempStegoFile();
		}
		if(this.audioType == AudioPlayer.AUDIO_PLAYER_WAV){
			audio_clip = AudioSystem.getClip();
			audio_clip.open(workFile.getCoverAIS());
			label.setText("0s/"+ (audio_clip.getMicrosecondLength() / 1000000) + "s  ");
		}else if(this.audioType == AudioPlayer.AUDIO_PLAYER_MP3){
			player = new PausablePlayer(new FileInputStream(audio_file), this);
			label.setText("0s/"+ (workFile.mp3Codec.length/ 1000) + "s  ");
		}
	}
	
	/**
	 * <p>Controls the JLabel playback time information, showing elapsed and total time of the audio
	 */
	private void startAudioTimeTracking(){
		coverTimerThread = new Thread(new Runnable() {
			public void run() {
				try {
					while(audio_playing){
						if(audioType == AudioPlayer.AUDIO_PLAYER_WAV){
							if(audio_clip.getMicrosecondPosition() < audio_clip.getMicrosecondLength()){
								long currentTime = (long) Math.ceil(audio_clip.getMicrosecondPosition() / 1000000);
								long totalTime = (long) Math.ceil(audio_clip.getMicrosecondLength() / 1000000);
								label.setText((currentTime) + "s/" + (totalTime) + "s  ");
							}else{
								audio_playing = false;
								long currentTime = (long) Math.ceil(audio_clip.getMicrosecondLength() / 1000000);
								long totalTime = (long) Math.ceil(audio_clip.getMicrosecondLength() / 1000000);
								label.setText((currentTime) + "s/" + (totalTime) + "s  ");
							}
						}else if(audioType == AudioPlayer.AUDIO_PLAYER_MP3){
							long currentTime = (long) Math.ceil(player.getPosition() / 1000);
							long totalTime = (long) Math.ceil(workFile.mp3Codec.length/ 1000);
							label.setText((currentTime) + "s/" + (totalTime) + "s  ");
						}
						Thread.sleep(100);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		     }
		});  
		coverTimerThread.start();
	}
	
	@Override
    public void playbackFinished(PlaybackEvent event) {
    	audio_playing = false;
    	audio_pos = event.getFrame();
    }
	
	/**
	 * <p>Starts or resumes playback of the audio file.
	 * 
	 * @throws JavaLayerException
	 */
	public void play() throws JavaLayerException{
		AudioPlayer parent = this;
		playback = new Thread(
	    	new Runnable() {
	    		public void run() {
                    try {
                    	if(!audio_playing){
                			if(audio_pos != -1){
                				if(parent.audioType == AudioPlayer.AUDIO_PLAYER_WAV){
                					audio_clip.setMicrosecondPosition(audio_pos);
                					audio_pos = -1;
                					audio_clip.start();	
                				}else if(parent.audioType == AudioPlayer.AUDIO_PLAYER_MP3){
                					player.resume();
                				}
                			}else{
                				if(parent.audioType == AudioPlayer.AUDIO_PLAYER_WAV){
                					audio_clip.setMicrosecondPosition(0);
                					audio_clip.start();
                				}else if(parent.audioType == AudioPlayer.AUDIO_PLAYER_MP3){
                					player = new PausablePlayer(new FileInputStream(audio_file), parent);
                					player.play();
                				}
                			}
                			startAudioTimeTracking();
                			audio_playing = true;
                		}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
	    	});
		playback.start();
	}
	/**
	 * <p>Stops playback of the audio file.
	 */
	public void stop(){
		audio_playing = false;
		audio_pos = -1;
		if(this.audioType == AudioPlayer.AUDIO_PLAYER_WAV){
			audio_clip.stop();
		}else if(this.audioType == AudioPlayer.AUDIO_PLAYER_MP3){
			player.stop();
		}
	}
	/**
	 * <p>Pauses playback of the audio file.
	 */
	public void pause(){
		audio_playing = false;
		if(this.audioType == AudioPlayer.AUDIO_PLAYER_WAV){
			audio_pos = audio_clip.getMicrosecondPosition();
			audio_clip.stop();
		}else if(this.audioType == AudioPlayer.AUDIO_PLAYER_MP3){
			audio_pos = 0;
			player.pause();
		}
	}
	
}
