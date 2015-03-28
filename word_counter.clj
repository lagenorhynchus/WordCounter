(ns word-counter
  (:require [clojure.string :refer [lower-case split]]))

;; 単語の出現回数
(defrecord WordFreq [word count]
  Object
  (toString [this]
    (str "(" (:word this) " " (:count this) ")"))

  Comparable
  ;; カウントの降順、単語の昇順
  (compareTo [this other]
    (if (not= (:count this) (:count other))
      (compare (:count other) (:count this))
      (compare (:word this) (:word other)))))

(defn- to-wordfreq [[w ws]]
  (->WordFreq w (count ws)))

(defn- word-elem? [c]
  (not ((set ",.;:?!/'\"()[]") c)))

;; 文字列を単語のリストに編集する。
(defn- word-list [s]
  (split (->> s (filter word-elem?) (apply str) lower-case) #"\s+"))

(defn count-words
  "欧文の文字列に含まれる単語の出現回数をカウントする。
  大文字と小文字は区別せず、記号類は除外する。"
  [s]
  (->> s word-list (group-by identity) (map to-wordfreq) sort))

(if (not= (count *command-line-args*) 2)
  (println "Please specify an input file.")
  (let [[_ f] *command-line-args*]
    (println (count-words (slurp f)))))
