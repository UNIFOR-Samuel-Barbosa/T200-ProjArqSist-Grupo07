package br.edu.unifor.missaomarte.infrastructure.ranking;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileRankingRepository implements RankingRepository {
    private static final Pattern ENTRY = Pattern.compile(
            "\\{\\s*\"piloto\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,\\s*"
                    + "\"pontuacao\"\\s*:\\s*(-?\\d+)\\s*,\\s*"
                    + "\"dificuldade\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*,\\s*"
                    + "\"concluidaEm\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"\\s*}");
    private final Path arquivo;

    public FileRankingRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public List<RankingEntry> carregar() {
        if (Files.notExists(arquivo)) {
            return List.of();
        }
        try {
            String json = Files.readString(arquivo, StandardCharsets.UTF_8);
            Matcher matcher = ENTRY.matcher(json);
            List<RankingEntry> entradas = new ArrayList<>();
            while (matcher.find()) {
                entradas.add(new RankingEntry(unescape(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)), unescape(matcher.group(3)),
                        Instant.parse(unescape(matcher.group(4)))));
            }
            if (!json.isBlank() && entradas.isEmpty() && !json.trim().matches("\\[\\s*\\]")) {
                throw new IllegalStateException("Formato inválido no arquivo de ranking: " + arquivo);
            }
            return List.copyOf(entradas);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler o ranking em " + arquivo, exception);
        }
    }

    @Override
    public void salvar(List<RankingEntry> entradas) {
        String conteudo = entradas.stream()
                .map(entrada -> "  {\"piloto\":\"" + escape(entrada.piloto())
                        + "\",\"pontuacao\":" + entrada.pontuacao()
                        + ",\"dificuldade\":\"" + escape(entrada.dificuldade())
                        + "\",\"concluidaEm\":\"" + entrada.concluidaEm() + "\"}")
                .collect(java.util.stream.Collectors.joining(",\n", "[\n", "\n]\n"));
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Files.writeString(arquivo, conteudo, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível gravar o ranking em " + arquivo, exception);
        }
    }

    @Override
    public void limpar() {
        salvar(List.of());
    }

    private String escape(String texto) {
        StringBuilder escapado = new StringBuilder();
        for (char caractere : texto.toCharArray()) {
            switch (caractere) {
                case '"' -> escapado.append("\\\"");
                case '\\' -> escapado.append("\\\\");
                case '\b' -> escapado.append("\\b");
                case '\f' -> escapado.append("\\f");
                case '\n' -> escapado.append("\\n");
                case '\r' -> escapado.append("\\r");
                case '\t' -> escapado.append("\\t");
                default -> {
                    if (caractere < 0x20) {
                        escapado.append(String.format("\\u%04x", (int) caractere));
                    } else {
                        escapado.append(caractere);
                    }
                }
            }
        }
        return escapado.toString();
    }

    private String unescape(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int indice = 0; indice < texto.length(); indice++) {
            char caractere = texto.charAt(indice);
            if (caractere == '\\') {
                if (indice + 1 == texto.length()) {
                    throw new IllegalStateException("Sequência de escape incompleta no ranking.");
                }
                char escape = texto.charAt(++indice);
                switch (escape) {
                    case '"' -> resultado.append('"');
                    case '\\' -> resultado.append('\\');
                    case '/' -> resultado.append('/');
                    case 'n' -> resultado.append('\n');
                    case 'r' -> resultado.append('\r');
                    case 't' -> resultado.append('\t');
                    case 'b' -> resultado.append('\b');
                    case 'f' -> resultado.append('\f');
                    case 'u' -> {
                        if (indice + 4 >= texto.length()) {
                            throw new IllegalStateException("Escape Unicode incompleto no ranking.");
                        }
                        try {
                            resultado.append((char) Integer.parseInt(texto.substring(indice + 1, indice + 5), 16));
                        } catch (NumberFormatException exception) {
                            throw new IllegalStateException("Escape Unicode inválido no ranking.", exception);
                        }
                        indice += 4;
                    }
                    default -> throw new IllegalStateException("Escape inválido no ranking.");
                }
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }
}
