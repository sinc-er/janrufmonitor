package de.janrufmonitor.service.sound;

public class SoundThread extends Thread {
    
    String filename;
    
    public SoundThread(String filename) {
        this.filename = filename;
    }
    
    public void run(){
        if (this.filename!=null) {
            SoundFile sf = new SoundFile(filename);
            sf.play();
        }
    }
    
}
