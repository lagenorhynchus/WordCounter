import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCounter {
  /**
   * 単語の出現回数
   */
  public static class WordFreq implements Comparable<WordFreq> {
    private String word;
    private int count;

    public WordFreq(String word, int count) {
      this.word = word;
      this.count = count;
    }

    public String getWord() {
      return this.word;
    }

    public int getCount() {
      return this.count;
    }

    @Override
    public String toString() {
      return String.format("(%s, %d)", this.getWord(), this.getCount());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof WordFreq) {
        WordFreq other = (WordFreq) obj;
        return this.getWord().equals(other.getWord())
          && this.getCount() == other.getCount();
      }
      return false;
    }

    // カウントの降順、単語の昇順
    @Override
    public int compareTo(WordFreq other) {
      if (this.getCount() != other.getCount()) {
        return other.getCount() - this.getCount();
      } else {
        return this.getWord().compareTo(other.getWord());
      }
    }
  }

  // 文字列ストリームを単語のストリームに編集する。
  private static Stream<String> wordStream(final Stream<String> ss) {
    return ss.flatMap(line -> Stream.of(line
      .replaceAll("[,.;:?!/'\"()\\[\\]]", "")
      .toLowerCase()
      .split("\\s+")))
      .filter(word -> !word.isEmpty());
  }

  /**
   * 欧文の文字列に含まれる単語の出現回数をカウントする。
   * 大文字と小文字は区別せず、記号類は除外する。
   *
   * @param  ss カウント対象文字列ストリーム
   * @return    単語の出現回数リスト
   */
  public static List<WordFreq> countWords(final Stream<String> ss) {
    final Map<String, List<String>> wordMap = wordStream(ss)
      .collect(Collectors.groupingBy(Function.identity()));

    return wordMap.entrySet().stream()
      .map(entry -> new WordFreq(entry.getKey(), entry.getValue().size()))
      .sorted()
      .collect(Collectors.toList());
  }

  public static void main(final String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("Please specify an input file.");
    } else {
      System.out.println(countWords(Files.lines(Paths.get(args[0]))));
    }
  }
}
