from functools import total_ordering
from itertools import groupby
import sys


@total_ordering
class WordFreq:
    """単語の出現回数"""
    def __init__(self, word, count):
        self.word = word
        self.count = count

    def __repr__(self):
        return 'WordFreq({0.word!r}, {0.count!r})'.format(self)

    def __str__(self):
        return '({0.word!s}, {0.count!s})'.format(self)

    def __eq__(self, other):
        return self.word == other.word and self.count == other.count

    # カウントの降順、単語の昇順
    def __lt__(self, other):
        if self.count != other.count:
            return other.count < self.count
        else:
            return self.word < other.word


def _is_word_elem(c):
    return c not in ',.;:?!/\'"()[]'


# 文字列を単語のリストに編集する。
def _word_list(s):
    return [word.lower() for word in ''.join(filter(_is_word_elem, s)).split()]


def count_words(s):
    """欧文の文字列に含まれる単語の出現回数をカウントする。
    大文字と小文字は区別せず、記号類は除外する。

    Args:
        s: カウント対象文字列
    Returns:
        単語の出現回数リスト
    """
    return sorted(WordFreq(key, len(list(group)))
                  for key, group in groupby(sorted(_word_list(s))))


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('Please specify an input file.')
    else:
        with open(sys.argv[1], encoding='utf-8') as file:
            print(count_words(file.read()))
