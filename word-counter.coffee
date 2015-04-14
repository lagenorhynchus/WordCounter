"use strict"

fs = require("fs")
_ = require("underscore")

# 単語の出現回数
class WordFreq
  constructor: (@word, @count) ->

  # カウントの降順、単語の昇順
  @comparator: (wf1, wf2) ->
    if wf1.count isnt wf2.count
      wf2.count - wf1.count
    else
      wf1.word.localeCompare(wf2.word)

isWordElem = (c) ->
  c not in ",.;:?!/'\"()[]"

# 文字列を単語のリストに編集する。
wordList = (s) ->
  (word.toLowerCase() for word in _.filter(s, isWordElem).join("").split(/\s+/))

# 欧文の文字列に含まれる単語の出現回数をカウントする。
# 大文字と小文字は区別せず、記号類は除外する。
countWords = (s) ->
  _.chain(wordList(s))
    .groupBy(_.identity)
    .map((ws, w) -> new WordFreq(w, ws.length))
    .sort(WordFreq.comparator)
    .value()

WordCounter = {countWords: countWords}
module.exports = WordCounter

if process.argv.length isnt 3
  console.log "Please specify an input file."
else
  fs.readFile(process.argv[2], "utf-8", (err, data) ->
    if err
      throw err
    console.log WordCounter.countWords(data)
  )
