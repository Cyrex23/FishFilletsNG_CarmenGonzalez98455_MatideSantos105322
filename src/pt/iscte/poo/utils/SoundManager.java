package pt.iscte.poo.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private static Clip explosionClip;

    //Carrega o som de explosão uma única vez --> evita-se carregar o audio multiplas vezes
    public static void init() {
        //Código idêntico que está na classe BackgroundMusic
        if (explosionClip != null) return;

        try {
            File audioFile = new File("sounds/explosion.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            explosionClip = AudioSystem.getClip();
            explosionClip.open(audioStream);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato de áudio de explosão não suportado: " + e);
        } catch (IOException e) {
            System.err.println("Erro a ler o ficheiro de explosão: " + e);
        } catch (LineUnavailableException e) {
            System.err.println("Linha de áudio não disponível para explosão: " + e);
        }
    }

    public static void playExplosion() {
        //Se o init() falhou (por ficheiro inexistente, etc.), explosionClip será null.
        if (explosionClip == null) return;

        // Se já estiver a tocar, paramos e voltamos ao início.
        //O explosionClip é único para todas as bombas (há um único Clip para o som de explosão).
        //O Java não sabe se o som está a ser da bomba A ou da bomba B. Para ele é sempre o som de explosão, vindo sempre do mesmo Clip.
        //Se uma bomba explodir depois de outra, vai parar a meio o som de explosão da primeira bomba e vai reiniciar o som (começa o som da explosão da segunda bomba).
        if (explosionClip.isRunning()) {
            explosionClip.stop();
        }
        //Volta ao início do som da explosão
        explosionClip.setFramePosition(0);
        //Toca o som da explosão desde o início
        explosionClip.start();
    }

    private static class BombSoundExplosion extends Thread {

        public void run() {
            try {
                playExplosion();
            } catch (Exception e) {
                System.err.println("Erro ao criar a thread do som da explosão: " + e);
            }
        }
    }

    public static void playExplosionThread() {
        new BombSoundExplosion().start();
    }
}
