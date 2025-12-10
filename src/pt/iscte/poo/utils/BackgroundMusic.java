package pt.iscte.poo.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusic {

    //O clip é um objeto da javax.sound.sampled. Guarda o áudio carregado na memória.
    private Clip clip;

    //Guarda o estado de muted (quando o muted está a true, a música desliga e quando está a false, a música está ligada).
    private boolean muted = false;

    public BackgroundMusic(String filePath) {
        try {
            //Cria um objeto File que representa o caminho/path para o ficheiro no disco. Não abre o ficheiro, apenas cria a referência para o ficheiro.
            File audioFile = new File(filePath);
            //Abre e lê o conteúdo do ficheiro
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            //Pede ao sistema de som que crie um Clip adequado ao hardware. Ainda não tem áudio nenhum lá dentro
            clip = AudioSystem.getClip();
            //Carrega o ficheiro de audio para a memória RAM. O Clip só sabe ler AudioInputStreams
            clip.open(audioStream);

        //O Java não conseguiu interpretar o formato do ficheiro de audio (ex: o ficheiro de audio não é um wav; o ficheiro está corrompido, etc).
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato de áudio não suportado: " + e);
        //Ocorreu um erro ao aceder ao ficheiro no disco (ex: o ficheiro não existe, o ficheiro está num path errado; o programa não tem permissões para ler, etc).
        } catch (IOException e) {
            System.err.println("Erro ao ler o ficheiro de áudio: " + e);
        //O sistema de áudio não conseguiu fornecer um Clip ou não conseguiu abrir o Clip (ex: o sistema está sem memória para linha de áudio; outro programa bloqueou o dispositivo.
        } catch (LineUnavailableException e) {
            System.err.println("Linha de áudio não disponível: " + e);
        }
    }

    //Liga a música e coloca a tocar em loop infinito, se o clip não for null e se ele não estava a tocar previamente
    public void playLoop() {
        if (clip != null && !clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            muted = false;
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            muted = true;
        }
    }

    //Liga/desliga a música. Isto permite que o jogador pressione M para alternar o estado da música (On/Off).
    public void toggle() {
        if (clip == null) return;

        if (clip.isRunning()) {
            stop();
        } else {
            playLoop();
        }
    }
}
