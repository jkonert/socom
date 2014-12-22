package de.tud.kom.socom.web.client.htmlelements;

import java.util.Date;

import com.chj.gwt.client.soundmanager2.Callback;
import com.chj.gwt.client.soundmanager2.Option;
import com.chj.gwt.client.soundmanager2.SMSound;
import com.chj.gwt.client.soundmanager2.SoundManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.util.FileUtils;
import de.tud.kom.socom.web.client.util.DateTimeUtils.Units;
import de.tud.kom.socom.web.client.util.FileUtils.FileState;

/** Provides the GWT implementation of an AudioControl with several DIVs for Play, Stop, Progress and Time
 * 
 * @author jkonert
 *
 */
public class AudioControl extends Composite {

	@UiTemplate("AudioControl.ui.xml")
	interface AudioControlUiBinder extends UiBinder<Widget, AudioControl> { }
	
	private static AudioControlUiBinder uiBinder = GWT.create(AudioControlUiBinder.class);		

	@UiField protected HTMLPanel audioControl; // the whole control field
	@UiField protected Label audioPlay;
	@UiField protected Label audioStop;
	@UiField protected DivElement audioProgressCurrent; // basically only set width from 0 to 100
	@UiField protected DivElement audioTime; // may be filled with 00:00:00 / 00:00:00  information
	@UiField protected DivElement audioLoading; // may be filled with "Loading..." information
	@UiField AudioStyle style; // mapping the styles from xml file (only the onces defined in interface)
	
	interface AudioStyle extends CssResource
	{
		String audioStopDisabled();
		String audioPlayDisabled();
	}
	
	private String filePath;
	private SoundManager soundManager;
	private String soundID;
	private SMSound sound;
	
	private NumberFormat digits = NumberFormat.getFormat("00");
	
	private boolean playEnabled = true;
	private boolean stopEnabled = false;
	
	private boolean playing = false;
	private boolean paused = false;

	private long totalMillis = -1;
	private long currentMillis = -1;

