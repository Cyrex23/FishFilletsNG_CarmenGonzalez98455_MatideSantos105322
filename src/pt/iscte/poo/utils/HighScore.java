package pt.iscte.poo.utils;

import java.io.*;
import java.util.*;
import pt.iscte.poo.gui.ImageGUI;

public class HighScore {
    //Ficheiro onde vao ser armazenados os highscores e o nome do jogador
    private static final String file = "highscores.txt";

    //Função para chamar no winGame
    public void processWin(int bigFishTurns, int smallFishTurns, long totalSeconds) {

        String name = ImageGUI.getInstance().showInputDialog("YOU WON!",
                "Your time: " + totalSeconds + "\n\nEnter your name:");

        //Caso não dê o nome preenchemos com Anonymous
        if (name == null || name.trim().isEmpty()) {
            name = "Anonymous";
        }

        int totalTurns = bigFishTurns + smallFishTurns;
        //Vamos dar update ao txt de highscores, caso seja um dos novos top 10
        updateHighScores(name, totalTurns, totalSeconds);

        //Pop up para mostrar o top 10
        String leaderboard = getHighScoresBoard();

        //Cabeçalho da janela da tabela de highscores
        ImageGUI.getInstance().showMessage("~~~~~~~~~~HIGHSCORES~~~~~~~~~~", leaderboard);
    }

    //A função mantém a lista de HighScores com máximo de 10 jogadores, sempre do menor tempo para o maior tempo. Verifica se uma nova pontuação vai pertencer ao top 10
    private void updateHighScores(String playerName, int numTurns, long time) {
        //Vamos aceder ao txt e colocar os nomes, tempo e numero de turnos/movimentos dos peixes na lista com o método loadScores().
        List<NewScore> times = loadScores();

        //Caso a lista ainda não tenha 10 scores vamos simplesmente adicionar o novo score (porque é top 10)
        if (times.size() < 10) {
            times.add(new NewScore(playerName, time, numTurns));
            saveScores(times);
        }
        //Caso já haja 10 top scores, vamos ver se o que queremos adicionar tem um tempo inferior a algum da lista
        else {
            //Como não sabemos em que posição está o score com maior tempo (pior score), vamos nos posicionar na ultima pessoa
            int lowest = times.size() - 1;
            //Criamos a variavel para encontrar quem tem o maior tempo. Por agora vamos assumir que é quem vem em ultimo na lista.
            long lowestScore = times.get(lowest).time;

            //Vamos iterar a lista e ver quem tem o maior tempo
            for (int i = times.size() - 1; i >= 0; i--) {
                //Variavel que vamos usar para comparar com o lowestScore; vai guardar cada uma das pontuações
                NewScore currentPlayer = times.get(i);

                //Fazemos a comparação: se se verificar que este score (currentPlayer.time) tem maior tempo que lowestScore, guardamos os novos dados
                if (currentPlayer.time > lowestScore) {
                    //Novo pior score - maior tempo
                    lowestScore = currentPlayer.time;
                    //Guardamos a posição/linha da lista onde está o pior score
                    lowest = i;
                }
            }

            //Se o tempo do jogador for menor que o lowestScore fazemos a troca.
            //A pontuação deste player é melhor que uma do top 10 logo este será um novo elemento dos highscores
            if (time < lowestScore) {
                times.remove(lowest);
                times.add(new NewScore(playerName, time, numTurns));
                //O saveScores volta a fazer o sort
                saveScores(times);
            }
        }
    }

    //Função para chamar o pop up top 10/highcores
    //JOptionPane usa uma fonte proporcional (as letras visualmente não têm todas o mesmo tamanho). Não usa uma fonte monoespaçada, em que cada caracter
    //ocupa a mesma largura. Logo, por questões de formatação e para as colunas ficarem bem alinhadas, usou-se HTML.
    private String getHighScoresBoard() {
        List<NewScore> scores = loadScores();

        StringBuilder board = new StringBuilder();

        //O Swing (JOptionPane ou JLabel) interpreta qualquer string que comece por <html> como HTML.
        //A partir deste momento, tudo o que está dentro é desenhado como HTML, não como texto normal.
        board.append("<html>");
        board.append("<h3>* . º * º * . º * º TOP 10 º * º . * º * º . *</h3>");
        //Criamos uma tabela HTML com: cellspacing='4' → espaço entre as células; cellpadding='2' → espaço interno dentro das células
        board.append("<table cellspacing='4' cellpadding='2'>");

        //Cabeçalho
        //<tr> é table row (tudo o que está entre <tr> e </tr> pertence à linha da tabela) e <th> é table header (Cada <th> indica uma coluna da tabela).
        //Com o <th> o texto fica automaticamente em negrito e fica centrado
        board.append("<tr>")
                .append("<th>Position</th>")
                .append("<th>Name</th>")
                .append("<th>Seconds</th>")
                .append("<th>Movements</th>")
                .append("</tr>");

        //Linhas
        for (int i = 0; i < scores.size(); i++) {
            NewScore entry = scores.get(i);

            String name = entry.getNameTruncated();
            long time = entry.getTime();
            int turns = entry.getNumTurns();

            //<tr> indica o começo de uma nova linha na tabela. <td> é table data (célula normal da tabela).
            board.append("<tr>").append("<td>").append(i + 1).append("</td>")
                                .append("<td>").append(name).append("</td>")
                                .append("<td align='right'>").append(time).append("</td>")
                                .append("<td align='right'>").append(turns).append("</td>")
                                .append("</tr>");
        }

        board.append("</table>");
        board.append("</html>");

        return board.toString();
    }


    private List<NewScore> loadScores() {
        //Vamos criar a lista onde vão ser armazenados os jogadores
        List<NewScore> list = new ArrayList<>();
        //file é o highscore.txt
        File f = new File(file);
        if (f.exists()) {
            //Se o ficheiro existe vamos le-lo
            try (Scanner s = new Scanner(f)) {
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    //Cada dado do jogador vai estar separado por : (nome : segundos : numTurns)
                    String[] parts = line.split(": ");
                    //temos 3 dados precisamos de 3 parts
                    if (parts.length == 3) {
                        //Vamos atribuir cada elemento da linha a uma variavel (parts[0] : parts[1] : parts[2])
                        String name = parts[0];
                        long time = Long.parseLong(parts[1]);
                        //O tempo é um long, porque o tempo em segundos pode crescer para valores que ultrapassam o limite de um int,
                        //especialmente se um jogador deixar o jogo aberto durante muito tempo.
                        int numTurns = Integer.parseInt(parts[2]);
                        //Transforma os dados em NewScore e depois adiciona à lista
                        list.add(new NewScore(name, time, numTurns));
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("The file highscores.txt does not exist: " + e);
            }
        }
        //Considerando que a list iria estar por ordem de inserção, vamos ter de dar sort na lista.
        //O Collections.sort usa o compareTo. O critério é a comparação por tempo e se houver empate de tempo, desempata-se por número de movimentos total dos peixes.
        Collections.sort(list);
        return list;
    }

    private void saveScores(List<NewScore> list) {
        //Dar sort à lista antes de a guardar
        Collections.sort(list);
        //Vamos aceder ao txt; faz-se um try...catch caso o ficheiro nao exista.
        try (PrintWriter pw = new PrintWriter(new File(file))) {
            //Vamos preencher o ficheiro com os dados formatados da forma nome : time : numTurns
            for (NewScore player : list) {
                pw.println(player.name + ": " + player.time + ": " + player.numTurns);
            }
        } catch (FileNotFoundException e) {
            System.out.println("The file highscores.txt does not exist: " + e);
        }
    }
}