	private boolean isBlinkLoading = false;

	
	public AudioControl()
	{
		initWidget(uiBinder.createAndBindUi(this));		
	}
	
	
	public AudioControl(String filePath)
	{
		this();
		this.setFilePath(filePath);
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	
	@UiHandler("audioPlay")
	protected void onPlayButtonClicked(ClickEvent event)
	{
		if (!playEnabled) return;
		if (isPlaying()) pause();
		else if (paused) resume();
		else play();
	}
	
	@UiHandler("audioStop")
	protected void onStopButtonClicked(ClickEvent event)
	{
		if (!stopEnabled) return;
		if (playing || paused) stop();				
	}
	
	
	public void play()
	{
		startLoading();
		soundManager.onReady(new Callback(){
			public void execute(){
				soundManager.play(soundID);	
				playing = true;
				paused = false;
				setEnableStopButton(true);				
			}					
		});	
	}
	
	public void stop()
	{
		//startLoading();
		if (soundManager == null) return;
		soundManager.onReady(new Callback(){
			public void execute(){
				soundManager.stop(soundID);
				playing = false;
				paused = false;
				setEnableStopButton(false);
				audioTime.setInnerText("");
				audioProgressCurrent.getStyle().setWidth(0, Unit.PX);
				stopBlinkLoading();
			}					
		});	
	}
	
	public void pause()
	{
		if (!playing) return;		
		startLoading();
		soundManager.onReady(new Callback(){			
			public void execute(){
				soundManager.pause(soundID);	
				playing = false;
				paused = true;				
			}					
		});	
	}
	
	public void resume()
	{
		if (playing || !paused) return;		
		startLoading();
		soundManager.onReady(new Callback(){
			public void execute(){
				soundManager.resume(soundID);	
				playing = true;
				paused = false;
			}					
		});	
	}

	public void startLoading()
	{
		if (getReadyState().equals(FileState.error))
		{ // on error clear everything and restart
			this.soundManager.destroySound(this.soundID);
			this.sound = null;
			this.soundManager = null;
		}
		if (soundManager == null) 
		{
			soundManager = SoundManager.quickStart();
			soundID = HTMLPanel.createUniqueId();
			soundManager.onReady(new Callback(){
				public void execute() {
					// see Documentation at http://www.schillmania.com/projects/soundmanager2/doc/#sound-object-properties
					// or https://github.com/rcaloras/gwt-soundmanager2
					soundManager.createSound(new Option[] {
					   new Option("id",soundID),
					   new Option("url", filePath),
					   new Option("stream", true),
					   new Option("whileloading", new Callback() {
					   		public void execute() {AudioControl.this.blinkLoading();}
					   		}),
					   new Option("whileplaying",new Callback() {
						    public void execute() {AudioControl.this.updateTimes();}
					   		}),
					   new Option("onfinish",new Callback() {
							    public void execute() {AudioControl.this.stop();}
						   	})
//					   new Option("onstop", new Callback(){
//						   		public void execute() {AudioControl.this.stop();}
//					   		})
					   });
					   AudioControl.this.sound = soundManager.getSoundById(soundID);					  
					};
				});
		}
	}
	
	public boolean isPlaying()
	{
		if (getReadyState().equals(FileUtils.FileState.error))
		{
			stop();
		}
		return playing;
	}
	
	/** 
	 * 
	 * @return returns the current state of a file to load/show. If not yet tried to play, the state is always uninitialized
	 */
	public FileState getReadyState() {
		if (soundManager != null && this.sound != null)
		{
			switch (this.sound.getReadyState())
			{//  (0=uninitialized, 1=loading, 2=error, 3=success/loaded)
			case 1: return FileState.loading;
			case 2: return FileState.error;
			case 3: return FileState.success;
			case 0: // fall-through
			default:
				// fall-through
			}
		}
		return FileState.uninitialized;
	}


	/**
	 * @return -1 if no sound is loaded yet, total time in millis otherwiese
	 */
	public long getMillisTotalTime()
	{
		return totalMillis;
	}
	
	/**
	 * @return -1 if no sound is loaded yet, current time index of song otherwise (updated only each second)
	 */
	public long getMillisCurrentTime()
	{
		return currentMillis;
	}
	
	private void setEnableStopButton(boolean enable)
	{
		if (enable)
		{
			audioStop.removeStyleName(style.audioStopDisabled());
			stopEnabled = true;
		}
		else
		{
			audioStop.addStyleName(style.audioStopDisabled());
			stopEnabled = false;
		}
	}
	
	// called by soundfile loop itself
	private void updateTimes()
	{		
    	if (sound == null) return;
		double width = 0;
    	long dur = sound.getDuration();
    	if (dur <= 0 ) dur = sound.getDurationEstimate();
    	if (dur > 0)
    	{
    		long position = sound.getPosition();
    		width = (position/((double)dur))*100;
    		audioProgressCurrent.getStyle().setWidth(width, Unit.PCT);
    		
    		long s = Units.SECONDS.toNextUnitRest(position);
    		long m = Units.SECONDS.toNextUnit(position);
    		long h = Units.MINUTES.toNextUnit(m);
    		m = Units.MINUTES.toNextUnitRest(m);
    		long S = Units.SECONDS.toNextUnitRest(dur);
    		long M = Units.SECONDS.toNextUnit(dur);
    		long H = Units.MINUTES.toNextUnit(M);
    		M = Units.MINUTES.toNextUnitRest(M);
    		audioTime.setInnerText(digits.format(h)+":"+digits.format(m)+":"+digits.format(s)+" / "+digits.format(H)+":"+digits.format(M)+":"+digits.format(S));
    		totalMillis = dur * 1000; //commes in seconds, we need millis
    		currentMillis = position * 1000; //commes in seconds, we need millis
    	}
    	if (sound.getLoaded()) stopBlinkLoading();
	}
	

	
	private void blinkLoading()
	{
		long res = new Date().getTime()%100;
		if ( res > 0 && res < 70) audioLoading.addClassName("hidden");
		else
		{
			audioLoading.removeClassName("hidden");
			isBlinkLoading = true;
		}
	}
	
	private void stopBlinkLoading()
	{
		if (isBlinkLoading )
		{
			audioLoading.addClassName("hidden");
			isBlinkLoading=false;
		}
	}
}
